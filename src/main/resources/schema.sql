-- ════════════════════════════════════════════════════════════════
-- SQL Server schema and sample data from Database_QLSinhvien_API.sql
-- ════════════════════════════════════════════════════════════════

CREATE TABLE dbo.tbl_users (
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
    created_at  DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at  DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT UQ_users_username UNIQUE (username),
    CONSTRAINT UQ_users_email    UNIQUE (email)
);

CREATE TABLE dbo.tbl_password_reset (
    id         BIGINT        IDENTITY(1,1) PRIMARY KEY,
    user_id    BIGINT        NOT NULL,
    token      NVARCHAR(500) NOT NULL,
    expires_at DATETIME2     NOT NULL,
    used       BIT           NOT NULL DEFAULT 0,
    CONSTRAINT UQ_password_reset_token UNIQUE (token),
    CONSTRAINT FK_password_reset_user FOREIGN KEY (user_id)
        REFERENCES dbo.tbl_users(id) ON DELETE CASCADE
);

CREATE TABLE dbo.tbl_khoa (
    id          BIGINT        IDENTITY(1,1) PRIMARY KEY,
    ma_khoa     NVARCHAR(20)  NOT NULL,
    ten_khoa    NVARCHAR(150) NOT NULL,
    mo_ta       NVARCHAR(500) NULL,
    truong_khoa NVARCHAR(100) NULL,
    is_active   BIT           NOT NULL DEFAULT 1,
    created_by  BIGINT        NULL,
    created_at  DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at  DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT UQ_khoa_ma         UNIQUE (ma_khoa),
    CONSTRAINT FK_khoa_created_by FOREIGN KEY (created_by)
        REFERENCES dbo.tbl_users(id) ON DELETE SET NULL
);

CREATE TABLE dbo.tbl_lophoc (
    id         BIGINT        IDENTITY(1,1) PRIMARY KEY,
    ma_lop     NVARCHAR(20)  NOT NULL,
    ten_lop    NVARCHAR(100) NOT NULL,
    khoa_id    BIGINT        NULL,
    nien_khoa  NVARCHAR(20)  NULL,
    si_so      INT           NOT NULL DEFAULT 0,
    is_active  BIT           NOT NULL DEFAULT 1,
    created_by BIGINT        NULL,
    created_at DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT UQ_lophoc_ma      UNIQUE (ma_lop),
    CONSTRAINT FK_lophoc_khoa    FOREIGN KEY (khoa_id)
        REFERENCES dbo.tbl_khoa(id) ON DELETE SET NULL,
    CONSTRAINT FK_lophoc_created FOREIGN KEY (created_by)
        REFERENCES dbo.tbl_users(id) ON DELETE SET NULL
);

CREATE TABLE dbo.tbl_sinhvien (
    id         BIGINT        IDENTITY(1,1) PRIMARY KEY,
    ho_ten     NVARCHAR(100) NOT NULL,
    email      NVARCHAR(150) NOT NULL,
    lop_id     BIGINT        NULL,
    created_by BIGINT        NULL,
    created_at DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT UQ_sinhvien_email      UNIQUE (email),
    CONSTRAINT FK_sinhvien_lop        FOREIGN KEY (lop_id)
        REFERENCES dbo.tbl_lophoc(id) ON DELETE SET NULL,
    CONSTRAINT FK_sinhvien_created_by FOREIGN KEY (created_by)
        REFERENCES dbo.tbl_users(id) ON DELETE SET NULL
);

CREATE TABLE dbo.tbl_monhoc (
    id         BIGINT        IDENTITY(1,1) PRIMARY KEY,
    ma_mon     NVARCHAR(20)  NOT NULL,
    ten_mon    NVARCHAR(150) NOT NULL,
    so_tin_chi INT           NOT NULL DEFAULT 3,
    khoa_id    BIGINT        NULL,
    mo_ta      NVARCHAR(500) NULL,
    is_active  BIT           NOT NULL DEFAULT 1,
    created_by BIGINT        NULL,
    created_at DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2     NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT UQ_monhoc_ma      UNIQUE (ma_mon),
    CONSTRAINT CK_monhoc_tinchi  CHECK  (so_tin_chi BETWEEN 1 AND 10),
    CONSTRAINT FK_monhoc_khoa    FOREIGN KEY (khoa_id)
        REFERENCES dbo.tbl_khoa(id) ON DELETE SET NULL,
    CONSTRAINT FK_monhoc_created FOREIGN KEY (created_by)
        REFERENCES dbo.tbl_users(id) ON DELETE SET NULL
);

CREATE TABLE dbo.tbl_diem (
    id              BIGINT       IDENTITY(1,1) PRIMARY KEY,
    sinhvien_id     BIGINT       NOT NULL,
    monhoc_id       BIGINT       NOT NULL,
    hoc_ki          INT          NOT NULL DEFAULT 1,
    nam_hoc         NVARCHAR(10) NOT NULL,
    diem_qua_trinh  DECIMAL(4,2) NULL,
    diem_giua_ki    DECIMAL(4,2) NULL,
    diem_cuoi_ki    DECIMAL(4,2) NULL,
    diem_tong_ket   DECIMAL(4,2) NULL,
    ghi_chu         NVARCHAR(300) NULL,
    created_by      BIGINT       NULL,
    created_at      DATETIME2    NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at      DATETIME2    NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT UQ_diem_sv_mon_hk UNIQUE (sinhvien_id, monhoc_id, hoc_ki, nam_hoc),
    CONSTRAINT CK_diem_hoc_ki      CHECK  (hoc_ki BETWEEN 1 AND 3),
    CONSTRAINT CK_diem_qua_trinh   CHECK  (diem_qua_trinh IS NULL OR diem_qua_trinh BETWEEN 0 AND 10),
    CONSTRAINT CK_diem_giua_ki     CHECK  (diem_giua_ki   IS NULL OR diem_giua_ki   BETWEEN 0 AND 10),
    CONSTRAINT CK_diem_cuoi_ki     CHECK  (diem_cuoi_ki   IS NULL OR diem_cuoi_ki   BETWEEN 0 AND 10),
    CONSTRAINT CK_diem_tong_ket    CHECK  (diem_tong_ket  IS NULL OR diem_tong_ket  BETWEEN 0 AND 10),
    CONSTRAINT FK_diem_sinhvien    FOREIGN KEY (sinhvien_id)
        REFERENCES dbo.tbl_sinhvien(id) ON DELETE CASCADE,
    CONSTRAINT FK_diem_monhoc      FOREIGN KEY (monhoc_id)
        REFERENCES dbo.tbl_monhoc(id) ON DELETE CASCADE,
    CONSTRAINT FK_diem_created     FOREIGN KEY (created_by)
        REFERENCES dbo.tbl_users(id) ON DELETE SET NULL
);

-- Sample data inserts
IF NOT EXISTS (SELECT 1 FROM dbo.tbl_users WHERE email = 'admin@gmail.com')
BEGIN
    INSERT INTO dbo.tbl_users (username, email, password, full_name, role, provider, enabled)
    VALUES ('admin', 'admin@gmail.com', '$2a$10$slYQmyNdgTY29Hu7KxP3be/MkSjO6r1jVzsDpYNXcE8gAuvCwgz.S', N'Quản Trị Viên', 'ADMIN', 'LOCAL', 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_users WHERE email = 'user@demo.com')
BEGIN
    INSERT INTO dbo.tbl_users (username, email, password, full_name, role, provider, enabled)
    VALUES ('nguyenvan', 'user@demo.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FXDt9wdkQP0.', N'Nguyễn Văn User', 'USER', 'LOCAL', 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_khoa WHERE ma_khoa = 'CNTT')
BEGIN
    INSERT INTO dbo.tbl_khoa (ma_khoa, ten_khoa, mo_ta, truong_khoa, created_by)
    VALUES ('CNTT', N'Công Nghệ Thông Tin', N'Khoa đào tạo kỹ sư phần mềm, mạng máy tính', N'PGS.TS Nguyễn Văn A', 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_khoa WHERE ma_khoa = 'KTKT')
BEGIN
    INSERT INTO dbo.tbl_khoa (ma_khoa, ten_khoa, mo_ta, truong_khoa, created_by)
    VALUES ('KTKT', N'Kỹ Thuật Kinh Tế', N'Khoa đào tạo kế toán, quản trị kinh doanh', N'TS Trần Thị B', 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_khoa WHERE ma_khoa = 'NGOAI')
BEGIN
    INSERT INTO dbo.tbl_khoa (ma_khoa, ten_khoa, mo_ta, truong_khoa, created_by)
    VALUES ('NGOAI', N'Ngoại Ngữ', N'Khoa đào tạo tiếng Anh, tiếng Nhật, Hàn', N'ThS Lê Văn C', 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_lophoc WHERE ma_lop = 'CNTT01')
BEGIN
    INSERT INTO dbo.tbl_lophoc (ma_lop, ten_lop, khoa_id, nien_khoa, si_so, created_by)
    VALUES ('CNTT01', N'Công Nghệ Thông Tin K1', 1, '2022-2026', 40, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_lophoc WHERE ma_lop = 'CNTT02')
BEGIN
    INSERT INTO dbo.tbl_lophoc (ma_lop, ten_lop, khoa_id, nien_khoa, si_so, created_by)
    VALUES ('CNTT02', N'Công Nghệ Thông Tin K2', 1, '2022-2026', 38, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_lophoc WHERE ma_lop = 'KTKT01')
BEGIN
    INSERT INTO dbo.tbl_lophoc (ma_lop, ten_lop, khoa_id, nien_khoa, si_so, created_by)
    VALUES ('KTKT01', N'Kế Toán K1', 2, '2023-2027', 35, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_sinhvien WHERE email = 'an.nguyen@sv.edu.vn')
BEGIN
    INSERT INTO dbo.tbl_sinhvien (ho_ten, email, lop_id, created_by)
    VALUES (N'Nguyễn Văn An', 'an.nguyen@sv.edu.vn', 1, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_sinhvien WHERE email = 'binh.tran@sv.edu.vn')
BEGIN
    INSERT INTO dbo.tbl_sinhvien (ho_ten, email, lop_id, created_by)
    VALUES (N'Trần Thị Bình', 'binh.tran@sv.edu.vn', 1, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_sinhvien WHERE email = 'chau.le@sv.edu.vn')
BEGIN
    INSERT INTO dbo.tbl_sinhvien (ho_ten, email, lop_id, created_by)
    VALUES (N'Lê Minh Châu', 'chau.le@sv.edu.vn', 2, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_sinhvien WHERE email = 'dung.pham@sv.edu.vn')
BEGIN
    INSERT INTO dbo.tbl_sinhvien (ho_ten, email, lop_id, created_by)
    VALUES (N'Phạm Thị Dung', 'dung.pham@sv.edu.vn', 2, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_sinhvien WHERE email = 'duc.hoang@sv.edu.vn')
BEGIN
    INSERT INTO dbo.tbl_sinhvien (ho_ten, email, lop_id, created_by)
    VALUES (N'Hoàng Văn Đức', 'duc.hoang@sv.edu.vn', 3, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_monhoc WHERE ma_mon = 'LTJAVA')
BEGIN
    INSERT INTO dbo.tbl_monhoc (ma_mon, ten_mon, so_tin_chi, khoa_id, created_by)
    VALUES ('LTJAVA', N'Lập Trình Java', 3, 1, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_monhoc WHERE ma_mon = 'CSDL')
BEGIN
    INSERT INTO dbo.tbl_monhoc (ma_mon, ten_mon, so_tin_chi, khoa_id, created_by)
    VALUES ('CSDL', N'Cơ Sở Dữ Liệu', 3, 1, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_monhoc WHERE ma_mon = 'MANG')
BEGIN
    INSERT INTO dbo.tbl_monhoc (ma_mon, ten_mon, so_tin_chi, khoa_id, created_by)
    VALUES ('MANG', N'Mạng Máy Tính', 3, 1, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_monhoc WHERE ma_mon = 'KETOAN')
BEGIN
    INSERT INTO dbo.tbl_monhoc (ma_mon, ten_mon, so_tin_chi, khoa_id, created_by)
    VALUES ('KETOAN', N'Kế Toán Đại Cương', 3, 2, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_monhoc WHERE ma_mon = 'TIENGNH')
BEGIN
    INSERT INTO dbo.tbl_monhoc (ma_mon, ten_mon, so_tin_chi, khoa_id, created_by)
    VALUES ('TIENGNH', N'Tiếng Anh Chuyên Ngành', 2, 3, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_diem WHERE sinhvien_id = 1 AND monhoc_id = 1 AND hoc_ki = 1 AND nam_hoc = '2024-2025')
BEGIN
    INSERT INTO dbo.tbl_diem (sinhvien_id, monhoc_id, hoc_ki, nam_hoc, diem_qua_trinh, diem_giua_ki, diem_cuoi_ki, diem_tong_ket, created_by)
    VALUES (1, 1, 1, '2024-2025', 8.0, 7.5, 8.5, 8.1, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_diem WHERE sinhvien_id = 1 AND monhoc_id = 2 AND hoc_ki = 1 AND nam_hoc = '2024-2025')
BEGIN
    INSERT INTO dbo.tbl_diem (sinhvien_id, monhoc_id, hoc_ki, nam_hoc, diem_qua_trinh, diem_giua_ki, diem_cuoi_ki, diem_tong_ket, created_by)
    VALUES (1, 2, 1, '2024-2025', 7.5, 8.0, 7.0, 7.4, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_diem WHERE sinhvien_id = 2 AND monhoc_id = 1 AND hoc_ki = 1 AND nam_hoc = '2024-2025')
BEGIN
    INSERT INTO dbo.tbl_diem (sinhvien_id, monhoc_id, hoc_ki, nam_hoc, diem_qua_trinh, diem_giua_ki, diem_cuoi_ki, diem_tong_ket, created_by)
    VALUES (2, 1, 1, '2024-2025', 9.0, 8.5, 9.0, 8.9, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_diem WHERE sinhvien_id = 2 AND monhoc_id = 2 AND hoc_ki = 1 AND nam_hoc = '2024-2025')
BEGIN
    INSERT INTO dbo.tbl_diem (sinhvien_id, monhoc_id, hoc_ki, nam_hoc, diem_qua_trinh, diem_giua_ki, diem_cuoi_ki, diem_tong_ket, created_by)
    VALUES (2, 2, 1, '2024-2025', 6.5, 7.0, 6.0, 6.4, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_diem WHERE sinhvien_id = 3 AND monhoc_id = 3 AND hoc_ki = 1 AND nam_hoc = '2024-2025')
BEGIN
    INSERT INTO dbo.tbl_diem (sinhvien_id, monhoc_id, hoc_ki, nam_hoc, diem_qua_trinh, diem_giua_ki, diem_cuoi_ki, diem_tong_ket, created_by)
    VALUES (3, 3, 1, '2024-2025', 8.5, 8.0, 8.0, 8.1, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_diem WHERE sinhvien_id = 4 AND monhoc_id = 4 AND hoc_ki = 1 AND nam_hoc = '2024-2025')
BEGIN
    INSERT INTO dbo.tbl_diem (sinhvien_id, monhoc_id, hoc_ki, nam_hoc, diem_qua_trinh, diem_giua_ki, diem_cuoi_ki, diem_tong_ket, created_by)
    VALUES (4, 4, 1, '2024-2025', 7.0, 7.5, 8.0, 7.6, 1);
END

IF NOT EXISTS (SELECT 1 FROM dbo.tbl_diem WHERE sinhvien_id = 5 AND monhoc_id = 5 AND hoc_ki = 1 AND nam_hoc = '2024-2025')
BEGIN
    INSERT INTO dbo.tbl_diem (sinhvien_id, monhoc_id, hoc_ki, nam_hoc, diem_qua_trinh, diem_giua_ki, diem_cuoi_ki, diem_tong_ket, created_by)
    VALUES (5, 5, 1, '2024-2025', 9.5, 9.0, 9.5, 9.4, 1);
END