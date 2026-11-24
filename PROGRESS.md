# PROGRESS Implementasi Kode Kelompok 8

Aturan dan rencana lengkap: `docs/ai/PROMPT.md`. Cara pakai untuk anggota: `docs/ai/CARA-PAKAI.md`.

## Status task

| Task | Judul | Pemilik | Pelaksana | Branch | Status |
|---|---|---|---|---|---|
| T01 | Fondasi proyek, workflow AI, entity akun dan jenis dokumen | AA | Ammar | ammar/t01-fondasi-entity-akun | Selesai |
| T02 | Konfigurasi, keamanan, layout, dan CSS | MR | Rasil | rasil/t02-konfigurasi-keamanan-layout | Selesai |
| T03 | Entity lampiran dan antrean verifikasi | AT | Ammar | ammar/t03-entity-lampiran-antrean | Selesai |
| T04 | Entity persyaratan dokumen dan pengajuan | MR | Rasil | rasil/t04-entity-persyaratan-pengajuan | Selesai |
| T05 | Entity riwayat status dan paket DTO | JA | Jasmine | jasmine/t05-entity-riwayat-dto | Selesai |
| T06 | Login ke sistem | AA | Amirah | amirah/t06-login | Selesai |
| T07 | Keluar dari akun | MR | Rasil | rasil/t07-logout | Selesai |
| T08 | Memilih jenis dokumen | AA | Amirah | amirah/t08-pilih-jenis-dokumen | Selesai |
| T09 | Mencantumkan tenggat kebutuhan | AT | Ammar | ammar/t09-tenggat-kebutuhan | Selesai |
| T10 | Mengunggah persyaratan dan submit pengajuan | AA | Amirah | amirah/t10-unggah-persyaratan | Selesai |
| T11 | Riwayat status pengajuan (Pengajuan Saya) | MR | Rasil | rasil/t11-riwayat-pengajuan | Selesai |
| T12 | Antrean pengajuan berdasarkan prioritas | AT | Ammar | ammar/t12-antrean-prioritas | Selesai |
| T13 | Validasi kelengkapan lampiran | JA | Jasmine | jasmine/t13-validasi-lampiran | Selesai |
| T14 | Memperbarui status pengajuan | MR | Rasil | - | Belum |
| T15 | Keputusan legalisasi oleh Pejabat | MR | Rasil | - | Belum |
| T16 | Merevisi pengajuan yang ditolak | JA | Jasmine | - | Belum |
| T17 | Melihat dan memperbarui profil | AT | Ammar | - | Belum |
| T18 | Integrasi akhir, README, dan penutupan | AT | Ammar | - | Belum |

## Task berikutnya

T14: Memperbarui status pengajuan. Pelaksana: Rasil (MR).

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

### T05: Entity riwayat status dan paket DTO
- Tanggal: 2026-11-21
- Pelaksana: Jasmine (JA)
- Ringkasan: Entity `RiwayatStatus` dan `RiwayatStatusRepository` (SC09) serta seluruh DTO (`HasilAksi`, `HasilLogin`, `BarisAntrean`, `HasilAntrean`, `ItemChecklist`, `HasilValidasiItem`, `DetailLegalisasi`, `BarisLegalisasi`, `LangkahRiwayat`) (SC12) disalin dari kode Modul 3 tanpa komentar. Berkas `.gitkeep` di folder `dto` dihapus.
- File dibuat atau diubah:
  - Dibuat: `src/main/java/id/ac/ub/layanan/entity/RiwayatStatus.java`, `src/main/java/id/ac/ub/layanan/repository/RiwayatStatusRepository.java`, `src/main/java/id/ac/ub/layanan/dto/BarisAntrean.java`, `src/main/java/id/ac/ub/layanan/dto/BarisLegalisasi.java`, `src/main/java/id/ac/ub/layanan/dto/DetailLegalisasi.java`, `src/main/java/id/ac/ub/layanan/dto/HasilAksi.java`, `src/main/java/id/ac/ub/layanan/dto/HasilAntrean.java`, `src/main/java/id/ac/ub/layanan/dto/HasilLogin.java`, `src/main/java/id/ac/ub/layanan/dto/HasilValidasiItem.java`, `src/main/java/id/ac/ub/layanan/dto/ItemChecklist.java`, `src/main/java/id/ac/ub/layanan/dto/LangkahRiwayat.java`
  - Diubah: `PROGRESS.md`
  - Dihapus: `src/main/java/id/ac/ub/layanan/dto/.gitkeep`
- Keputusan dan penyimpangan dari modul (beserta alasannya):
  - Seluruh berkas disalin persis dari `docs/referensi/kode-modul-3/` dengan seluruh komentar dihapus.
  - `RiwayatStatus` tetap `@Immutable` tanpa setter sesuai NFR-SRS-11.
- Perubahan di luar cakupan: Tidak ada
- Status build: Lulus (`.\mvnw.cmd -q -DskipTests package`)
- Catatan untuk task berikutnya:
  - DTO `HasilLogin` sudah tersedia dan siap dipakai oleh `AuthService` dan `AuthController` pada T06.
  - Sisa `.gitkeep`: `controller`, `service`, dan `src/test`.

### T06: Login ke sistem
- Tanggal: 2026-11-21
- Pelaksana: Amirah (AA)
- Ringkasan: Implementasi fitur login pengguna (UC-01) dengan validasi kredensial BCrypt (`AuthService.validasiKredensial`), penentuan peran (`AuthService.tentukanPeran`), pembuatan token sesi acak 64 karakter (`AuthService.buatSessionToken`), inisialisasi sesi HTTP baru beserta inisial avatar dan redirect dashboard sesuai peran pada `AuthController`, serta antarmuka `login.html` lengkap dengan alert pesan kesalahan, status keluar, sesi berakhir, dan dialog bantuan lupa password tanpa komentar.
- File dibuat atau diubah:
  - Dibuat: `src/main/java/id/ac/ub/layanan/controller/AuthController.java`, `src/main/java/id/ac/ub/layanan/service/AuthService.java`, `src/main/resources/templates/login.html`
  - Diubah: `PROGRESS.md`
  - Dihapus: `src/main/java/id/ac/ub/layanan/controller/.gitkeep`, `src/main/java/id/ac/ub/layanan/service/.gitkeep`
- Keputusan dan penyimpangan dari modul (beserta alasannya):
  - Mengikuti instruksi cakupan T06 pada `docs/ai/PROMPT.md`, hanya bagian login yang disalin ke `AuthController` dan `AuthService`. Endpoint `POST /logout` dan method `hancurkanSessionToken` tidak disertakan karena menjadi cakupan T07.
  - Seluruh berkas disalin dari `docs/referensi/kode-modul-3/` dengan seluruh komentar dihapus.
- Perubahan di luar cakupan: Tidak ada
- Status build: Lulus (`.\mvnw.cmd -q -DskipTests package`)
- Catatan untuk task berikutnya:
  - `AuthController` dan `AuthService` sudah siap ditambahkan endpoint `POST /logout` dan method `hancurkanSessionToken` pada T07.
  - Dialog konfirmasi keluar (`dlgKeluar`) sudah tersedia di `fragments/layout.html` dan mengarah ke `POST /logout`.
  - Sisa `.gitkeep`: `src/test` dan `uploads`.

### T07: Keluar dari akun
- Tanggal: 2026-11-22
- Pelaksana: Rasil (MR)
- Ringkasan: Fitur keluar dari akun (UC-12, PC-45) ditambahkan pada `AuthController` (`POST /logout` melalui `prosesLogout`) dan `AuthService` (`prosesLogout` dan `hancurkanSessionToken`) sesuai `docs/referensi/kode-modul-3/` tanpa komentar. Saat berhasil, `sessionToken` pada `AkunPengguna` dikosongkan di database, sesi HTTP di-invalidate, dan pengguna diarahkan ke `/login?keluar`; jika gagal, `pesanError` dikirim lewat flash attribute dan pengguna diarahkan kembali ke dashboard peran.
- File dibuat atau diubah:
  - Dibuat: Tidak ada
  - Diubah: `src/main/java/id/ac/ub/layanan/controller/AuthController.java`, `src/main/java/id/ac/ub/layanan/service/AuthService.java`, `PROGRESS.md`
  - Dihapus: Tidak ada
- Keputusan dan penyimpangan dari modul (beserta alasannya):
  - Bagian logout pada `AuthController` dan `AuthService` disalin persis dari `docs/referensi/kode-modul-3/` dengan seluruh komentar dihapus.
  - Dialog konfirmasi keluar (`dlgKeluar`) di `fragments/layout.html` yang mengarah ke `POST /logout` dan pesan sukses `?keluar` di `login.html` sudah tersedia dari T02 dan T06.
- Perubahan di luar cakupan: Tidak ada
- Status build: Lulus (`.\mvnw.cmd -DskipTests package`)
- Catatan untuk task berikutnya:
  - Alur autentikasi (UC-01 login dan UC-12 logout) sudah lengkap dan tersambung dengan `AuthInterceptor` serta `fragments/layout.html`.
  - Untuk T08 (UC-03 memilih jenis dokumen), ingat catatan T04: konstruktor tanpa argumen di `Pengajuan` bersifat `protected` sesuai Modul 3, sehingga T08 perlu menambahkan konstruktor `public` atau factory method secara minimal pada `Pengajuan` (dicatat di "Perubahan di luar cakupan") agar `PemilihanDokumenService` dapat membuat objek `Pengajuan` baru.
  - Sisa `.gitkeep`: `src/test` dan `uploads`.

### T08: Memilih jenis dokumen
- Tanggal: 2026-11-22
- Pelaksana: Amirah (AA)
- Ringkasan: Fitur pemilihan jenis dokumen kemahasiswaan (UC-03) diimplementasikan melalui pembuatan `JenisDokumenRepository` dengan method query kategori dan jenis dokumen, `PemilihanDokumenService` untuk menampilkan daftar kategori kebutuhan (PC-08), sub-jenis dokumen berdasarkan kategori terpilih (PC-09), serta penyimpanan record pengajuan baru berstatus Draft dengan tanggal pengajuan dari Clock WIB (PC-10). Controller `PemilihanDokumenController` menangani request GET dan POST di rute `/pengajuan/baru` dengan proteksi `@RequiredRole("Mahasiswa")`, serta template `pilih-dokumen.html` mengimplementasikan tampilan UI-03 dengan stepper langkah 1 "Pilih Jenis", dropdown kategori & jenis, kartu ringkasan estimasi dan legalisasi dokumen, serta daftar persyaratan wajib tanpa komentar.
- File dibuat atau diubah:
  - Dibuat: `src/main/java/id/ac/ub/layanan/repository/JenisDokumenRepository.java`, `src/main/java/id/ac/ub/layanan/service/PemilihanDokumenService.java`, `src/main/java/id/ac/ub/layanan/controller/PemilihanDokumenController.java`, `src/main/resources/templates/pilih-dokumen.html`
  - Diubah: `src/main/java/id/ac/ub/layanan/entity/Pengajuan.java`, `src/main/resources/static/css/app.css`, `PROGRESS.md`
  - Dihapus: Tidak ada
- Keputusan dan penyimpangan dari modul (beserta alasannya):
  - Daftar kategori pada `PemilihanDokumenService` (`DAFTAR_KATEGORI`) menyertakan 5 kategori resmi sesuai DDL dan Modul 2 (`Lomba`, `Beasiswa`, `Organisasi`, `Akademik/PKL`, `Lainnya`), sehingga pilihan "Lainnya" tetap muncul di dropdown dan menampilkan pesan arahan kategori tidak tersedia sesuai Alternative Flow 1 dan PC-09.
- Perubahan di luar cakupan:
  - Mengubah visibility konstruktor tanpa argumen di `src/main/java/id/ac/ub/layanan/entity/Pengajuan.java` dari `protected` menjadi `public` agar service dapat menginstansiasi objek pengajuan baru saat submit jenis dokumen (sesuai catatan T04).
  - Menambahkan styling `.ringkasan-jenis`, `.daftar-syarat`, dan `select:disabled` di akhir `src/main/resources/static/css/app.css` menggunakan variabel warna yang sudah ada untuk mendukung tata letak antarmuka UI-03.
- Status build: Lulus (`.\mvnw.cmd -q -DskipTests package`)
- Catatan untuk task berikutnya:
  - Setelah `POST /pengajuan/baru` berhasil, pengguna dialihkan ke `/pengajuan/{id}/tenggat`. Rute, controller, dan form tenggat ini akan diimplementasikan pada T09 (`TenggatController`, `TenggatService`, dan `form-tenggat.html`).
  - Sisa `.gitkeep`: `src/test` dan `uploads`.

### T09: Mencantumkan tenggat kebutuhan
- Tanggal: 2026-11-22
- Pelaksana: Ammar (AT)
- Ringkasan: Fitur mencantumkan tenggat kebutuhan (UC-05) ditambahkan: `TenggatController` (`GET /pengajuan/{idPengajuan}/tenggat`, `POST /pengajuan/{idPengajuan}/tenggat`, `POST /pengajuan/{idPengajuan}/tenggat/konfirmasi`), `TenggatService` (PC-17 `prosesTenggat`, PC-18 `validasiTanggalTenggat`, PC-19 `hitungSisaHariKerja`, PC-20 `konfirmasiTenggat`), dan template `form-tenggat.html` (UI-05, UI-05a error, UI-05b mendesak, dan tampilan "Pengajuan berhasil dikirim"). Semua disalin dari kode Modul 3 tanpa komentar.
- File dibuat atau diubah:
  - Dibuat: `src/main/java/id/ac/ub/layanan/controller/TenggatController.java`, `src/main/java/id/ac/ub/layanan/service/TenggatService.java`, `src/main/resources/templates/form-tenggat.html`
  - Diubah: `PROGRESS.md`
  - Dihapus: Tidak ada
- Keputusan dan penyimpangan dari modul (beserta alasannya):
  - Ketiga berkas identik dengan `R3` setelah komentar dihapus (sudah dibandingkan dengan `diff`). Komentar di akhir baris (`getDataPengajuan(...); // ...` dan `BATAS_HARI_MENDESAK = 3; // ...`) juga dihapus.
  - Komentar penanda kartu "Persyaratan dokumen" di kolom kiri `form-tenggat.html` diganti dengan elemen kosong `<div id="slotPersyaratan"></div>` sesuai cakupan T09.
  - Sketsa UI-05 masih menulis "maksimal 2 MB" dan "tidak boleh lebih awal dari hari ini". Template mengikuti Modul 3 dan keputusan tim (tanggal pengajuan sebagai batas). Batas 5 MB menjadi urusan T10.
- Perubahan di luar cakupan: Tidak ada
- Status build: Lulus (`./mvnw -q -DskipTests package`). Aplikasi tidak dijalankan karena kredensial MySQL lokal tidak tersedia untuk agent.
- Catatan untuk task berikutnya:
  - Untuk T10: sesuai Modul 3, `prosesTenggat` (PC-17) dan `konfirmasiTenggat` (PC-20) saat ini langsung mengubah status menjadi `Menunggu Verifikasi`, tetapi belum membuat baris `AntreanVerifikasi` dan belum mencatat `RiwayatStatus`. T10 perlu menyatukan jalur submit (PC-14 cek kelengkapan, lalu PC-17 sampai PC-20, lalu PC-16 `submitPengajuan`) sehingga perubahan status, pembuatan antrean, dan `catatLog` terjadi di satu tempat. Catat perubahan pada `TenggatService` dan `TenggatController` di "Perubahan di luar cakupan".
  - Tombol "Submit Pengajuan" di kartu Ringkasan memakai `form="formTenggat"` dan saat ini hanya dinonaktifkan ketika `tenggatMendesak`. T10 perlu menambahkan syarat nonaktif selama persyaratan wajib belum lengkap.
  - Kartu "Persyaratan dokumen" dipasang pada `<div id="slotPersyaratan"></div>` di kolom kiri `form-tenggat.html`.
  - Ingat catatan T03: `AntreanVerifikasi.waktuMasuk` harus diisi dengan `LocalDateTime.now(clock)` sebelum `save`, dan konstruktor `BerkasLampiran` serta `AntreanVerifikasi` masih `protected`.
  - Tampilan setelah submit memakai flash attribute `terkirim` dan menautkan ke `/pengajuan/{id}` serta `/pengajuan`. Kedua rute itu dibuat pada T11.
  - Sisa `.gitkeep`: `src/test` dan `uploads`.

### T10: Mengunggah persyaratan dan submit pengajuan
- Tanggal: 2026-11-22
- Pelaksana: Amirah (AA)
- Ringkasan: Fitur unggah persyaratan (UC-04) ditambahkan melalui `UnggahPersyaratanController` dan `UnggahPersyaratanService`. Mahasiswa dapat mengunggah, mengganti, dan menghapus lampiran PDF/JPG/PNG hingga 5 MB untuk setiap persyaratan wajib, serta melihat status dan ukuran berkas pada kartu Persyaratan dokumen di form tenggat. Submit kini memeriksa kelengkapan, menyimpan tenggat, membuat antrean `Menunggu`, dan mencatat `RiwayatStatus` saat status berubah menjadi `Menunggu Verifikasi`.
- File dibuat atau diubah:
  - Dibuat: `src/main/java/id/ac/ub/layanan/controller/UnggahPersyaratanController.java`, `src/main/java/id/ac/ub/layanan/service/UnggahPersyaratanService.java`
  - Diubah: `src/main/java/id/ac/ub/layanan/controller/TenggatController.java`, `src/main/java/id/ac/ub/layanan/service/TenggatService.java`, `src/main/java/id/ac/ub/layanan/entity/BerkasLampiran.java`, `src/main/java/id/ac/ub/layanan/entity/AntreanVerifikasi.java`, `src/main/resources/templates/form-tenggat.html`, `src/main/resources/static/css/app.css`, `PROGRESS.md`
  - Dihapus: Tidak ada
- Keputusan dan penyimpangan dari modul (beserta alasannya):
  - Berkas yang diganti sebelum pengajuan disubmit menghapus berkas fisik dan record lampiran aktif sebelumnya, lalu menyimpan lampiran pengganti sebagai versi 1. Riwayat versi penggantian untuk pengajuan berstatus `Revisi` tetap menjadi cakupan T16.
- Perubahan di luar cakupan:
  - Mengubah konstruktor tanpa argumen `BerkasLampiran` dan `AntreanVerifikasi` menjadi `public` agar T10 dapat membuat entity baru, sesuai catatan T03.
  - Menyesuaikan `TenggatController` dan `TenggatService` agar validasi tenggat tidak lagi langsung mengubah status. Status, antrean, dan riwayat kini dibuat melalui satu jalur submit di `UnggahPersyaratanService`, sehingga tidak ada pengajuan berstatus `Menunggu Verifikasi` tanpa antrean atau log.
- Status build: Lulus (`mvn -q -DskipTests package` melalui instalasi Maven lokal). Maven Wrapper tidak dapat dipakai di lingkungan agent karena path profil Windows mengandung spasi.
- Catatan untuk task berikutnya:
  - T11 dapat memakai rute yang sudah ditautkan pada tampilan berhasil submit: `GET /pengajuan` dan `GET /pengajuan/{idPengajuan}`.
  - Berkas lampiran disimpan di direktori `app.upload-dir` dengan path absolut. Endpoint untuk menampilkan berkas kepada Staf menjadi cakupan T13 (`BerkasController`).
  - Sisa `.gitkeep`: `src/test` dan `uploads`.

### T11: Riwayat status pengajuan (Pengajuan Saya)
- Tanggal: 2026-11-22
- Pelaksana: Rasil (MR)
- Ringkasan: Implementasi halaman Pengajuan Saya (`GET /pengajuan`) dan detail riwayat status (`GET /pengajuan/{idPengajuan}`) untuk Mahasiswa (UC-11, PC-42 sampai PC-44) melalui `RiwayatController`, `RiwayatService`, `pengajuan-saya.html`, dan `pengajuan-detail.html` tanpa komentar. Halaman daftar menampilkan peringatan revisi, tab penyaringan status, tabel pengajuan milik mahasiswa yang login, serta empty state dengan tombol Ajukan Dokumen; halaman detail menampilkan timeline riwayat status kronologis (terbaru di atas) lengkap dengan catatan perbaikan dan tombol `Perbaiki Pengajuan` ke `/pengajuan/{id}/revisi` untuk status `Revisi`, ringkasan pengajuan, serta daftar lampiran yang dapat dibuka pemiliknya melalui `GET /pengajuan/{idPengajuan}/lampiran/{idLampiran}`. Pengajuan berstatus `Draft` diarahkan kembali ke `/pengajuan/{id}/tenggat`.
- File dibuat atau diubah:
  - Dibuat: `src/main/java/id/ac/ub/layanan/controller/RiwayatController.java`, `src/main/java/id/ac/ub/layanan/service/RiwayatService.java`, `src/main/resources/templates/pengajuan-saya.html`, `src/main/resources/templates/pengajuan-detail.html`
  - Diubah: `src/main/java/id/ac/ub/layanan/repository/PengajuanRepository.java`, `src/main/resources/static/css/app.css`, `PROGRESS.md`
  - Dihapus: Tidak ada
- Keputusan dan penyimpangan dari modul (beserta alasannya):
  - Method `RiwayatService.urutkanKronologis` (PC-44) tetap mengurutkan `RiwayatStatus` secara kronologis (ascending berdasarkan `waktu`), lalu `requestRiwayatStatus` menyusun `LangkahRiwayat` dengan urutan tahapan terbaru paling atas untuk tampilan timeline agar konsisten dengan sketsa `UI-11-001.jpg` ("Tahapan terbaru ditampilkan paling atas") dan pola `LegalisasiService`/`legalisasi-detail.html` pada Modul 3.
  - Ditambahkan endpoint `GET /pengajuan/{idPengajuan}/lampiran/{idLampiran}` pada `RiwayatController` (khusus pemilik dengan proteksi path traversal) agar tautan "Lihat" pada kartu Lampiran di `UI-11-001.jpg` dapat diakses Mahasiswa, mengingat `BerkasController` (T13) hanya untuk Staf Kemahasiswaan dan Pejabat.
  - Pada baris tabel berstatus `Revisi` di `pengajuan-saya.html`, disediakan tautan `Perbaiki` (`/pengajuan/{id}/revisi`) sekaligus `Lihat detail` (`/pengajuan/{id}`) agar mahasiswa dapat langsung memperbaiki maupun meninjau timeline riwayatnya.
- Perubahan di luar cakupan:
  - Menambahkan query `getPengajuanByMahasiswa` pada `src/main/java/id/ac/ub/layanan/repository/PengajuanRepository.java` (sesuai catatan T04 dan PC-42) dengan `join fetch` ke `pengguna` dan `jenisDokumen`.
  - Menambahkan gaya `.badge.draft`, `tr.baris-revisi`, warna penanda status pada `.timeline li`, dan `.lampiran-ringkas` di bagian akhir `src/main/resources/static/css/app.css` memakai variabel warna yang sudah ada untuk mendukung tampilan UI-11.
- Status build: Lulus (`.\mvnw.cmd -DskipTests package`)
- Catatan untuk task berikutnya:
  - Rute dashboard Mahasiswa (`GET /pengajuan`) dan detail pengajuan (`GET /pengajuan/{idPengajuan}`) sudah tersedia; pengajuan berstatus `Revisi` sudah memiliki tombol menuju `/pengajuan/{id}/revisi` yang akan diimplementasikan pada T16.
  - Sisa `.gitkeep`: `src/test` dan `uploads`.

### T12: Antrean pengajuan berdasarkan prioritas
- Tanggal: 2026-11-23
- Pelaksana: Ammar (AT)
- Ringkasan: Halaman antrean Staf Kemahasiswaan (UC-06) ditambahkan melalui `AntreanController` (`GET /antrean`, `@RequiredRole("Staf Kemahasiswaan")`), `AntreanService` (PC-21 `requestAntreanPengajuan`, PC-22 `hitungSisaWaktu`, PC-23 `urutkanAntrean`, PC-24 `beriLabelPrioritas`, memakai `Clock` WIB), dan template `antrean.html` (UI-06 tabel prioritas dengan tab status, filter kategori, dan pencarian, serta UI-06b antrean kosong). Semua disalin dari kode Modul 3 tanpa komentar.
- File dibuat atau diubah:
  - Dibuat: `src/main/java/id/ac/ub/layanan/controller/AntreanController.java`, `src/main/java/id/ac/ub/layanan/service/AntreanService.java`, `src/main/resources/templates/antrean.html`
  - Diubah: `PROGRESS.md`
  - Dihapus: Tidak ada
- Keputusan dan penyimpangan dari modul (beserta alasannya):
  - Ketiga berkas identik dengan `R3` setelah komentar dihapus (sudah dibandingkan dengan `diff`), termasuk komentar di akhir baris `BATAS_MERAH` dan `BATAS_KUNING` dan komentar `//` di dalam `<script>`.
  - Berkas `docs/referensi/sketsa-ui/UI-06-000.jpg` ternyata berisi gambar UI-05b (tenggat mendesak), bukan halaman antrean. Tampilan antrean mengikuti `antrean.html` dari Modul 3, yang berada di urutan prioritas lebih tinggi daripada sketsa.
  - Pengajuan hasil submit T10 (status `Menunggu Verifikasi`, `AntreanVerifikasi.statusAntrean = Menunggu`, `tanggalTenggat` terisi) sudah memenuhi kondisi query `PengajuanRepository.getDaftarAntrean`, sehingga muncul di antrean tanpa perubahan kode lain. Data seed 1001 (`Diproses`) dan 1004 (`Menunggu`) juga muncul dengan label "Terlewat" karena tenggatnya sudah lewat.
- Perubahan di luar cakupan: Tidak ada
- Status build: Lulus (`./mvnw -q -DskipTests package`). Aplikasi tidak dijalankan karena kredensial MySQL lokal tidak tersedia untuk agent.
- Catatan untuk task berikutnya:
  - Tombol "Proses" di setiap baris antrean menautkan ke `GET /pengajuan/{idPengajuan}/checklist`, yang dibuat pada T13 (`ValidasiLampiranController`).
  - `ValidasiLampiranController` di Modul 3 memakai `AntreanService.hitungSisaWaktu` dan `beriLabelPrioritas` untuk kartu sisa waktu di halaman checklist. Kedua method itu sudah `public` dan siap dipakai.
  - Sisa `.gitkeep`: `src/test` dan `uploads`.

### T13: Validasi kelengkapan lampiran
- Tanggal: 2026-11-24
- Pelaksana: Jasmine (JA)
- Ringkasan: Fitur validasi kelengkapan berkas lampiran oleh Staf Kemahasiswaan (UC-07) diimplementasikan melalui `ValidasiLampiranController` (`GET /pengajuan/{idPengajuan}/checklist` dan `POST /lampiran/{idLampiran}/validasi`), `ValidasiLampiranService` (PC-25 `requestChecklist`, PC-26 `susunChecklist`, PC-27 `prosesValidasiItem`, PC-28 `validasiInputItem`, PC-29 `hitungHasilValidasi`), `BerkasController` (`GET /lampiran/{idLampiran}/berkas` untuk membaca berkas fisik dari storage), dan template `checklist.html` (UI-07 antarmuka penilaian kelengkapan lampiran, badge prioritas tenggat, form catatan alasan, penilaian interaktif tanpa reload halaman, dan toast notifikasi). Semua berkas disalin dari kode Modul 3 tanpa komentar.
- File dibuat atau diubah:
  - Dibuat: `src/main/java/id/ac/ub/layanan/controller/ValidasiLampiranController.java`, `src/main/java/id/ac/ub/layanan/controller/BerkasController.java`, `src/main/java/id/ac/ub/layanan/service/ValidasiLampiranService.java`, `src/main/resources/templates/checklist.html`
  - Diubah: `PROGRESS.md`
  - Dihapus: Tidak ada
- Keputusan dan penyimpangan dari modul (beserta alasannya):
  - Seluruh berkas disalin persis dari `docs/referensi/kode-modul-3/` dengan seluruh komentar dihapus.
  - Komentar penanda kartu "Perbarui status" di kolom kanan `checklist.html` diganti dengan elemen kosong `<div id="slotPerbaruiStatus"></div>` sesuai cakupan T13 untuk diisi pada T14.
  - Pada inline JavaScript di `checklist.html`, penulisan template natural Thymeleaf diubah menjadi `const idPengajuan = [[${pengajuan.idPengajuan}]];` sesuai ketentuan format tanpa komentar.
- Perubahan di luar cakupan: Tidak ada
- Status build: Lulus (`.\mvnw.cmd -q -DskipTests package`)
- Catatan untuk task berikutnya:
  - Kartu "Perbarui status" (UI-08) pada T14 dapat dipasang pada `<div id="slotPerbaruiStatus"></div>` di kolom kanan `checklist.html`.
  - Hasil validasi lampiran (`Lengkap`, `Tidak Lengkap`, `Belum Selesai`) dari `ValidasiLampiranService.hitungHasilValidasi` dapat langsung dimanfaatkan oleh T14 (`PengajuanService`) sebagai syarat penentuan status pengajuan berikutnya.
  - Sisa `.gitkeep`: `src/test` dan `uploads`.
