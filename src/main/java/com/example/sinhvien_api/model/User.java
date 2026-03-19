package com.example.sinhvien_api.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // Không trả password ra JSON — tránh lộ mật khẩu
    @JsonIgnore
    @Column(length = 255)
    private String password;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AuthProvider provider = AuthProvider.LOCAL;

    @Column(name = "provider_id", length = 200)
    private String providerId;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters ─────────────────────────────────────────────
    public Integer       getId()         { return id; }
    public String        getUsername()   { return username; }
    public String        getEmail()      { return email; }
    public String        getPassword()   { return password; }
    public String        getFullName()   { return fullName; }
    public Role          getRole()       { return role; }
    public AuthProvider  getProvider()   { return provider; }
    public Boolean       getEnabled()    { return enabled; }
    public String        getAvatarUrl()  { return avatarUrl; }
    public String        getProviderId() { return providerId; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public LocalDateTime getUpdatedAt()  { return updatedAt; }

    // ── Setters ─────────────────────────────────────────────
    public void setUsername(String username)    { this.username   = username; }
    public void setEmail(String email)          { this.email      = email; }
    public void setPassword(String password)    { this.password   = password; }
    public void setFullName(String fullName)    { this.fullName   = fullName; }
    public void setRole(Role role)              { this.role       = role; }
    public void setProvider(AuthProvider p)     { this.provider   = p; }
    public void setEnabled(Boolean enabled)     { this.enabled    = enabled; }
    public void setProviderId(String p)         { this.providerId = p; }
    public void setAvatarUrl(String avatarUrl)  { this.avatarUrl  = avatarUrl; }
    public void setCreatedAt(LocalDateTime v)   { this.createdAt  = v; }
    public void setUpdatedAt(LocalDateTime v)   { this.updatedAt  = v; }

    // ── Builder ─────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Integer      id;
        private String       username;
        private String       email;
        private String       password;
        private String       fullName;
        private String       avatarUrl;
        private Role         role     = Role.USER;
        private AuthProvider provider = AuthProvider.LOCAL;
        private String       providerId;
        private Boolean      enabled  = true;

        public Builder id(Integer v)          { this.id         = v; return this; }
        public Builder username(String v)     { this.username   = v; return this; }
        public Builder email(String v)        { this.email      = v; return this; }
        public Builder password(String v)     { this.password   = v; return this; }
        public Builder fullName(String v)     { this.fullName   = v; return this; }
        public Builder avatarUrl(String v)    { this.avatarUrl  = v; return this; }
        public Builder role(Role v)           { this.role       = v; return this; }
        public Builder provider(AuthProvider v){ this.provider  = v; return this; }
        public Builder providerId(String v)   { this.providerId = v; return this; }
        public Builder enabled(Boolean v)     { this.enabled    = v; return this; }

        public User build() {
            User user = new User();
            user.id         = this.id;
            user.username   = this.username;
            user.email      = this.email;
            user.password   = this.password;
            user.fullName   = this.fullName;
            user.avatarUrl  = this.avatarUrl;
            user.role       = this.role;
            user.provider   = this.provider;
            user.providerId = this.providerId;
            user.enabled    = this.enabled;
            return user;
        }
    }

    // ── Enums ────────────────────────────────────────────────
    public enum Role { USER, ADMIN }
    public enum AuthProvider { LOCAL, GOOGLE, FACEBOOK }
}