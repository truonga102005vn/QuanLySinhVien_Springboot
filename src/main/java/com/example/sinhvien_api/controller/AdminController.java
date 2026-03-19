package com.example.sinhvien_api.controller;

import com.example.sinhvien_api.model.User;
import com.example.sinhvien_api.repository.SinhVienRepository;
import com.example.sinhvien_api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepo;
    private final SinhVienRepository svRepo;
    private final PasswordEncoder encoder;

    public AdminController(UserRepository userRepo,
                           SinhVienRepository svRepo,
                           PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.svRepo   = svRepo;
        this.encoder  = encoder;
    }

    /** GET /api/admin/stats */
    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        long totalUsers = userRepo.count();
        long totalSv    = svRepo.count();
        long adminCount = userRepo.findAll().stream()
                .filter(u -> u.getRole() == User.Role.ADMIN).count();
        return ResponseEntity.ok(Map.of(
            "totalUsers",    totalUsers,
            "totalSinhVien", totalSv,
            "adminCount",    adminCount,
            "userCount",     totalUsers - adminCount
        ));
    }

    /** GET /api/admin/users */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<Map<String, Object>> result = userRepo.findAll().stream().map(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",        u.getId());
            m.put("username",  u.getUsername());
            m.put("email",     u.getEmail());
            m.put("fullName",  u.getFullName());
            m.put("role",      u.getRole().name());
            m.put("provider",  u.getProvider().name());
            m.put("enabled",   u.getEnabled());
            m.put("avatarUrl", u.getAvatarUrl());
            m.put("createdAt", u.getCreatedAt().toString());
            m.put("svCount",   svRepo.countByCreatedById(u.getId()));
            return m;
        }).toList();
        return ResponseEntity.ok(result);
    }

    /** PUT /api/admin/users/{id}/role */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> changeRole(@PathVariable Integer id,
                                        @RequestBody Map<String, String> body) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User không tồn tại"));
        try {
            user.setRole(User.Role.valueOf(body.get("role").toUpperCase()));
            userRepo.save(user);
            return ResponseEntity.ok(Map.of("message", "Cập nhật role thành công"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Role không hợp lệ"));
        }
    }

    /** PUT /api/admin/users/{id}/toggle */
    @PutMapping("/users/{id}/toggle")
    public ResponseEntity<?> toggleEnabled(@PathVariable Integer id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User không tồn tại"));
        user.setEnabled(!user.getEnabled());
        userRepo.save(user);
        String status = user.getEnabled() ? "kích hoạt" : "khóa";
        return ResponseEntity.ok(Map.of(
            "message", "Đã " + status + " tài khoản",
            "enabled", user.getEnabled()
        ));
    }

    /** DELETE /api/admin/users/{id} */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        if (!userRepo.existsById(id))
            return ResponseEntity.status(404).body(Map.of("error", "User không tồn tại"));
        userRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa user thành công"));
    }

    /** PUT /api/admin/users/{id}/reset-password */
    @PutMapping("/users/{id}/reset-password")
    public ResponseEntity<?> adminResetPassword(@PathVariable Integer id,
                                                @RequestBody Map<String, String> body) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User không tồn tại"));
        String newPass = body.getOrDefault("password", "Admin@123");
        user.setPassword(encoder.encode(newPass));
        userRepo.save(user);
        return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công"));
    }
}