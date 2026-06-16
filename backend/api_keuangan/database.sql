-- ============================================
-- Database: KeuanganKu
-- Aplikasi Pengelolaan Keuangan Pribadi Mahasiswa
-- ============================================

CREATE DATABASE IF NOT EXISTS db_keuangan;
USE db_keuangan;

-- ============================================
-- Tabel User
-- ============================================
CREATE TABLE IF NOT EXISTS user (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Tabel Transaksi
-- ============================================
CREATE TABLE IF NOT EXISTS transaksi (
    id_transaksi INT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL,
    jenis ENUM('pemasukan', 'pengeluaran') NOT NULL,
    kategori VARCHAR(100) NOT NULL,
    nominal DECIMAL(15, 2) NOT NULL,
    tanggal DATE NOT NULL,
    keterangan TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_user) REFERENCES user(id_user) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
