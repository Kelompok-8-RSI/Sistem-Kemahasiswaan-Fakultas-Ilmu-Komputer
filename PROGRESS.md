# PROGRESS Implementasi Kode Kelompok 8

Aturan dan rencana lengkap: `docs/ai/PROMPT.md`. Cara pakai untuk anggota: `docs/ai/CARA-PAKAI.md`.

## Status task

| Task | Judul | Pemilik | Pelaksana | Branch | Status |
|---|---|---|---|---|---|
| T01 | Fondasi proyek, workflow AI, entity akun dan jenis dokumen | AA | Ammar | ammar/t01-fondasi-entity-akun | Selesai |
| T02 | Konfigurasi, keamanan, layout, dan CSS | MR | Rasil | - | Belum |
| T03 | Entity lampiran dan antrean verifikasi | AT | Ammar | - | Belum |
| T04 | Entity persyaratan dokumen dan pengajuan | MR | Rasil | - | Belum |
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

T02: Konfigurasi, keamanan, layout, dan CSS. Pelaksana: Rasil (MR).

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
