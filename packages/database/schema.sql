-- ===================================================================
-- Skrip DDL Database: Sistem Informasi Pengajuan Dokumen Kemahasiswaan
-- Fakultas Ilmu Komputer Universitas Brawijaya
-- Modul 3: Evaluasi Dokumen - Bagian 3.1
-- ===================================================================

CREATE DATABASE IF NOT EXISTS `kemahasiswaan_filkom`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `kemahasiswaan_filkom`;

-- 1. [AA] Tabel AkunPengguna
CREATE TABLE IF NOT EXISTS `AkunPengguna` (
  `idPengguna`   INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `username`     VARCHAR(30)  NOT NULL UNIQUE,
  `password`     VARCHAR(255) NOT NULL,
  `nama`         VARCHAR(100) NOT NULL,
  `nimNip`       VARCHAR(20)  NOT NULL UNIQUE,
  `email`        VARCHAR(100) NOT NULL UNIQUE,
  `noTelepon`    VARCHAR(15)  NULL,
  `peran`        VARCHAR(20)  NOT NULL,
  `sessionToken` VARCHAR(64)  NULL UNIQUE,
  CONSTRAINT `chk_akun_peran` CHECK (`peran` IN ('Mahasiswa','Staf Kemahasiswaan','Pejabat'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. [AA] Tabel JenisDokumen
CREATE TABLE IF NOT EXISTS `JenisDokumen` (
  `idJenisDokumen`    INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `kategori`          VARCHAR(30)  NOT NULL,
  `namaJenis`         VARCHAR(100) NOT NULL,
  `deskripsi`         VARCHAR(255) NULL,
  `estimasiHariKerja` INT          NOT NULL DEFAULT 3,
  `perluLegalisasi`   BOOLEAN      NOT NULL DEFAULT FALSE,
  CONSTRAINT `chk_jenis_kategori` CHECK (`kategori` IN ('Lomba','Beasiswa','Organisasi','Akademik/PKL','Lainnya')),
  CONSTRAINT `chk_jenis_estimasi` CHECK (`estimasiHariKerja` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. [MR] Tabel PersyaratanDokumen
CREATE TABLE IF NOT EXISTS `PersyaratanDokumen` (
  `idPersyaratan`   INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `idJenisDokumen`  INT          NOT NULL,
  `namaPersyaratan` VARCHAR(100) NOT NULL,
  `wajib`           BOOLEAN      NOT NULL DEFAULT TRUE,
  CONSTRAINT `fk_persyaratan_jenis` FOREIGN KEY (`idJenisDokumen`) REFERENCES `JenisDokumen`(`idJenisDokumen`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. [MR] Tabel Pengajuan
CREATE TABLE IF NOT EXISTS `Pengajuan` (
  `idPengajuan`      INT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `idPengguna`       INT         NOT NULL,
  `idJenisDokumen`   INT         NOT NULL,
  `tanggalPengajuan` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tanggalTenggat`   DATE        NULL,
  `status`           VARCHAR(30) NOT NULL DEFAULT 'Draft',
  CONSTRAINT `fk_pengajuan_pengguna` FOREIGN KEY (`idPengguna`) REFERENCES `AkunPengguna`(`idPengguna`) ON DELETE CASCADE,
  CONSTRAINT `fk_pengajuan_jenis` FOREIGN KEY (`idJenisDokumen`) REFERENCES `JenisDokumen`(`idJenisDokumen`) ON DELETE CASCADE,
  CONSTRAINT `chk_pengajuan_tenggat` CHECK (`tanggalTenggat` >= DATE(`tanggalPengajuan`)),
  CONSTRAINT `chk_pengajuan_status` CHECK (`status` IN ('Draft','Menunggu Verifikasi','Sedang Diproses','Revisi','Menunggu Legalisasi Pejabat','Selesai'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. [AT] Tabel BerkasLampiran
CREATE TABLE IF NOT EXISTS `BerkasLampiran` (
  `idLampiran`      INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `idPengajuan`     INT          NOT NULL,
  `idPersyaratan`   INT          NOT NULL,
  `namaFile`        VARCHAR(150) NOT NULL,
  `pathFile`        VARCHAR(255) NOT NULL,
  `ukuranKB`        INT          NOT NULL,
  `statusItem`      VARCHAR(20)  NOT NULL DEFAULT 'Terunggah',
  `versi`           INT          NOT NULL DEFAULT 1,
  `statusValidasi`  VARCHAR(20)  NOT NULL DEFAULT 'Belum Diperiksa',
  `catatanValidasi` VARCHAR(255) NULL,
  `idStafValidator` INT          NULL,
  `waktuValidasi`   DATETIME     NULL,
  `tanggalUnggah`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `uq_lampiran_versi` UNIQUE (`idPengajuan`, `idPersyaratan`, `versi`),
  CONSTRAINT `fk_lampiran_pengajuan` FOREIGN KEY (`idPengajuan`) REFERENCES `Pengajuan`(`idPengajuan`) ON DELETE CASCADE,
  CONSTRAINT `fk_lampiran_persyaratan` FOREIGN KEY (`idPersyaratan`) REFERENCES `PersyaratanDokumen`(`idPersyaratan`) ON DELETE CASCADE,
  CONSTRAINT `fk_lampiran_validator` FOREIGN KEY (`idStafValidator`) REFERENCES `AkunPengguna`(`idPengguna`) ON DELETE SET NULL,
  CONSTRAINT `chk_lampiran_ukuran` CHECK (`ukuranKB` > 0),
  CONSTRAINT `chk_lampiran_statusitem` CHECK (`statusItem` IN ('Terunggah','Diganti')),
  CONSTRAINT `chk_lampiran_statusvalidasi` CHECK (`statusValidasi` IN ('Belum Diperiksa','Lengkap','Tidak Lengkap'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. [AT] Tabel AntreanVerifikasi
CREATE TABLE IF NOT EXISTS `AntreanVerifikasi` (
  `idAntrean`     INT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `idPengajuan`   INT         NOT NULL UNIQUE,
  `statusAntrean` VARCHAR(20) NOT NULL DEFAULT 'Menunggu',
  `waktuMasuk`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_antrean_pengajuan` FOREIGN KEY (`idPengajuan`) REFERENCES `Pengajuan`(`idPengajuan`) ON DELETE CASCADE,
  CONSTRAINT `chk_antrean_status` CHECK (`statusAntrean` IN ('Menunggu','Diproses','Selesai'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. [JA] Tabel RiwayatStatus
CREATE TABLE IF NOT EXISTS `RiwayatStatus` (
  `idRiwayat`   INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `idPengajuan` INT          NOT NULL,
  `idPelaku`    INT          NOT NULL,
  `status`      VARCHAR(30)  NOT NULL,
  `catatan`     VARCHAR(255) NULL,
  `timestamp`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_riwayat_pengajuan` FOREIGN KEY (`idPengajuan`) REFERENCES `Pengajuan`(`idPengajuan`) ON DELETE CASCADE,
  CONSTRAINT `fk_riwayat_pelaku` FOREIGN KEY (`idPelaku`) REFERENCES `AkunPengguna`(`idPengguna`) ON DELETE CASCADE,
  CONSTRAINT `chk_riwayat_status` CHECK (`status` IN ('Draft','Menunggu Verifikasi','Sedang Diproses','Revisi','Menunggu Legalisasi Pejabat','Selesai'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
