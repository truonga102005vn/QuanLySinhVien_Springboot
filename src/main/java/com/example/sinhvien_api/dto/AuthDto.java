package com.example.sinhvien_api.dto;

import jakarta.validation.constraints.*;

public class AuthDto {

    public static class RegisterRequest {
        @NotBlank @Size(min = 3, max = 50)
        private String username;

        @NotBlank @Email
        private String email;

        @NotBlank @Size(min = 8, max = 100)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
                 message = "Mật khẩu phải có chữ hoa, chữ thường và số")
        private String password;

        @Size(max = 100)
        private String fullName;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }

    public static class LoginRequest {
        @NotBlank private String email;
        @NotBlank private String password;

        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class ForgotPasswordRequest {
        @NotBlank @Email
        private String email;

        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ResetPasswordRequest {
        @NotBlank private String token;

        @NotBlank @Size(min = 8, max = 100)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
                 message = "Mật khẩu phải có chữ hoa, chữ thường và số")
        private String newPassword;

        // Getters and setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class AuthResponse {
        private String token;
        private String email;
        private String fullName;
        private String role;
        private String avatarUrl;

        public AuthResponse(String token, String email, String fullName,
                            String role, String avatarUrl) {
            this.token     = token;
            this.email     = email;
            this.fullName  = fullName;
            this.role      = role;
            this.avatarUrl = avatarUrl;
        }

        // Getters
        public String getToken() { return token; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
        public String getRole() { return role; }
        public String getAvatarUrl() { return avatarUrl; }
    }
}