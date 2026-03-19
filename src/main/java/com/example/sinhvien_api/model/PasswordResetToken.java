package com.example.sinhvien_api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_password_reset")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean used = false;

    // Constructors
    public PasswordResetToken() {}

    public PasswordResetToken(User user, String token, LocalDateTime expiresAt) {
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
        this.used = false;
    }

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Boolean getUsed() { return used; }
    public void setUsed(Boolean used) { this.used = used; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private User user;
        private String token;
        private LocalDateTime expiresAt;
        private Boolean used = false;

        public Builder user(User user) { this.user = user; return this; }
        public Builder token(String token) { this.token = token; return this; }
        public Builder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder used(Boolean used) { this.used = used; return this; }

        public PasswordResetToken build() {
            PasswordResetToken token = new PasswordResetToken();
            token.user = this.user;
            token.token = this.token;
            token.expiresAt = this.expiresAt;
            token.used = this.used;
            return token;
        }
    }
}