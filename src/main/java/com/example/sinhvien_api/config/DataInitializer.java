package com.example.sinhvien_api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.sinhvien_api.model.SinhVien;
import com.example.sinhvien_api.model.User;
import com.example.sinhvien_api.repository.SinhVienRepository;
import com.example.sinhvien_api.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepo,
                               SinhVienRepository svRepo,
                               PasswordEncoder encoder) {
        return args -> {

            // ── Tạo tài khoản ADMIN ─────────────────────────────────
            // Email: admin@gmail.com  |  Mật khẩu: admin@123
            if (userRepo.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(encoder.encode("admin@123")); // BCrypt tự động
                admin.setFullName("Quản Trị Viên");
                admin.setRole(User.Role.ADMIN);
                admin.setProvider(User.AuthProvider.LOCAL);
                admin.setEnabled(true);
                userRepo.save(admin);
                System.out.println("✅ Đã tạo tài khoản admin: admin@gmail.com / admin@123");
            }

            // ── Tạo tài khoản USER mẫu ──────────────────────────────
            // Email: user@demo.com  |  Mật khẩu: User@123
            if (userRepo.findByEmail("user@demo.com").isEmpty()) {
                User user = new User();
                user.setUsername("nguyenvan");
                user.setEmail("user@demo.com");
                user.setPassword(encoder.encode("User@123"));
                user.setFullName("Nguyễn Văn User");
                user.setRole(User.Role.USER);
                user.setProvider(User.AuthProvider.LOCAL);
                user.setEnabled(true);
                userRepo.save(user);
                System.out.println("✅ Đã tạo tài khoản user: user@demo.com / User@123");
            }

            // ── Tạo dữ liệu sinh viên mẫu ───────────────────────────
            if (svRepo.count() == 0) {
                User admin = userRepo.findByEmail("admin@gmail.com").orElse(null);

                String[][] sinhViens = {
                    {"Nguyễn Văn An",  "an.nguyen@sv.edu.vn"},
                    {"Trần Thị Bình",  "binh.tran@sv.edu.vn"},
                    {"Lê Minh Châu",   "chau.le@sv.edu.vn"},
                    {"Phạm Thị Dung",  "dung.pham@sv.edu.vn"},
                    {"Hoàng Văn Đức",  "duc.hoang@sv.edu.vn"},
                };

                for (String[] sv : sinhViens) {
                    SinhVien s = new SinhVien();
                    s.setHoTen(sv[0]);
                    s.setEmail(sv[1]);
                    s.setCreatedBy(admin);
                    svRepo.save(s);
                }
                System.out.println("✅ Đã tạo " + sinhViens.length + " sinh viên mẫu");
            }
        };
    }
}