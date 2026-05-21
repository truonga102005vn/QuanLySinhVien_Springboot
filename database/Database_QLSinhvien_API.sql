-- ════════════════════════════════════════════════════════════════
-- DATABASE: QLSinhVien  — RESET & TẠO MỚI HOÀN TOÀN
-- Bao gồm: tbl_users, tbl_password_reset, tbl_sinhvien,
--           tbl_khoa, tbl_lophoc, tbl_monhoc, tbl_diem
-- ════════════════════════════════════════════════════════════════

-- ════════════════════════════════════════════
-- BƯỚC 1: Xóa database cũ và tạo lại
-- ════════════════════════════════════════════
USE master;
GO

-- Ngắt tất cả kết nối đang dùng database
IF EXISTS (SELECT name FROM sys.databases WHERE name = 'QLSinhVien')
BEGIN
    ALTER DATABASE QLSinhVien SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QLSinhVien;
    PRINT 'Da xoa database QLSinhVien cu';
END
GO

CREATE DATABASE QLSinhVien;
PRINT 'Da tao database QLSinhVien moi';
GO

USE QLSinhVien;
GO

-- ════════════════════════════════════════════
-- BƯỚC 2: tbl_users
-- ════════════════════════════════════════════
CREATE TABLE tbl_users (
    id          BIGINT        IDENTITY(1,1) PRIMARY KEY,
    username    NVARCHAR(50)  NOT NULL,
    email       NVARCHAR(150) NOT NULL,
    password    NVARCHAR(255) NULL,
    full_name   NVARCHAR(100) NULL,
    avatar_url  NVARCHAR(500) NULL,
    role        NVARCHAR(20)  NOT NULL DEFAULT 'USER',
    provider    NVARCHAR(20)  NOT NULL DEFAULT 'LOCAL',
    provider_id NVARCHAR(200) NULL,
    enabled     BIT           NOT NULL DEFAULT 1,
    created_at  DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at  DATETIME2     NOT NULL DEFAULT GETDATE(),

    CONSTRAINT UQ_users_username UNIQUE (username),
    CONSTRAINT UQ_users_email    UNIQUE (email)
);
PRINT 'Tao bang tbl_users thanh cong';
GO

-- ════════════════════════════════════════════
-- BƯỚC 3: tbl_password_reset
-- ════════════════════════════════════════════
CREATE TABLE tbl_password_reset (
    id         BIGINT        IDENTITY(1,1) PRIMARY KEY,
    user_id    BIGINT        NOT NULL,
    token      NVARCHAR(500) NOT NULL,
    expires_at DATETIME2     NOT NULL,
    used       BIT           NOT NULL DEFAULT 0,

    CONSTRAINT UQ_password_reset_token UNIQUE (token),
    CONSTRAINT FK_password_reset_user  FOREIGN KEY (user_id)
        REFERENCES tbl_users(id) ON DELETE CASCADE
);
PRINT 'Tao bang tbl_password_reset thanh cong';
GO

-- ════════════════════════════════════════════
-- BƯỚC 4: tbl_khoa  (Khoa / Ngành)
-- ════════════════════════════════════════════
CREATE TABLE tbl_khoa (
    id          BIGINT        IDENTITY(1,1) PRIMARY KEY,
    ma_khoa     NVARCHAR(20)  NOT NULL,
    ten_khoa    NVARCHAR(150) NOT NULL,
    mo_ta       NVARCHAR(500) NULL,
    truong_khoa NVARCHAR(100) NULL,
    is_active   BIT           NOT NULL DEFAULT 1,
    created_by  BIGINT        NULL,
    created_at  DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at  DATETIME2     NOT NULL DEFAULT GETDATE(),

    CONSTRAINT UQ_khoa_ma         UNIQUE (ma_khoa),
    CONSTRAINT FK_khoa_created_by FOREIGN KEY (created_by)
        REFERENCES tbl_users(id) ON DELETE SET NULL
);
PRINT 'Tao bang tbl_khoa thanh cong';
GO

-- ════════════════════════════════════════════
-- BƯỚC 5: tbl_lophoc  (Lớp học)
-- ════════════════════════════════════════════
CREATE TABLE tbl_lophoc (
    id         BIGINT        IDENTITY(1,1) PRIMARY KEY,
    ma_lop     NVARCHAR(20)  NOT NULL,
    ten_lop    NVARCHAR(100) NOT NULL,
    khoa_id    BIGINT        NULL,
    nien_khoa  NVARCHAR(20)  NULL,           -- VD: 2022-2026
    si_so      INT           NOT NULL DEFAULT 0,
    is_active  BIT           NOT NULL DEFAULT 1,
    created_by BIGINT        NULL,
    created_at DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2     NOT NULL DEFAULT GETDATE(),

    CONSTRAINT UQ_lophoc_ma      UNIQUE (ma_lop),
    CONSTRAINT FK_lophoc_khoa    FOREIGN KEY (khoa_id)
        REFERENCES tbl_khoa(id) ON DELETE SET NULL,
    CONSTRAINT FK_lophoc_created FOREIGN KEY (created_by)
        REFERENCES tbl_users(id) ON DELETE SET NULL
);
PRINT 'Tao bang tbl_lophoc thanh cong';
GO

-- ════════════════════════════════════════════
-- BƯỚC 6: tbl_sinhvien
-- ════════════════════════════════════════════
CREATE TABLE tbl_sinhvien (
    id         BIGINT        IDENTITY(1,1) PRIMARY KEY,
    ho_ten     NVARCHAR(100) NOT NULL,
    email      NVARCHAR(150) NOT NULL,
    lop_id     BIGINT        NULL,
    created_by BIGINT        NULL,
    created_at DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2     NOT NULL DEFAULT GETDATE(),

    CONSTRAINT UQ_sinhvien_email      UNIQUE (email),
    CONSTRAINT FK_sinhvien_lop        FOREIGN KEY (lop_id)
        REFERENCES tbl_lophoc(id) ON DELETE SET NULL,
    CONSTRAINT FK_sinhvien_created_by FOREIGN KEY (created_by)
        REFERENCES tbl_users(id) ON DELETE SET NULL
);
PRINT 'Tao bang tbl_sinhvien thanh cong';
GO

-- ════════════════════════════════════════════
-- BƯỚC 7: tbl_monhoc  (Môn học)
-- ════════════════════════════════════════════
CREATE TABLE tbl_monhoc (
    id         BIGINT        IDENTITY(1,1) PRIMARY KEY,
    ma_mon     NVARCHAR(20)  NOT NULL,
    ten_mon    NVARCHAR(150) NOT NULL,
    so_tin_chi INT           NOT NULL DEFAULT 3,
    khoa_id    BIGINT        NULL,
    mo_ta      NVARCHAR(500) NULL,
    is_active  BIT           NOT NULL DEFAULT 1,
    created_by BIGINT        NULL,
    created_at DATETIME2     NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2     NOT NULL DEFAULT GETDATE(),

    CONSTRAINT UQ_monhoc_ma      UNIQUE (ma_mon),
    CONSTRAINT CK_monhoc_tinchi  CHECK  (so_tin_chi BETWEEN 1 AND 10),
    CONSTRAINT FK_monhoc_khoa    FOREIGN KEY (khoa_id)
        REFERENCES tbl_khoa(id) ON DELETE SET NULL,
    CONSTRAINT FK_monhoc_created FOREIGN KEY (created_by)
        REFERENCES tbl_users(id) ON DELETE SET NULL
);
PRINT 'Tao bang tbl_monhoc thanh cong';
GO

-- ════════════════════════════════════════════
-- BƯỚC 8: tbl_diem  (Điểm số)
-- ════════════════════════════════════════════
CREATE TABLE tbl_diem (
    id              BIGINT       IDENTITY(1,1) PRIMARY KEY,
    sinhvien_id     BIGINT       NOT NULL,
    monhoc_id       BIGINT       NOT NULL,
    hoc_ki          INT          NOT NULL DEFAULT 1,
    nam_hoc         NVARCHAR(10) NOT NULL,           -- VD: 2024-2025
    diem_qua_trinh  DECIMAL(4,2) NULL,               -- 0.00 – 10.00
    diem_giua_ki    DECIMAL(4,2) NULL,
    diem_cuoi_ki    DECIMAL(4,2) NULL,
    diem_tong_ket   DECIMAL(4,2) NULL,
    ghi_chu         NVARCHAR(300) NULL,
    created_by      BIGINT       NULL,
    created_at      DATETIME2    NOT NULL DEFAULT GETDATE(),
    updated_at      DATETIME2    NOT NULL DEFAULT GETDATE(),

    -- Mỗi SV chỉ có 1 dòng điểm / môn / học kỳ / năm học
    CONSTRAINT UQ_diem_sv_mon_hk   UNIQUE (sinhvien_id, monhoc_id, hoc_ki, nam_hoc),
    CONSTRAINT CK_diem_hoc_ki      CHECK  (hoc_ki BETWEEN 1 AND 3),
    CONSTRAINT CK_diem_qua_trinh   CHECK  (diem_qua_trinh IS NULL OR diem_qua_trinh BETWEEN 0 AND 10),
    CONSTRAINT CK_diem_giua_ki     CHECK  (diem_giua_ki   IS NULL OR diem_giua_ki   BETWEEN 0 AND 10),
    CONSTRAINT CK_diem_cuoi_ki     CHECK  (diem_cuoi_ki   IS NULL OR diem_cuoi_ki   BETWEEN 0 AND 10),
    CONSTRAINT CK_diem_tong_ket    CHECK  (diem_tong_ket  IS NULL OR diem_tong_ket  BETWEEN 0 AND 10),

    CONSTRAINT FK_diem_sinhvien    FOREIGN KEY (sinhvien_id)
        REFERENCES tbl_sinhvien(id) ON DELETE CASCADE,
    CONSTRAINT FK_diem_monhoc      FOREIGN KEY (monhoc_id)
        REFERENCES tbl_monhoc(id)  ON DELETE CASCADE,
    CONSTRAINT FK_diem_created     FOREIGN KEY (created_by)
        REFERENCES tbl_users(id)   ON DELETE SET NULL
);
PRINT 'Tao bang tbl_diem thanh cong';
GO

-- ════════════════════════════════════════════
-- BƯỚC 9: Tạo Index
-- ════════════════════════════════════════════
CREATE INDEX IX_users_email          ON tbl_users(email);
CREATE INDEX IX_users_username       ON tbl_users(username);
CREATE INDEX IX_pwreset_token        ON tbl_password_reset(token);
CREATE INDEX IX_pwreset_user_id      ON tbl_password_reset(user_id);
CREATE INDEX IX_sinhvien_email       ON tbl_sinhvien(email);
CREATE INDEX IX_sinhvien_lop         ON tbl_sinhvien(lop_id);
CREATE INDEX IX_diem_sinhvien        ON tbl_diem(sinhvien_id);
CREATE INDEX IX_diem_monhoc          ON tbl_diem(monhoc_id);
CREATE INDEX IX_diem_namhoc_hocki    ON tbl_diem(nam_hoc, hoc_ki);
PRINT 'Tao cac Index thanh cong';
GO

-- ════════════════════════════════════════════
-- BƯỚC 10: Dữ liệu mẫu — Users
-- admin@gmail.com / admin@123
-- user@demo.com   / User@123
-- ════════════════════════════════════════════
INSERT INTO tbl_users (username, email, password, full_name, role, provider, enabled) VALUES
('admin',     'admin@gmail.com', '$2a$10$slYQmyNdgTY29Hu7KxP3be/MkSjO6r1jVzsDpYNXcE8gAuvCwgz.S', N'Quản Trị Viên',   'ADMIN', 'LOCAL', 1),
('nguyenvan', 'user@demo.com',   '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FXDt9wdkQP0.', N'Nguyễn Văn User', 'USER',  'LOCAL', 1);
PRINT 'Tao du lieu mau tbl_users thanh cong';
GO

-- ════════════════════════════════════════════
-- BƯỚC 11: Dữ liệu mẫu — Khoa
-- ════════════════════════════════════════════
INSERT INTO tbl_khoa (ma_khoa, ten_khoa, mo_ta, truong_khoa, created_by) VALUES
('CNTT',  N'Công Nghệ Thông Tin',  N'Khoa đào tạo kỹ sư phần mềm, mạng máy tính', N'PGS.TS Nguyễn Văn A', 1),
('KTKT',  N'Kỹ Thuật Kinh Tế',     N'Khoa đào tạo kế toán, quản trị kinh doanh',  N'TS Trần Thị B',       1),
('NGOAI', N'Ngoại Ngữ',            N'Khoa đào tạo tiếng Anh, tiếng Nhật, Hàn',    N'ThS Lê Văn C',        1);
PRINT 'Tao du lieu mau tbl_khoa thanh cong';
GO

-- ════════════════════════════════════════════
-- BƯỚC 12: Dữ liệu mẫu — Lớp học
-- ════════════════════════════════════════════
INSERT INTO tbl_lophoc (ma_lop, ten_lop, khoa_id, nien_khoa, si_so, created_by) VALUES
('CNTT01', N'Công Nghệ Thông Tin K1', 1, '2022-2026', 40, 1),
('CNTT02', N'Công Nghệ Thông Tin K2', 1, '2022-2026', 38, 1),
('KTKT01', N'Kế Toán K1',            2, '2023-2027', 35, 1);
PRINT 'Tao du lieu mau tbl_lophoc thanh cong';
GO

-- ════════════════════════════════════════════
-- BƯỚC 13: Dữ liệu mẫu — Sinh viên
-- ════════════════════════════════════════════
INSERT INTO tbl_sinhvien (ho_ten, email, lop_id, created_by) VALUES
(N'Nguyễn Văn An',   'an.nguyen@sv.edu.vn',   1, 1),
(N'Trần Thị Bình',   'binh.tran@sv.edu.vn',   1, 1),
(N'Lê Minh Châu',    'chau.le@sv.edu.vn',     2, 1),
(N'Phạm Thị Dung',   'dung.pham@sv.edu.vn',   2, 1),
(N'Hoàng Văn Đức',   'duc.hoang@sv.edu.vn',   3, 1);
PRINT 'Tao du lieu mau tbl_sinhvien thanh cong';
GO

-- ════════════════════════════════════════════
-- BƯỚC 14: Dữ liệu mẫu — Môn học
-- ════════════════════════════════════════════
INSERT INTO tbl_monhoc (ma_mon, ten_mon, so_tin_chi, khoa_id, created_by) VALUES
('LTJAVA',  N'Lập Trình Java',          3, 1, 1),
('CSDL',    N'Cơ Sở Dữ Liệu',          3, 1, 1),
('MANG',    N'Mạng Máy Tính',           3, 1, 1),
('KETOAN',  N'Kế Toán Đại Cương',       3, 2, 1),
('TIENGNH', N'Tiếng Anh Chuyên Ngành', 2, 3, 1);
PRINT 'Tao du lieu mau tbl_monhoc thanh cong';
GO

-- ════════════════════════════════════════════
-- BƯỚC 15: Dữ liệu mẫu — Điểm số
-- ════════════════════════════════════════════
INSERT INTO tbl_diem (sinhvien_id, monhoc_id, hoc_ki, nam_hoc, diem_qua_trinh, diem_giua_ki, diem_cuoi_ki, diem_tong_ket, created_by) VALUES
(1, 1, 1, '2024-2025', 8.0,  7.5,  8.5,  8.1,  1),
(1, 2, 1, '2024-2025', 7.5,  8.0,  7.0,  7.4,  1),
(2, 1, 1, '2024-2025', 9.0,  8.5,  9.0,  8.9,  1),
(2, 2, 1, '2024-2025', 6.5,  7.0,  6.0,  6.4,  1),
(3, 3, 1, '2024-2025', 8.5,  8.0,  8.0,  8.1,  1),
(4, 4, 1, '2024-2025', 7.0,  7.5,  8.0,  7.6,  1),
(5, 5, 1, '2024-2025', 9.5,  9.0,  9.5,  9.4,  1);
PRINT 'Tao du lieu mau tbl_diem thanh cong';
GO

-- ════════════════════════════════════════════
-- BƯỚC 16: Kiểm tra kết quả
-- ════════════════════════════════════════════
SELECT Bang, SoLuong FROM (
    SELECT 'tbl_users'          AS Bang, COUNT(*) AS SoLuong FROM tbl_users          UNION ALL
    SELECT 'tbl_password_reset',          COUNT(*)            FROM tbl_password_reset UNION ALL
    SELECT 'tbl_khoa',                    COUNT(*)            FROM tbl_khoa           UNION ALL
    SELECT 'tbl_lophoc',                  COUNT(*)            FROM tbl_lophoc         UNION ALL
    SELECT 'tbl_sinhvien',                COUNT(*)            FROM tbl_sinhvien       UNION ALL
    SELECT 'tbl_monhoc',                  COUNT(*)            FROM tbl_monhoc         UNION ALL
    SELECT 'tbl_diem',                    COUNT(*)            FROM tbl_diem
) AS KetQua
ORDER BY Bang;
GO

PRINT '=== HOAN TAT! Database QLSinhVien da duoc tao moi hoan toan ===';
GO