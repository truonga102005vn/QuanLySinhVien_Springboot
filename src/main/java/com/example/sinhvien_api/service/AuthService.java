package com.example.sinhvien_api.service;

import com.example.sinhvien_api.dto.AuthDto.*;
import com.example.sinhvien_api.model.PasswordResetToken;
import com.example.sinhvien_api.model.User;
import com.example.sinhvien_api.repository.PasswordResetTokenRepository;
import com.example.sinhvien_api.repository.UserRepository;
import com.example.sinhvien_api.security.JwtUtil;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private static final Logger log = Logger.getLogger(AuthService.class.getName());

    private final UserRepository userRepo;
    private final PasswordResetTokenRepository resetTokenRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public AuthService(UserRepository userRepo, PasswordResetTokenRepository resetTokenRepo, PasswordEncoder encoder, JwtUtil jwtUtil, JavaMailSender mailSender) {
        this.userRepo = userRepo;
        this.resetTokenRepo = resetTokenRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.mailSender = mailSender;
    }

    // ── Đăng ký ──────────────────────────────────────────────
    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email đã được sử dụng");
        if (userRepo.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .role(User.Role.USER)
                .provider(User.AuthProvider.LOCAL)
                .enabled(true)
                .build();
        userRepo.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getFullName(),
                                user.getRole().name(), user.getAvatarUrl());
    }

    // ── Đăng nhập ────────────────────────────────────────────
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email hoặc mật khẩu không đúng"));

        if (!user.getEnabled())
            throw new IllegalArgumentException("Tài khoản bị khóa");

        if (user.getProvider() != User.AuthProvider.LOCAL)
            throw new IllegalArgumentException("Tài khoản này đăng nhập qua " + user.getProvider().name());

        if (!encoder.matches(req.getPassword(), user.getPassword()))
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng");

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getFullName(),
                                user.getRole().name(), user.getAvatarUrl());
    }

    // ── Quên mật khẩu (gửi email) ────────────────────────────
    public void forgotPassword(ForgotPasswordRequest req) {
        userRepo.findByEmail(req.getEmail()).ifPresent(user -> {
            // Xóa token cũ
            resetTokenRepo.deleteByUserId(user.getId());

            String rawToken = UUID.randomUUID().toString();
            PasswordResetToken prt = PasswordResetToken.builder()
                    .user(user)
                    .token(rawToken)
                    .expiresAt(LocalDateTime.now().plusMinutes(15))
                    .used(false)
                    .build();
            resetTokenRepo.save(prt);

            // Gửi email
            String link = frontendUrl + "/login.html?reset=" + rawToken;
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(user.getEmail());
            msg.setSubject("Đặt lại mật khẩu - Hệ thống Quản lý Sinh Viên");
            msg.setText(
                "Xin chào " + user.getFullName() + ",\n\n" +
                "Bạn vừa yêu cầu đặt lại mật khẩu. Click vào link sau (hết hạn sau 15 phút):\n\n" +
                link + "\n\n" +
                "Nếu bạn không yêu cầu, hãy bỏ qua email này.\n\n" +
                "Trân trọng,\nHệ thống Quản lý Sinh Viên"
            );
            try {
                mailSender.send(msg);
            } catch (Exception e) {
                log.warning("Không thể gửi email: " + e.getMessage());
            }
        });
        // Luôn trả về thành công để tránh user enumeration
    }

    // ── Reset mật khẩu ───────────────────────────────────────
    public void resetPassword(ResetPasswordRequest req) {
        PasswordResetToken prt = resetTokenRepo.findByToken(req.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ"));

        if (prt.isExpired())
            throw new IllegalArgumentException("Token đã hết hạn (15 phút)");
        if (prt.getUsed())
            throw new IllegalArgumentException("Token đã được sử dụng");

        User user = prt.getUser();
        user.setPassword(encoder.encode(req.getNewPassword()));
        userRepo.save(user);

        prt.setUsed(true);
        resetTokenRepo.save(prt);
    }

    // ── Lấy thông tin user hiện tại ──────────────────────────
    @Transactional(readOnly = true)
    public User getCurrentUser(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
    }
}