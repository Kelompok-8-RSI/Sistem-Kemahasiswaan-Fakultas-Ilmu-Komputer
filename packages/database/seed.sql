-- ===================================================================
-- Skrip Seeder Database: Sistem Informasi Pengajuan Dokumen Kemahasiswaan
-- Data awal berdasarkan contoh studi kasus Modul 1 & Modul 2
-- ===================================================================

USE `kemahasiswaan_filkom`;

-- 1. Seeder AkunPengguna
INSERT INTO `AkunPengguna` (`idPengguna`, `username`, `password`, `nama`, `nimNip`, `email`, `noTelepon`, `peran`, `sessionToken`)
VALUES
(1, 'sal.priadi', '$2b$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Sal Priadi', '215150200111045', 'sal.priadi@student.ub.ac.id', '081234567890', 'Mahasiswa', NULL),
(2, 'dwi.lestari', '$2b$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Dwi Lestari, S.E.', '198705122015042001', 'dwi.lestari@ub.ac.id', '081331224578', 'Staf Kemahasiswaan', NULL),
(3, 'bayu.santoso', '$2b$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Dr. Bayu Santoso, S.Kom., M.Kom.', '197811032005011002', 'bayu.santoso@ub.ac.id', '081252340917', 'Pejabat', NULL),
(4, 'fajar.nugroho', '$2b$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Fajar Nugroho', '235150400111058', 'fajar.nugroho@student.ub.ac.id', '085654521833', 'Mahasiswa', NULL)
ON DUPLICATE KEY UPDATE `nama`=VALUES(`nama`);

-- 2. Seeder JenisDokumen
INSERT INTO `JenisDokumen` (`idJenisDokumen`, `kategori`, `namaJenis`, `deskripsi`, `estimasiHariKerja`, `perluLegalisasi`)
VALUES
(1, 'Lomba', 'Surat Tugas Delegasi Lomba', 'Surat tugas delegasi resmi fakultas untuk perwakilan perlombaan', 3, TRUE),
(2, 'Beasiswa', 'Surat Keterangan Beasiswa', 'Surat rekomendasi kelayakan untuk pendaftaran beasiswa', 3, FALSE),
(3, 'Lomba', 'Surat Rekomendasi Lomba', 'Surat rekomendasi fakultas untuk mendaftar lomba tingkat nasional atau internasional atas nama mahasiswa FILKOM', 3, FALSE),
(4, 'Organisasi', 'Surat Izin Kegiatan Organisasi', 'Surat izin penyelenggaraan program kerja dan kegiatan kelembagaan mahasiswa', 5, TRUE),
(5, 'Akademik/PKL', 'Surat Pengantar PKL', 'Surat pengantar permohonan magang / Praktik Kerja Lapangan ke instansi tujuan', 3, FALSE)
ON DUPLICATE KEY UPDATE `namaJenis`=VALUES(`namaJenis`);

-- 3. Seeder PersyaratanDokumen
INSERT INTO `PersyaratanDokumen` (`idPersyaratan`, `idJenisDokumen`, `namaPersyaratan`, `wajib`)
VALUES
(1, 3, 'Bukti pendaftaran lomba', TRUE),
(2, 3, 'Kartu Tanda Mahasiswa (KTM)', TRUE),
(3, 3, 'Surat ketentuan lomba', TRUE),
(4, 1, 'Surat undangan delegasi', TRUE),
(5, 1, 'Kartu Tanda Mahasiswa (KTM)', TRUE),
(6, 1, 'Proposal keikutsertaan', TRUE),
(7, 5, 'Surat permohonan PKL dari instansi', TRUE),
(8, 5, 'Kartu Tanda Mahasiswa (KTM)', TRUE),
(9, 5, 'Proposal PKL', TRUE)
ON DUPLICATE KEY UPDATE `namaPersyaratan`=VALUES(`namaPersyaratan`);

-- 4. Seeder Pengajuan
INSERT INTO `Pengajuan` (`idPengajuan`, `idPengguna`, `idJenisDokumen`, `tanggalPengajuan`, `tanggalTenggat`, `status`)
VALUES
(1001, 1, 3, '2026-10-12 09:30:00', '2026-10-20', 'Sedang Diproses'),
(1003, 1, 1, '2026-10-14 13:02:00', '2026-10-25', 'Menunggu Legalisasi Pejabat'),
(1004, 1, 3, '2026-10-28 09:00:00', '2026-11-06', 'Menunggu Verifikasi')
ON DUPLICATE KEY UPDATE `status`=VALUES(`status`);

-- 5. Seeder BerkasLampiran
INSERT INTO `BerkasLampiran` (`idLampiran`, `idPengajuan`, `idPersyaratan`, `namaFile`, `pathFile`, `ukuranKB`, `statusItem`, `versi`, `statusValidasi`, `catatanValidasi`, `idStafValidator`, `waktuValidasi`, `tanggalUnggah`)
VALUES
(5001, 1001, 1, 'bukti_pendaftaran.pdf', '/uploads/1001/bukti_pendaftaran_v1.pdf', 842, 'Terunggah', 1, 'Lengkap', 'Dokumen terbaca jelas', 2, '2026-10-12 13:05:00', '2026-10-12 09:45:00'),
(5002, 1001, 2, 'ktm.jpg', '/uploads/1001/ktm_v1.jpg', 310, 'Terunggah', 1, 'Lengkap', 'KTM masih berlaku', 2, '2026-10-12 13:05:00', '2026-10-12 09:45:00'),
(5003, 1001, 3, 'guidebook_gemastik.pdf', '/uploads/1001/guidebook_gemastik_v1.pdf', 1843, 'Terunggah', 1, 'Tidak Lengkap', 'Bukan dokumen resmi panitia', 2, '2026-10-12 13:20:00', '2026-10-12 09:45:00')
ON DUPLICATE KEY UPDATE `statusItem`=VALUES(`statusItem`);

-- 6. Seeder AntreanVerifikasi
INSERT INTO `AntreanVerifikasi` (`idAntrean`, `idPengajuan`, `statusAntrean`, `waktuMasuk`)
VALUES
(301, 1001, 'Diproses', '2026-10-12 10:00:00'),
(302, 1004, 'Menunggu', '2026-10-28 09:05:00')
ON DUPLICATE KEY UPDATE `statusAntrean`=VALUES(`statusAntrean`);

-- 7. Seeder RiwayatStatus
INSERT INTO `RiwayatStatus` (`idRiwayat`, `idPengajuan`, `idPelaku`, `status`, `catatan`, `timestamp`)
VALUES
(7001, 1001, 1, 'Draft', 'Pengajuan dibuat oleh mahasiswa', '2026-10-12 09:24:00'),
(7002, 1001, 1, 'Menunggu Verifikasi', 'Pengajuan disubmit mahasiswa', '2026-10-12 09:30:00'),
(7003, 1001, 2, 'Sedang Diproses', 'Mulai diverifikasi oleh staf', '2026-10-12 11:10:00')
ON DUPLICATE KEY UPDATE `status`=VALUES(`status`);
