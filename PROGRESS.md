# PROGRESS Implementasi Kode Kelompok 8

Aturan dan rencana lengkap: `docs/ai/PROMPT.md`. Cara pakai untuk anggota: `docs/ai/CARA-PAKAI.md`.

## Status task

| Task | Judul | Pemilik | Pelaksana | Branch | Status |
|---|---|---|---|---|---|
| T01 | Fondasi proyek, workflow AI, entity akun dan jenis dokumen | AA | Ammar | ammar/t01-fondasi-entity-akun | Selesai |
| T02 | Konfigurasi, keamanan, layout, dan CSS | MR | Rasil | rasil/t02-konfigurasi-keamanan-layout | Selesai |
| T03 | Entity lampiran dan antrean verifikasi | AT | Ammar | ammar/t03-entity-lampiran-antrean | Selesai |
| T04 | Entity persyaratan dokumen dan pengajuan | MR | Rasil | rasil/t04-entity-persyaratan-pengajuan | Selesai |
| T05 | Entity riwayat status dan paket DTO | JA | Jasmine | - | Belum |
| T06 | Login ke sistem | AA | Amirah | - | Belum |
| T07 | Keluar dari akun | MR | Rasil | - | Belum |
| T08 | Memilih jenis dokumen | AA | Amirah | - | Belum |
| T09 | Mencantumkan tenggat kebutuhan | AT | Ammar | - | Belum |
| T10 | Mengunggah persyaratan dan submit pengajuan | AA | Amirah | - | Belum |
| T11 | Riwayat status pengajuan (Pengajuan Saya) | MR | Rasil | - | Belum |
| T12 | Antrean pengajuan berdasarkan prioritas | AT | Ammar | - | Belum |
| T13 | Validasi kelengkapan lampiran | JA | Jasmine | - | Belum |
| T14 | Memperbarui status pengajuan | MR | Rasil | - | Belum |
| T15 | Keputusan legalisasi oleh Pejabat | MR | Rasil | - | Belum |
| T16 | Merevisi pengajuan yang ditolak | JA | Jasmine | - | Belum |
| T17 | Melihat dan memperbarui profil | AT | Ammar | - | Belum |
| T18 | Integrasi akhir, README, dan penutupan | AT | Ammar | - | Belum |

## Task berikutnya

T05: Entity riwayat status dan paket DTO. Pelaksana: Jasmine (JA).

## Log pengerjaan

### T01: Fondasi proyek, workflow AI, entity akun dan jenis dokumen
- Tanggal: 2026-11-10
- Pelaksana: Ammar (mengambil alih AA)
- Ringkasan: Paket workflow AI (`AGENTS.md`, `CLAUDE.md`, `GEMINI.md`, `docs/ai/`, `docs/referensi/`) dimasukkan ke repository dan `PROGRESS.md` dibuat. Konfigurasi proyek (SC11) dan skrip database disamakan dengan kode Modul 3 tanpa komentar. Entity `AkunPengguna`, `JenisDokumen`, dan `AkunPenggunaRepository` (SC06) ditambahkan. README diperbarui dengan akun demo dan pembagian task T01 sampai T18.
- File dibuat atau diubah:
  - Dibuat: `PROGRESS.md`, `src/main/java/id/ac/ub/layanan/entity/AkunPengguna.java`, `src/main/java/id/ac/ub/layanan/entity/JenisDokumen.java`, `src/main/java/id/ac/ub/layanan/repository/AkunPenggunaRepository.java`
  - Diubah: `pom.xml`, `src/main/resources/application.properties`, `src/main/java/id/ac/ub/layanan/LayananKemahasiswaanApplication.java`, `database/schema.sql`, `database/seed.sql`, `README.md`
  - Dihapus: `src/main/java/id/ac/ub/layanan/entity/.gitkeep`, `src/main/java/id/ac/ub/layanan/repository/.gitkeep`
  - Paket awal yang dimasukkan apa adanya: `AGENTS.md`, `CLAUDE.md`, `GEMINI.md`, `docs/`
- Keputusan dan penyimpangan dari modul (beserta alasannya):
  - `database/schema.sql` disalin persis dari `R3/database/schema.sql` (tanpa backtick kecuali kolom `timestamp`, tanpa `ENGINE`, tanpa `IF NOT EXISTS` pada tabel). Akibatnya skrip ini hanya bisa dijalankan pada database yang belum berisi tabel. Untuk mengulang dari awal, jalankan `DROP DATABASE layanan_kemahasiswaan;` dulu.
  - `pom.xml` mengikuti indentasi 2 spasi dari `R3/pom.xml`.
  - `database/seed.sql` tetap memakai format lama (backtick dan `ON DUPLICATE KEY UPDATE`). Hanya komentar yang dihapus dan hash password diganti dengan hash `password123`. Kecocokan hash dengan `password123` sudah diuji memakai `BCryptPasswordEncoder.matches`. Semua nilai sudah dicek dan memenuhi CHECK pada DDL.
- Perubahan di luar cakupan: Tidak ada
- Status build: Lulus (`./mvnw -q -DskipTests package`)
- Catatan untuk task berikutnya:
  - `AuthInterceptor` (T02) memakai `AkunPenggunaRepository.findBySessionToken` yang sudah tersedia.
  - Folder `.gitkeep` yang masih tersisa: `config`, `controller`, `dto`, `exception`, `security`, `service`, `static/css`, `templates/fragments`, dan `src/test`. Hapus `.gitkeep` di folder yang mulai diisi.
  - Logo `src/main/resources/static/img/logo-filkom-ub.png` belum ada. Ini perlu disiapkan pada T02 sesuai instruksi di `docs/ai/PROMPT.md`.
  - Seed pengajuan 1003 (status `Menunggu Legalisasi Pejabat`) belum punya baris `AntreanVerifikasi` dan `RiwayatStatus`, dan lampiran contoh di seed belum punya berkas fisik di `uploads/`. Ini dirapikan pada T18.

### T02: Konfigurasi, keamanan, layout, dan CSS
- Tanggal: 2026-11-19
- Pelaksana: Rasil (MR)
- Ringkasan: Konfigurasi aplikasi (`AppConfig`, `WebConfig`), keamanan sesi dan peran (`AuthInterceptor`, `RequiredRole`), exception `DataTidakDitemukanException` (SC10), serta template layout dasar `fragments/layout.html` dan stylesheet `app.css` (IM-12) beserta logo FILKOM UB ditambahkan ke proyek tanpa komentar.
- File dibuat atau diubah:
  - Dibuat: `src/main/java/id/ac/ub/layanan/config/AppConfig.java`, `src/main/java/id/ac/ub/layanan/config/WebConfig.java`, `src/main/java/id/ac/ub/layanan/security/AuthInterceptor.java`, `src/main/java/id/ac/ub/layanan/security/RequiredRole.java`, `src/main/java/id/ac/ub/layanan/exception/DataTidakDitemukanException.java`, `src/main/resources/templates/fragments/layout.html`, `src/main/resources/static/css/app.css` (serta menyertakan `src/main/resources/static/img/logo-filkom-ub.png` yang sudah disiapkan pelaksana)
  - Diubah: `PROGRESS.md`
  - Dihapus: `src/main/java/id/ac/ub/layanan/config/.gitkeep`, `src/main/java/id/ac/ub/layanan/security/.gitkeep`, `src/main/java/id/ac/ub/layanan/exception/.gitkeep`, `src/main/resources/static/css/.gitkeep`, `src/main/resources/templates/fragments/.gitkeep`
- Keputusan dan penyimpangan dari modul (beserta alasannya):
  - Semua berkas disalin persis dari `docs/referensi/kode-modul-3/` tanpa komentar.
  - Berkas `src/main/resources/static/img/logo-filkom-ub.png` sudah tersedia di workspace sebelum sesi dijalankan sehingga langsung disertakan pada commit UI tanpa perubahan.
- Perubahan di luar cakupan: Tidak ada
- Status build: Lulus (`.\mvnw.cmd -q -DskipTests package`)
- Catatan untuk task berikutnya:
  - `AuthInterceptor` sekarang melindungi semua rute kecuali `/login`, `/css/**`, `/img/**`, dan `/error`.
  - Sisa `.gitkeep` berada di folder `controller`, `dto`, `service`, dan `src/test`. Hapus `.gitkeep` di folder yang mulai diisi pada task terkait.
  - Aset `src/main/resources/static/img/logo-filkom-ub.png` sudah siap dipakai oleh `fragments/layout.html` dan `login.html` (T06).

### T03: Entity lampiran dan antrean verifikasi
- Tanggal: 2026-11-19
- Pelaksana: Ammar (AT)
- Ringkasan: Entity `BerkasLampiran` dan `AntreanVerifikasi` serta `BerkasLampiranRepository` (SC08) disalin dari kode Modul 3 tanpa komentar. `AntreanVerifikasiRepository` dengan method `findByIdPengajuan` ditambahkan untuk dipakai T10, T14, dan T16.
- File dibuat atau diubah:
  - Dibuat: `src/main/java/id/ac/ub/layanan/entity/BerkasLampiran.java`, `src/main/java/id/ac/ub/layanan/entity/AntreanVerifikasi.java`, `src/main/java/id/ac/ub/layanan/repository/BerkasLampiranRepository.java`, `src/main/java/id/ac/ub/layanan/repository/AntreanVerifikasiRepository.java`
  - Diubah: `PROGRESS.md`
  - Dihapus: Tidak ada (`.gitkeep` di `entity` dan `repository` sudah dihapus pada T01)
- Keputusan dan penyimpangan dari modul (beserta alasannya):
  - Ketiga berkas dari `R3` disalin identik, hanya komentar yang dihapus (sudah dibandingkan dengan `diff`).
  - `AntreanVerifikasiRepository` tidak ada di Modul 3. Repository ini ditambahkan sesuai cakupan T03 pada `docs/ai/PROMPT.md`.
- Perubahan di luar cakupan: Tidak ada
- Status build: Lulus (`./mvnw -q -DskipTests package`)
- Catatan untuk task berikutnya:
  - Konstruktor tanpa argumen di `BerkasLampiran` dan `AntreanVerifikasi` bersifat `protected` (sesuai Modul 3), jadi service di package lain tidak bisa memanggil `new BerkasLampiran()` atau `new AntreanVerifikasi()`. Task yang membuat baris baru (T10 untuk lampiran dan antrean, T16 untuk lampiran versi baru) perlu menambahkan konstruktor `public` atau factory method secara minimal, lalu mencatatnya di "Perubahan di luar cakupan". `Pengajuan` dari T04 kemungkinan punya pola yang sama dan dibutuhkan T08.
  - `AntreanVerifikasi.waktuMasuk` tidak punya `@PrePersist` dan tidak diisi otomatis, padahal kolomnya `NOT NULL`. Hibernate tetap menyertakan nilai `null` saat insert, sehingga MySQL akan menolaknya. T10 wajib mengisi `waktuMasuk` dengan `LocalDateTime.now(clock)` sebelum `save`.
  - `BerkasLampiran.tanggalUnggah` sudah terisi otomatis lewat `@PrePersist` (zona WIB).
  - Sisa `.gitkeep`: `controller`, `dto`, `service`, dan `src/test`.

### T04: Entity persyaratan dokumen dan pengajuan
- Tanggal: 2026-11-19
- Pelaksana: Rasil (MR)
- Ringkasan: Entity `PersyaratanDokumen` dan `Pengajuan` beserta `PersyaratanDokumenRepository` dan `PengajuanRepository` (SC07) disalin dari kode Modul 3 tanpa komentar.
- File dibuat atau diubah:
  - Dibuat: `src/main/java/id/ac/ub/layanan/entity/PersyaratanDokumen.java`, `src/main/java/id/ac/ub/layanan/entity/Pengajuan.java`, `src/main/java/id/ac/ub/layanan/repository/PengajuanRepository.java`, `src/main/java/id/ac/ub/layanan/repository/PersyaratanDokumenRepository.java`
  - Diubah: `PROGRESS.md`
  - Dihapus: Tidak ada (`.gitkeep` di `entity` dan `repository` sudah dihapus pada T01)
- Keputusan dan penyimpangan dari modul (beserta alasannya):
  - Keempat berkas disalin persis dari `docs/referensi/kode-modul-3/` dengan seluruh komentar dihapus.
- Perubahan di luar cakupan: Tidak ada
- Status build: Lulus (`.\mvnw.cmd -DskipTests package`)
- Catatan untuk task berikutnya:
  - `Pengajuan` sudah memiliki `@PrePersist` untuk `tanggalPengajuan` (zona WIB), method `simpanTenggat`, dan `updateStatusPengajuan`, namun konstruktor tanpa argumennya bersifat `protected` sesuai Modul 3; T08 yang membuat objek `Pengajuan` baru di service perlu menambahkan konstruktor `public` atau factory method secara minimal dan mencatatnya di "Perubahan di luar cakupan".
  - `PengajuanRepository` menyediakan `getDetailPengajuan`, `getDaftarAntrean`, `getDaftarByStatus`, dan `existsByIdPengajuanAndPenggunaIdPengguna`. Task T11 (`RiwayatController`/`RiwayatService`) yang membutuhkan daftar pengajuan milik seorang mahasiswa dapat menambahkan query method seperti `findByPenggunaIdPenggunaOrderByTanggalPengajuanDesc` saat mengerjakan T11 dan mencatatnya di "Perubahan di luar cakupan".
  - Sisa `.gitkeep`: `controller`, `dto` (akan dihapus pada T05), `service`, dan `src/test`.
