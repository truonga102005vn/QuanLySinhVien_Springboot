# Giai đoạn 1: Build ứng dụng
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Giai đoạn 2: Chạy ứng dụng (Dùng bản slim để ổn định hơn)
FROM openjdk:17-jdk-slim
WORKDIR /app
# Lấy file jar từ bước build
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]