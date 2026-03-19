-- ════════════════════════════════════════════
-- H2 SCHEMA
-- ════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS tbl_users (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    email        VARCHAR(150) NOT NULL UNIQUE,
    password     VARCHAR(255),
    full_name    VARCHAR(100),
    avatar_url   VARCHAR(500),
    role         VARCHAR(20)  NOT NULL DEFAULT 'USER',
    provider     VARCHAR(20)  NOT NULL DEFAULT 'LOCAL',
    provider_id  VARCHAR(200),
    enabled      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tbl_password_reset (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES tbl_users(id),
    token      VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMP    NOT NULL,
    used       BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS tbl_sinhvien (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    ho_ten     VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    created_by BIGINT       REFERENCES tbl_users(id),
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);