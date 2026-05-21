# 🎓 EduManager — Hệ thống Quản lý Sinh Viên

> **Student Management System** — Ứng dụng web quản lý sinh viên với Spring Boot REST API, xác thực JWT, đăng nhập OAuth2 (Google/Facebook), phân quyền ADMIN/USER và kết nối SQL Server.

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?style=flat-square&logo=springboot)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk)
![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=flat-square&logo=jsonwebtokens)
![OAuth2](https://img.shields.io/badge/OAuth2-Google%20%7C%20Facebook-4285F4?style=flat-square&logo=google)
![SQL Server](https://img.shields.io/badge/Database-SQL%20Server-0078D6?style=flat-square&logo=microsoft-sql-server)

---

## 📌 Mục lục

1. [Giới thiệu](#giới-thiệu)
2. [Chức năng](#chức-năng)
3. [Cấu trúc thư mục](#cấu-trúc-thư-mục)
4. [Yêu cầu môi trường](#yêu-cầu-môi-trường)
5. [Cài đặt và chạy](#cài-đặt-và-chạy)
6. [Tài khoản mặc định](#tài-khoản-mặc-định)
7. [REST API](#rest-api)
8. [Cấu hình](#cấu-hình)
9. [Lưu ý](#lưu-ý)

---

## Giới thiệu

EduManager là ứng dụng web quản lý sinh viên, xây dựng theo kiến trúc **RESTful API**. Backend sử dụng Spring Boot, frontend sử dụng HTML/CSS/JavaScript thuần, kết nối SQL Server.

### Dùng để làm gì?

- Quản lý sinh viên (thêm, sửa, xóa, tìm kiếm)
- Xác thực người dùng bằng JWT và OAuth2
- Phân quyền truy cập theo vai trò ADMIN / USER
- Chạy cùng SQL Server với schema và dữ liệu mẫu

### Công nghệ sử dụng

| Nhóm | Công nghệ | Phiên bản |
|---|---|---|
| Backend | Spring Boot | 3.2.0 |
| Ngôn ngữ | Java | 17 |
| Bảo mật | Spring Security + JWT (jjwt) | 0.12.3 |
| OAuth2 | Spring OAuth2 Client | — |
| Database | SQL Server | — |
| ORM | Spring Data JPA + Hibernate | — |
| Email | Spring Mail + Gmail SMTP | — |
| Frontend | HTML + CSS + JavaScript | — |

---

## Chức năng

### 🔐 Xác thực người dùng
- Đăng ký tài khoản (username, email, password)
- Đăng nhập bằng email/password → nhận JWT token
- Đăng nhập bằng **Google OAuth2**
- Đăng nhập bằng **Facebook OAuth2**
- Quên mật khẩu → nhận email chứa link reset
- Đặt lại mật khẩu bằng token
- Đăng xuất

### 📋 Quản lý sinh viên (CRUD)
- Xem danh sách sinh viên
- Tìm kiếm theo họ tên hoặc email
- Thêm sinh viên mới — yêu cầu đăng nhập
- Cập nhật thông tin — yêu cầu đăng nhập
- Xóa sinh viên — chỉ **ADMIN**

### ⚙️ Trang Admin (chỉ ADMIN)
- Dashboard thống kê
- Quản lý người dùng
- Thay đổi role (USER ↔ ADMIN)
- Khóa / mở khóa tài khoản
- Reset mật khẩu người dùng
- Xóa người dùng

### 🛡️ Phân quyền (RBAC)

| Chức năng | USER | ADMIN |
|---|:---:|:---:|
| Xem danh sách sinh viên | ✅ | ✅ |
| Thêm / Sửa sinh viên | ✅ | ✅ |
| Xóa sinh viên | ❌ | ✅ |
| Truy cập trang Admin | ❌ | ✅ |
| Quản lý người dùng | ❌ | ✅ |
| Thay đổi role / Khóa tài khoản | ❌ | ✅ |

---

## Cấu trúc thư mục

`
sinhvien-api/
├── pom.xml
├── database/
│   └── Database_QLSinhvien_API.sql      ← Script tạo database SQL Server đầy đủ
└── src/main/
    ├── java/com/example/sinhvien_api/
    │   ├── config/
    │   │   ├── SecurityConfig.java
    │   │   ├── WebMvcConfig.java
    │   │   └── DataInitializer.java
    │   ├── controller/
    │   │   ├── AuthController.java
    │   │   ├── SinhVienController.java
    │   │   └── AdminController.java
    │   ├── dto/
    │   │   └── AuthDto.java
    │   ├── model/
    │   │   ├── User.java
    │   │   ├── SinhVien.java
    │   │   └── PasswordResetToken.java
    │   ├── repository/
    │   │   ├── UserRepository.java
    │   │   ├── SinhVienRepository.java
    │   │   └── PasswordResetTokenRepository.java
    │   ├── security/
    │   │   ├── JwtUtil.java
    │   │   ├── JwtAuthFilter.java
    │   │   └── OAuth2SuccessHandler.java
    │   ├── service/
    │   │   ├── AuthService.java
    │   │   └── SinhVienService.java
    │   └── SinhVienApiApplication.java
    └── resources/
        ├── application.properties
        ├── schema.sql
        └── static/
            ├── login.html
            ├── sinhvien.html
            └── admin.html
`

---

## Yêu cầu môi trường

| Phần mềm | Phiên bản | Kiểm tra |
|---|---|---|
| Java JDK | 17+ | java -version |
| Maven | 3.8+ | mvn -version |
| SQL Server | 2017+ | Kết nối DB |
| VS Code | Mới nhất | Extension Pack for Java |
| Trình duyệt | Chrome / Edge / Firefox | Hỗ trợ localStorage |

---

## Cài đặt và chạy

### 1. Mở project

`ash
cd sinhvien-api
`

### 2. Chuẩn bị SQL Server

- Mở SQL Server Management Studio hoặc công cụ tương tự.
- Tạo database QLSinhVien hoặc để ứng dụng tự tạo schema.
- Nếu muốn, chạy script database/Database_QLSinhvien_API.sql.

### 3. Cấu hình application.properties

`properties
spring.datasource.url=jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=QLSinhVien;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=Truong@01102005
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.continue-on-error=true

app.jwt.secret=YourSuperSecretKey1234567890_change_in_production
app.jwt.expiration-ms=86400000
app.jwt.reset-expiration-ms=900000

spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=email,profile
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/api/auth/oauth2/callback/google

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=youremail@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

server.port=8080
app.frontend-url=http://localhost:8080
`

> **Lưu ý khi deploy trên Render**
>
> Render có thể gán biến môi trường `SPRING_DATASOURCE_URL` mặc định cho PostgreSQL nếu bạn dùng database add-on Postgres. Ứng dụng của bạn đang cố gắng sử dụng SQL Server, nên không nên dùng biến `SPRING_DATASOURCE_*` chung của Render.
>
> Nếu cần thay đổi chuỗi kết nối SQL Server trên môi trường deploy, hãy dùng:
>
> ```properties
> APP_DATASOURCE_URL=jdbc:sqlserver://your-sql-server-host:1433;databaseName=QLSinhVien;encrypt=true;trustServerCertificate=true
> APP_DATASOURCE_USERNAME=sa
> APP_DATASOURCE_PASSWORD=YourPassword
> ```

### 4. Chạy ứng dụng

`ash
mvn spring-boot:run
`

### 5. Truy cập

- http://localhost:8080/login.html
- http://localhost:8080/sinhvien.html
- http://localhost:8080/admin.html

---

## Tài khoản mặc định

| Vai trò | Email | Mật khẩu |
|---|---|---|
| Quản trị viên | dmin@gmail.com | dmin@123 |
| Người dùng | user@demo.com | User@123 |

---

## REST API

### Auth — /api/auth/**

| Method | Endpoint | Mô tả |
|---|---|---|
| POST | /api/auth/register | Đăng ký tài khoản mới |
| POST | /api/auth/login | Đăng nhập → nhận JWT |
| POST | /api/auth/forgot-password | Gửi email reset mật khẩu |
| POST | /api/auth/reset-password | Đặt lại mật khẩu |
| GET | /api/auth/me | Thông tin user hiện tại |
| GET | /api/auth/oauth2/authorize/google | Bắt đầu đăng nhập Google |

### Sinh Viên — /api/sinhvien/**

| Method | Endpoint | Yêu cầu | Mô tả |
|---|---|---|---|
| GET | /api/sinhvien | Public | Lấy tất cả sinh viên |
| GET | /api/sinhvien/{id} | Public | Lấy sinh viên theo ID |
| GET | /api/sinhvien/search?keyword= | Public | Tìm kiếm sinh viên |
| POST | /api/sinhvien | 🔒 Login | Thêm sinh viên mới |
| PUT | /api/sinhvien/{id} | 🔒 Login | Cập nhật sinh viên |
| DELETE | /api/sinhvien/{id} | 👑 ADMIN | Xóa sinh viên |

### Admin — /api/admin/**

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | /api/admin/stats | Thống kê dashboard |
| GET | /api/admin/users | Danh sách người dùng |
| PUT | /api/admin/users/{id}/role | Thay đổi role |
| PUT | /api/admin/users/{id}/toggle | Khóa / mở khóa tài khoản |
| PUT | /api/admin/users/{id}/reset-password | Reset mật khẩu |
| DELETE | /api/admin/users/{id} | Xóa người dùng |

---

## Lưu ý

- Ứng dụng hiện sử dụng SQL Server.
- File database/Database_QLSinhvien_API.sql chứa schema và dữ liệu mẫu đầy đủ.
- schema.sql trong src/main/resources được dùng để tạo bảng khi ứng dụng khởi động.
- spring.sql.init.continue-on-error=true giúp bỏ qua lỗi khi bảng đã tồn tại.

---

## License

MIT
