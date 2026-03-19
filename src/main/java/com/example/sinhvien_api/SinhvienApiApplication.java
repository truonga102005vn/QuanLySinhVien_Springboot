package com.example.sinhvien_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SinhvienApiApplication {

    public static void main(String[] args) {
        // // Tạo BCrypt hash cho mật khẩu mới — xóa sau khi copy hash
        // var encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        // System.out.println("=== HASH cho admin@123 ===");
        // System.out.println(encoder.encode("admin@123"));
        // System.out.println("========================");
        SpringApplication.run(SinhvienApiApplication.class, args);
    }
}