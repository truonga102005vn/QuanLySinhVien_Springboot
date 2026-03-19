# Bước 1: Build ứng dụng bằng Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
# Copy file pom.xml và source code vào container
COPY pom.xml .
COPY src ./src
# Build project và bỏ qua bước chạy test để deploy nhanh hơn
RUN mvn clean package -DskipTests

# Bước 2: Chạy ứng dụng với JRE nhẹ gọn
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy file .jar đã build từ Bước 1 sang
COPY --from=build /app/target/*.jar app.jar
# Mở port 8080 (port mặc định của Spring Boot)
EXPOSE 8080
# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]