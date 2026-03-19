# Giai đoạn 1: Build ứng dụng
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Giai đoạn 2: Chạy ứng dụng (Dùng Temurin thay cho OpenJDK cũ)
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=build /app/target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]