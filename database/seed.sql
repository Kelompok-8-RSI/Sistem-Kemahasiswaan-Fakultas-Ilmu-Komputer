USE `layanan_kemahasiswaan`;

INSERT INTO `AkunPengguna` (`idPengguna`, `username`, `password`, `nama`, `nimNip`, `email`, `noTelepon`, `peran`, `sessionToken`)
VALUES
(1, '215150200111045', '$2a$10$OR/QZlQ3F4LOLvwmuObcWeSmYfTMIFDgdRhN2ylM5JgZMEnxeXmB6', 'Sal Priadi', '215150200111045', 'sal.priadi@student.ub.ac.id', '081234567890', 'Mahasiswa', NULL),
(2, '198705122015042001', '$2a$10$OR/QZlQ3F4LOLvwmuObcWeSmYfTMIFDgdRhN2ylM5JgZMEnxeXmB6', 'Dwi Lestari, S.E.', '198705122015042001', 'dwi.lestari@ub.ac.id', '081331224578', 'Staf Kemahasiswaan', NULL),
(3, '197811032005011002', '$2a$10$OR/QZlQ3F4LOLvwmuObcWeSmYfTMIFDgdRhN2ylM5JgZMEnxeXmB6', 'Dr. Bayu Santoso, S.Kom., M.Kom.', '197811032005011002', 'bayu.santoso@ub.ac.id', '081252340917', 'Pejabat', NULL),
(4, '235150400111058', '$2a$10$OR/QZlQ3F4LOLvwmuObcWeSmYfTMIFDgdRhN2ylM5JgZMEnxeXmB6', 'Fajar Nugroho', '235150400111058', 'fajar.nugroho@student.ub.ac.id', '085654521833', 'Mahasiswa', NULL)
ON DUPLICATE KEY UPDATE `nama`=VALUES(`nama`);

INSERT INTO `JenisDokumen` (`idJenisDokumen`, `kategori`, `namaJenis`, `deskripsi`, `estimasiHariKerja`, `perluLegalisasi`)
VALUES
(1, 'Lomba', 'Surat Tugas Delegasi Lomba', 'Surat tugas delegasi resmi fakultas untuk perwakilan perlombaan', 3, TRUE),
(2, 'Beasiswa', 'Surat Keterangan Beasiswa', 'Surat rekomendasi kelayakan untuk pendaftaran beasiswa', 3, FALSE),
(3, 'Lomba', 'Surat Rekomendasi Lomba', 'Surat rekomendasi fakultas untuk mendaftar lomba tingkat nasional atau internasional atas nama mahasiswa FILKOM', 3, FALSE),
(4, 'Organisasi', 'Surat Izin Kegiatan Organisasi', 'Surat izin penyelenggaraan program kerja dan kegiatan kelembagaan mahasiswa', 5, TRUE),
(5, 'Akademik/PKL', 'Surat Pengantar PKL', 'Surat pengantar permohonan magang / Praktik Kerja Lapangan ke instansi tujuan', 3, FALSE)
ON DUPLICATE KEY UPDATE `namaJenis`=VALUES(`namaJenis`);

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

INSERT INTO `Pengajuan` (`idPengajuan`, `idPengguna`, `idJenisDokumen`, `tanggalPengajuan`, `tanggalTenggat`, `status`)
VALUES
(1001, 1, 3, '2026-10-12 09:30:00', '2026-10-20', 'Sedang Diproses'),
(1003, 1, 1, '2026-10-14 13:02:00', '2026-10-25', 'Menunggu Legalisasi Pejabat'),
(1004, 1, 3, '2026-10-28 09:00:00', '2026-11-06', 'Menunggu Verifikasi')
ON DUPLICATE KEY UPDATE `status`=VALUES(`status`);

INSERT INTO `BerkasLampiran` (`idLampiran`, `idPengajuan`, `idPersyaratan`, `namaFile`, `pathFile`, `ukuranKB`, `statusItem`, `versi`, `statusValidasi`, `catatanValidasi`, `idStafValidator`, `waktuValidasi`, `tanggalUnggah`)
VALUES
(5001, 1001, 1, 'bukti_pendaftaran.pdf', '/uploads/1001/bukti_pendaftaran_v1.pdf', 842, 'Terunggah', 1, 'Lengkap', 'Dokumen terbaca jelas', 2, '2026-10-12 13:05:00', '2026-10-12 09:45:00'),
(5002, 1001, 2, 'ktm.jpg', '/uploads/1001/ktm_v1.jpg', 310, 'Terunggah', 1, 'Lengkap', 'KTM masih berlaku', 2, '2026-10-12 13:05:00', '2026-10-12 09:45:00'),
(5003, 1001, 3, 'guidebook_gemastik.pdf', '/uploads/1001/guidebook_gemastik_v1.pdf', 1843, 'Terunggah', 1, 'Tidak Lengkap', 'Bukan dokumen resmi panitia', 2, '2026-10-12 13:20:00', '2026-10-12 09:45:00'),
(5004, 1003, 4, 'undangan_delegasi.pdf', '/uploads/1003/undangan_delegasi_v1.pdf', 512, 'Terunggah', 1, 'Lengkap', 'Undangan resmi dari penyelenggara', 2, '2026-10-15 10:15:00', '2026-10-14 13:20:00'),
(5005, 1003, 5, 'ktm.jpg', '/uploads/1003/ktm_v1.jpg', 310, 'Terunggah', 1, 'Lengkap', 'KTM masih berlaku', 2, '2026-10-15 10:15:00', '2026-10-14 13:20:00'),
(5006, 1003, 6, 'proposal_keikutsertaan.pdf', '/uploads/1003/proposal_keikutsertaan_v1.pdf', 1204, 'Terunggah', 1, 'Lengkap', 'Proposal lengkap dan ditandatangani', 2, '2026-10-15 10:20:00', '2026-10-14 13:20:00'),
(5007, 1004, 1, 'bukti_pendaftaran_lomba.pdf', '/uploads/1004/bukti_pendaftaran_lomba_v1.pdf', 655, 'Terunggah', 1, 'Belum Diperiksa', NULL, NULL, NULL, '2026-10-28 09:02:00'),
(5008, 1004, 2, 'ktm.jpg', '/uploads/1004/ktm_v1.jpg', 310, 'Terunggah', 1, 'Belum Diperiksa', NULL, NULL, NULL, '2026-10-28 09:02:00'),
(5009, 1004, 3, 'ketentuan_lomba.pdf', '/uploads/1004/ketentuan_lomba_v1.pdf', 980, 'Terunggah', 1, 'Belum Diperiksa', NULL, NULL, NULL, '2026-10-28 09:02:00')
ON DUPLICATE KEY UPDATE `statusItem`=VALUES(`statusItem`);

INSERT INTO `AntreanVerifikasi` (`idAntrean`, `idPengajuan`, `statusAntrean`, `waktuMasuk`)
VALUES
(301, 1001, 'Diproses', '2026-10-12 09:50:00'),
(302, 1004, 'Menunggu', '2026-10-28 09:05:00'),
(303, 1003, 'Selesai', '2026-10-14 13:30:00')
ON DUPLICATE KEY UPDATE `statusAntrean`=VALUES(`statusAntrean`);

INSERT INTO `RiwayatStatus` (`idRiwayat`, `idPengajuan`, `idPelaku`, `status`, `catatan`, `timestamp`)
VALUES
(7001, 1001, 1, 'Draft', 'Pengajuan dibuat oleh mahasiswa', '2026-10-12 09:30:00'),
(7002, 1001, 1, 'Menunggu Verifikasi', 'Pengajuan disubmit mahasiswa', '2026-10-12 09:50:00'),
(7003, 1001, 2, 'Sedang Diproses', 'Mulai diverifikasi oleh staf', '2026-10-12 11:10:00'),
(7004, 1003, 1, 'Menunggu Verifikasi', 'Pengajuan disubmit mahasiswa', '2026-10-14 13:30:00'),
(7005, 1003, 2, 'Sedang Diproses', 'Mulai diverifikasi oleh staf', '2026-10-15 09:00:00'),
(7006, 1003, 2, 'Menunggu Legalisasi Pejabat', 'Lampiran lengkap, diteruskan ke Pejabat Fakultas', '2026-10-15 10:30:00'),
(7007, 1004, 1, 'Menunggu Verifikasi', 'Pengajuan disubmit mahasiswa', '2026-10-28 09:05:00')
ON DUPLICATE KEY UPDATE `status`=VALUES(`status`);
