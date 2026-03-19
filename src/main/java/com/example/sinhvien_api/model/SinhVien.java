package com.example.sinhvien_api.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tbl_sinhvien")
public class SinhVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 150)
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // FIX 1: LAZY → EAGER để tránh LazyInitializationException khi serialize JSON
    // FIX 2: @JsonIgnoreProperties để bỏ qua các field nhạy cảm của User
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by")
    @JsonIgnoreProperties({"password", "enabled", "provider", "providerId",
                            "createdAt", "updatedAt", "hibernateLazyInitializer"})
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ── Constructors ────────────────────────────────────────
    public SinhVien() {}

    public SinhVien(String hoTen, String email, User createdBy) {
        this.hoTen     = hoTen;
        this.email     = email;
        this.createdBy = createdBy;
    }

    // ── Getters ─────────────────────────────────────────────
    public Integer       getId()        { return id; }
    public String        getHoTen()     { return hoTen; }
    public String        getEmail()     { return email; }
    public User          getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ── Setters ─────────────────────────────────────────────
    public void setId(Integer id)              { this.id        = id; }
    public void setHoTen(String hoTen)         { this.hoTen     = hoTen; }
    public void setEmail(String email)         { this.email     = email; }
    public void setCreatedBy(User createdBy)   { this.createdBy = createdBy; }
    public void setCreatedAt(LocalDateTime v)  { this.createdAt = v; }
    public void setUpdatedAt(LocalDateTime v)  { this.updatedAt = v; }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}