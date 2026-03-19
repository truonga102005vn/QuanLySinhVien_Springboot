package com.example.sinhvien_api.controller;

import com.example.sinhvien_api.dto.AuthDto.*;
import com.example.sinhvien_api.model.User;
import com.example.sinhvien_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/auth/register */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error(e.getMessage()));
        }
    }

    /** POST /api/auth/login */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            return ResponseEntity.ok(authService.login(req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error(e.getMessage()));
        }
    }

    /** POST /api/auth/forgot-password */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        authService.forgotPassword(req);
        return ResponseEntity.ok(ok("Nếu email tồn tại, link đặt lại mật khẩu đã được gửi"));
    }

    /** POST /api/auth/reset-password */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        try {
            authService.resetPassword(req);
            return ResponseEntity.ok(ok("Đặt lại mật khẩu thành công"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        }
    }

    /** GET /api/auth/me */
    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        if (principal == null)
            return ResponseEntity.status(401).body(error("Chưa đăng nhập"));

        User user = authService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(Map.of(
            "id",        user.getId(),
            "email",     user.getEmail(),
            "fullName",  user.getFullName()  != null ? user.getFullName()  : "",
            "username",  user.getUsername(),
            "role",      user.getRole().name(),
            "provider",  user.getProvider().name(),
            "avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "",
            "createdAt", user.getCreatedAt().toString()
        ));
    }

    /** GET /api/auth/oauth2/url/{provider} */
    @GetMapping("/oauth2/url/{provider}")
    public ResponseEntity<?> getOAuthUrl(@PathVariable String provider) {
        return ResponseEntity.ok(Map.of("url", "/api/auth/oauth2/authorize/" + provider));
    }

    private Map<String, String> error(String msg) { return Map.of("error", msg); }
    private Map<String, String> ok(String msg)    { return Map.of("message", msg); }
}