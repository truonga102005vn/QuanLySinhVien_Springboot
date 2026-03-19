# 🎓 EduManager — Hệ thống Quản lý Sinh Viên

> **Student Management System** — Ứng dụng web quản lý sinh viên với Spring Boot REST API, xác thực JWT, đăng nhập OAuth2 (Google/Facebook), phân quyền ADMIN/USER và giao diện dark mode hiện đại.

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?style=flat-square&logo=springboot)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk)
![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=flat-square&logo=jsonwebtokens)
![OAuth2](https://img.shields.io/badge/OAuth2-Google%20%7C%20Facebook-4285F4?style=flat-square&logo=google)
![H2](https://img.shields.io/badge/Database-H2-1e2a42?style=flat-square)

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

EduManager là ứng dụng web fullstack quản lý sinh viên, xây dựng theo kiến trúc **RESTful API**. Backend sử dụng Spring Boot, frontend sử dụng HTML/CSS/JavaScript thuần không cần framework.

### Dùng để làm gì?

- Quản lý danh sách sinh viên (thêm, sửa, xóa, tìm kiếm)
- Xác thực người dùng bằng JWT và OAuth2
- Phân quyền truy cập theo vai trò ADMIN / USER
- Demo kiến trúc Spring Boot REST API + Security hoàn chỉnh

### Công nghệ sử dụng

| Nhóm | Công nghệ | Phiên bản |
|---|---|---|
| Backend | Spring Boot | 3.2.0 |
| Ngôn ngữ | Java | 17 |
| Bảo mật | Spring Security + JWT (jjwt) | 0.12.3 |
| OAuth2 | Spring OAuth2 Client | — |
| Database | H2 In-Memory | — |
| ORM | Spring Data JPA + Hibernate | — |
| Email | Spring Mail + Gmail SMTP | — |
| Frontend | HTML + CSS + JavaScript | — |
| Font | Sora (Google Fonts) | — |

---

## Chức năng

### 🔐 Xác thực người dùng
- Đăng ký tài khoản (username, email, password)
- Đăng nhập bằng email/password → nhận JWT token
- Đăng nhập bằng **Google OAuth2**
- Đăng nhập bằng **Facebook OAuth2**
- Quên mật khẩu → nhận email chứa link reset (hết hạn 15 phút)
- Đặt lại mật khẩu bằng token
- Đăng xuất

### 📋 Quản lý sinh viên (CRUD)
- Xem danh sách — **public**, không cần đăng nhập
- Tìm kiếm theo họ tên hoặc email (debounce 350ms)
- Thêm sinh viên mới — yêu cầu đăng nhập
- Cập nhật thông tin — yêu cầu đăng nhập
- Xóa sinh viên — chỉ **ADMIN**

### ⚙️ Trang Admin (chỉ ADMIN)
- Dashboard: thống kê tổng user, sinh viên, admin, user thường
- Danh sách người dùng, lọc theo role / provider
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

```
sinhvien-api/
├── pom.xml
└── src/main/
    ├── java/com/example/sinhvien_api/
    │   ├── config/
    │   │   ├── SecurityConfig.java         ← Spring Security + OAuth2
    │   │   ├── WebMvcConfig.java           ← URL redirect shortcuts
    │   │   └── DataInitializer.java        ← Tạo dữ liệu mẫu khi khởi động
    │   ├── controller/
    │   │   ├── AuthController.java         ← /api/auth/**
    │   │   ├── SinhVienController.java     ← /api/sinhvien/**
    │   │   └── AdminController.java        ← /api/admin/** (ADMIN only)
    │   ├── dto/
    │   │   └── AuthDto.java                ← Request/Response DTOs
    │   ├── model/
    │   │   ├── User.java                   ← Entity người dùng
    │   │   ├── SinhVien.java               ← Entity sinh viên
    │   │   └── PasswordResetToken.java     ← Entity token reset mật khẩu
    │   ├── repository/
    │   │   ├── UserRepository.java
    │   │   ├── SinhVienRepository.java
    │   │   └── PasswordResetTokenRepository.java
    │   ├── security/
    │   │   ├── JwtUtil.java                ← Tạo & xác thực JWT
    │   │   ├── JwtAuthFilter.java          ← Filter đọc Bearer token
    │   │   └── OAuth2SuccessHandler.java   ← Xử lý sau OAuth2 thành công
    │   ├── service/
    │   │   ├── AuthService.java            ← Logic xác thực
    │   │   └── SinhVienService.java        ← Logic CRUD sinh viên
    │   └── SinhVienApiApplication.java     ← Entry point
    └── resources/
        ├── application.properties          ← Cấu hình ứng dụng
        ├── schema.sql                      ← Tạo bảng H2
        └── static/
            ├── login.html                  ← Đăng nhập / Đăng ký
            ├── sinhvien.html               ← Trang chủ quản lý sinh viên
            └── admin.html                  ← Trang quản trị
```

---

## Yêu cầu môi trường

| Phần mềm | Phiên bản | Kiểm tra |
|---|---|---|
| Java JDK | 17+ | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| VS Code | Mới nhất | Extension Pack for Java |
| Trình duyệt | Chrome / Edge / Firefox | Hỗ trợ localStorage |

---

## Cài đặt và chạy

### Bước 1 — Mở project

```bash
cd sinhvien-api
```

Mở trong VS Code, đảm bảo đã cài **Extension Pack for Java**.

### Bước 2 — Cấu hình `application.properties`

```properties
# Database H2
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER
spring.datasource.username=sa
spring.datasource.password=your_password

# JWT
app.jwt.secret=YourSuperSecretKey1234567890_ChangeThis
app.jwt.expiration-ms=86400000

# OAuth2 Google (lấy từ Google Cloud Console)
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET

# Gmail SMTP (tạo App Password trong Google Account)
spring.mail.username=youremail@gmail.com
spring.mail.password=your_app_password

# Frontend URL
app.frontend-url=http://localhost:8080
```

### Bước 3 — Chạy ứng dụng

```bash
mvn spring-boot:run
```

Khi terminal hiện ra các dòng sau, server đã sẵn sàng:

```
✅ Đã tạo tài khoản admin: admin@gmail.com / admin@123
✅ Đã tạo tài khoản user: user@demo.com / User@123
✅ Đã tạo 5 sinh viên mẫu
```

### Bước 4 — Truy cập

```
http://localhost:8080              → Trang chủ (sinhvien.html)
http://localhost:8080/login.html   → Đăng nhập
http://localhost:8080/admin.html   → Trang Admin
http://localhost:8080/h2-console   → H2 Database Console
```

---

## Tài khoản mặc định

| Vai trò | Email | Mật khẩu | Quyền |
|---|---|---|---|
| Quản trị viên | `admin@gmail.com` | `admin@123` | ADMIN — Toàn quyền |
| Người dùng | `user@demo.com` | `User@123` | USER — Xem & CRUD |

> ⚠️ **Lưu ý:** H2 là in-memory database — dữ liệu mất khi tắt server. `DataInitializer` tự tạo lại dữ liệu mẫu mỗi lần khởi động.

---

## REST API

### Auth — `/api/auth/**`

| Method | Endpoint | Mô tả |
|---|---|---|
| `POST` | `/api/auth/register` | Đăng ký tài khoản mới |
| `POST` | `/api/auth/login` | Đăng nhập → nhận JWT |
| `POST` | `/api/auth/forgot-password` | Gửi email reset mật khẩu |
| `POST` | `/api/auth/reset-password` | Đặt lại mật khẩu bằng token |
| `GET` | `/api/auth/me` | Thông tin user hiện tại |
| `GET` | `/api/auth/oauth2/authorize/google` | Bắt đầu đăng nhập Google |
| `GET` | `/api/auth/oauth2/authorize/facebook` | Bắt đầu đăng nhập Facebook |

### Sinh Viên — `/api/sinhvien/**`

| Method | Endpoint | Yêu cầu | Mô tả |
|---|---|---|---|
| `GET` | `/api/sinhvien` | Public | Lấy tất cả sinh viên |
| `GET` | `/api/sinhvien/{id}` | Public | Lấy sinh viên theo ID |
| `GET` | `/api/sinhvien/search?keyword=` | Public | Tìm kiếm sinh viên |
| `POST` | `/api/sinhvien` | 🔒 Login | Thêm sinh viên mới |
| `PUT` | `/api/sinhvien/{id}` | 🔒 Login | Cập nhật sinh viên |
| `DELETE` | `/api/sinhvien/{id}` | 👑 ADMIN | Xóa sinh viên |

### Admin — `/api/admin/**` *(chỉ ADMIN)*

| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/api/admin/stats` | Thống kê dashboard |
| `GET` | `/api/admin/users` | Danh sách người dùng |
| `PUT` | `/api/admin/users/{id}/role` | Thay đổi role |
| `PUT` | `/api/admin/users/{id}/toggle` | Khóa / mở khóa tài khoản |
| `PUT` | `/api/admin/users/{id}/reset-password` | Reset mật khẩu |
| `DELETE` | `/api/admin/users/{id}` | Xóa người dùng |

### Ví dụ request

```bash
# Đăng nhập
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@gmail.com","password":"admin@123"}'

# Lấy danh sách sinh viên (public)
curl http://localhost:8080/api/sinhvien

# Thêm sinh viên (cần token)
curl -X POST http://localhost:8080/api/sinhvien \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"hoTen":"Nguyễn Văn A","email":"a@sv.edu.vn"}'
```

---

## Cấu hình

### OAuth2 Google

1. Vào [Google Cloud Console](https://console.cloud.google.com)
2. Tạo project → Enable **Google People API**
3. Credentials → Create OAuth client ID → Web application
4. Authorized redirect URI: `http://localhost:8080/api/auth/oauth2/callback/google`
5. Copy Client ID + Secret → paste vào `application.properties`

### OAuth2 Facebook

1. Vào [Facebook Developers](https://developers.facebook.com)
2. Create App → Add **Facebook Login** product
3. Valid OAuth Redirect URI: `http://localhost:8080/api/auth/oauth2/callback/facebook`
4. Copy App ID + Secret → paste vào `application.properties`

### Gmail SMTP

1. Google Account → Security → 2-Step Verification → **App passwords**
2. Tạo App Password cho **Mail**
3. Dùng password đó trong `spring.mail.password`

### Dùng H2 file-based (lưu dữ liệu vĩnh viễn)

```properties
spring.datasource.url=jdbc:h2:file:./data/qlsinhvien;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER
```

---

## Lưu ý

- 🔑 JWT token hết hạn sau **24 giờ** — người dùng cần đăng nhập lại
- 💾 H2 in-memory: dữ liệu **mất khi restart** — dùng H2 file-based hoặc SQL Server cho production
- 🔒 Thay `app.jwt.secret` bằng chuỗi ngẫu nhiên dài trước khi deploy
- 📧 Gmail SMTP cần bật 2FA và tạo App Password riêng
- 🌐 Facebook OAuth2 cần HTTPS + app review để dùng public
- 🗄️ Đổi sang **SQL Server / MySQL / PostgreSQL** khi deploy production

---

*EduManager — Built with ❤️ using Spring Boot + JWT + OAuth2*
