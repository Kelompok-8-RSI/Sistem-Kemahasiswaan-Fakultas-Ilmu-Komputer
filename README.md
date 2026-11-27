# Sistem Informasi Pengajuan Dokumen Kemahasiswaan FILKOM UB
**Praktikum Rekayasa Sistem Informasi (RSI) - Kelas SI-E - Kelompok 8**

## Anggota Kelompok
1. **Ayu Amirah Kusumaningtyas A.** - NIM. 255150407111070 (AA)
2. **Mohammad Rahardian Atsil Q.** - NIM. 255150401111028 (MR)
3. **M. Ammar F. Triwahyu** - NIM. 255150400111037 (AT)
4. **Jasmine Aqilah Rahmadia** - NIM. 255150400111053 (JA)

---

## 1. Arsitektur
Satu aplikasi **Spring Boot 3.3 (Java 17)** dengan arsitektur berlapis berbasis MVC. Halaman dirender oleh **Thymeleaf** di aplikasi yang sama, jadi tidak ada frontend atau API terpisah. Pembagian lapisan mengikuti pemodelan Boundary - Control - Entity pada Modul 2:

| Lapisan | Isi | Lokasi |
|---|---|---|
| Boundary (View) | Template Thymeleaf dan CSS | `src/main/resources/templates`, `static/css` |
| Control | Controller (Spring MVC) dan Service (aturan bisnis) | `.../layanan/controller`, `.../layanan/service` |
| Entity | Repository (Spring Data JPA) dan Entity JPA | `.../layanan/repository`, `.../layanan/entity` |
| Keamanan | `AuthInterceptor` (cek `sessionToken` dan peran lewat `@RequiredRole`), BCrypt | `.../layanan/security`, `.../layanan/config` |
| Database | MySQL 8, skema dari `database/schema.sql` | `database/` |
| Storage | Berkas lampiran yang diunggah | `uploads/` |

## 2. Struktur Proyek
```
.
├── pom.xml
├── mvnw, mvnw.cmd, .mvn/          Maven Wrapper (tidak perlu instal Maven)
├── database/
│   ├── schema.sql                 DDL 7 tabel (Modul 3 bagian 3.1)
│   └── seed.sql                   akun demo dan data contoh
├── uploads/                       storage berkas lampiran (isinya tidak di-commit)
└── src/main/
    ├── java/id/ac/ub/layanan/
    │   ├── LayananKemahasiswaanApplication.java
    │   ├── config/        AppConfig, WebConfig
    │   ├── security/      AuthInterceptor, RequiredRole
    │   ├── controller/    Auth, Profil, PemilihanDokumen, UnggahPersyaratan, Tenggat,
    │   │                  Riwayat, Antrean, ValidasiLampiran, Berkas, Pengajuan,
    │   │                  Legalisasi, RevisiPengajuan
    │   ├── service/       satu service untuk setiap controller fitur
    │   ├── repository/    AkunPengguna, JenisDokumen, PersyaratanDokumen, Pengajuan,
    │   │                  BerkasLampiran, AntreanVerifikasi, RiwayatStatus
    │   ├── entity/        7 entity sesuai tabel PDM
    │   ├── dto/           HasilAksi, HasilLogin, BarisAntrean, ItemChecklist, DataProfil, dst.
    │   └── exception/     DataTidakDitemukanException
    └── resources/
        ├── application.properties
        ├── static/        css/app.css, img/logo-filkom-ub.png
        └── templates/     login, profil, pilih-dokumen, form-tenggat, pengajuan-saya,
                           pengajuan-detail, revisi, antrean, checklist,
                           legalisasi-antrean, legalisasi-detail, fragments/layout
```

---

## 3. Prasyarat
1. **JDK 17 atau lebih baru.** Cek dengan `java -version`.
2. **MySQL Server 8.0** yang aktif di port 3306.
3. **Git.**

Maven tidak perlu diinstal karena proyek memakai Maven Wrapper (`mvnw` / `mvnw.cmd`).

## 4. Menjalankan Aplikasi

### Langkah 1: Buat database
Jalankan skema lalu seeder di MySQL (lewat CLI, Workbench, atau DBeaver):
```bash
mysql -u root -p < database/schema.sql
```
```bash
mysql -u root -p < database/seed.sql
```
Skrip ini membuat database `layanan_kemahasiswaan`. `schema.sql` mengikuti DDL Modul 3 apa adanya (tanpa `IF NOT EXISTS`), jadi hanya bisa dijalankan pada database yang belum berisi tabel. Untuk mengulang dari awal, misalnya setelah `seed.sql` diperbarui, hapus dulu databasenya:
```sql
DROP DATABASE layanan_kemahasiswaan;
```

### Langkah 2: Atur koneksi database
Nilai bawaan ada di `src/main/resources/application.properties`. Ubah lewat variabel lingkungan bila berbeda:

| Variabel | Bawaan |
|---|---|
| `DB_HOST` | `localhost` |
| `DB_NAME` | `layanan_kemahasiswaan` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | *(kosong)* |
| `UPLOAD_DIR` | `./uploads` |

Contoh di PowerShell (berlaku untuk terminal itu saja):
```powershell
$env:DB_PASSWORD = "password_mysql_kamu"
```

### Langkah 3: Jalankan
Windows (PowerShell / CMD):
```powershell
.\mvnw.cmd spring-boot:run
```
Git Bash, Linux, atau macOS:
```bash
./mvnw spring-boot:run
```
Setelah itu, buka `http://localhost:8080/login`.

Pertama kali dijalankan, wrapper akan mengunduh Maven dan dependensi, jadi butuh koneksi internet.

### Akun demo
Login memakai NIM atau NIP di kolom "NIM atau NIP". Semua akun memakai password `password123`.

| NIM / NIP | Nama | Peran | Halaman awal setelah login |
|---|---|---|---|
| `215150200111045` | Sal Priadi | Mahasiswa | `/pengajuan` |
| `235150400111058` | Fajar Nugroho | Mahasiswa | `/pengajuan` |
| `198705122015042001` | Dwi Lestari, S.E. | Staf Kemahasiswaan | `/antrean` |
| `197811032005011002` | Dr. Bayu Santoso, S.Kom., M.Kom. | Pejabat | `/legalisasi` |

### Data contoh di `seed.sql`
Semua pengajuan contoh milik Sal Priadi:

| No. | Jenis dokumen | Status | Keterangan |
|---|---|---|---|
| #1001 | Surat Rekomendasi Lomba | Sedang Diproses | Ada di antrean Staf. Satu lampiran ditandai `Tidak Lengkap`, cocok untuk mencoba status `Revisi` lalu alur revisi. |
| #1003 | Surat Tugas Delegasi Lomba | Menunggu Legalisasi Pejabat | Ada di antrean Pejabat. Cocok untuk mencoba Setuju atau Tolak. |
| #1004 | Surat Rekomendasi Lomba | Menunggu Verifikasi | Ada di antrean Staf. Lampiran belum diperiksa, cocok untuk mencoba checklist validasi. |

Tenggat semua data contoh sudah lewat, jadi di antrean Staf pengajuan itu berlabel "Terlewat". Akun Fajar Nugroho belum punya pengajuan, sehingga cocok untuk mencoba alur pengajuan dari awal.

**Berkas lampiran contoh tidak punya berkas fisik.** Lampiran #5001 sampai #5009 di `seed.sql` hanya berupa baris data dengan `pathFile` seperti `/uploads/1001/...`, sedangkan isi folder `uploads/` tidak ikut di-commit. Tombol untuk membuka berkas lampiran contoh akan menampilkan halaman "tidak ditemukan". Lampiran yang diunggah sendiri lewat aplikasi bisa dibuka seperti biasa.

---

## 5. Fitur dan Rute

| Use case | Fitur | Aktor | Rute |
|---|---|---|---|
| UC-01 | Login | Semua | `GET/POST /login` |
| UC-02 | Melihat dan memperbarui profil (email dan nomor telepon) | Semua | `GET/POST /profil`, mode ubah `GET /profil?ubah=true` |
| UC-03 | Memilih jenis dokumen | Mahasiswa | `GET/POST /pengajuan/baru` |
| UC-04 | Mengunggah, mengganti, dan menghapus persyaratan, lalu submit | Mahasiswa | `POST /pengajuan/{id}/lampiran`, `POST /pengajuan/{id}/lampiran/{idLampiran}/hapus` |
| UC-05 | Mencantumkan tenggat kebutuhan (termasuk konfirmasi tenggat mendesak) | Mahasiswa | `GET/POST /pengajuan/{id}/tenggat`, `POST /pengajuan/{id}/tenggat/konfirmasi` |
| UC-06 | Antrean pengajuan berdasarkan prioritas tenggat | Staf Kemahasiswaan | `GET /antrean` |
| UC-07 | Validasi kelengkapan lampiran (tanpa reload) dan membuka berkas | Staf Kemahasiswaan | `GET /pengajuan/{id}/checklist`, `POST /lampiran/{idLampiran}/validasi`, `GET /lampiran/{idLampiran}/berkas` |
| UC-08 | Memperbarui status pengajuan | Staf Kemahasiswaan | `POST /pengajuan/{id}/status` |
| UC-09 | Keputusan legalisasi (Setuju atau Tolak) | Pejabat | `GET /legalisasi`, `GET /legalisasi/{id}`, `POST /legalisasi/{id}/keputusan` |
| UC-10 | Merevisi pengajuan yang ditolak | Mahasiswa | `GET /pengajuan/{id}/revisi`, `POST /pengajuan/{id}/revisi/lampiran`, `POST /pengajuan/{id}/revisi/submit` |
| UC-11 | Pengajuan Saya dan riwayat status | Mahasiswa | `GET /pengajuan`, `GET /pengajuan/{id}`, `GET /pengajuan/{id}/lampiran/{idLampiran}` |
| UC-12 | Keluar dari akun | Semua | `POST /logout` |

Alur utama:
1. Mahasiswa memilih jenis dokumen, lalu mengunggah semua persyaratan wajib (PDF, JPG, atau PNG, maksimal 5 MB) dan mengisi tenggat.
2. Saat Mahasiswa submit, status menjadi `Menunggu Verifikasi` dan pengajuan masuk ke antrean Staf.
3. Staf memvalidasi lampiran, lalu memperbarui status menjadi `Sedang Diproses`, `Revisi`, `Menunggu Legalisasi Pejabat`, atau `Selesai`.
4. Untuk dokumen yang butuh legalisasi, Pejabat memilih Setuju (status menjadi `Selesai`) atau Tolak (status menjadi `Revisi`).
5. Mahasiswa merevisi lampiran yang ditandai, lalu mengirim ulang sehingga pengajuan kembali ke antrean Staf.

Setiap perubahan status tercatat di `RiwayatStatus` beserta pelaku dan waktunya (WIB).

---

## 6. Pembagian Implementasi
Implementasi kode dikerjakan secara bertahap (T01 sampai T18) mengikuti pembagian modul dan ketergantungan antarkomponen pada Modul 1, Modul 2, dan Modul 3.

| Task | Judul | Pemilik modul | Pelaksana | Use case / kode modul |
|---|---|---|---|---|
| T01 | Fondasi proyek, entity akun dan jenis dokumen | AA | Ammar (mengambil alih AA) | SC11, SC06 |
| T02 | Konfigurasi, keamanan, layout, dan CSS | MR | Rasil | SC10, IM-12 |
| T03 | Entity lampiran dan antrean verifikasi | AT | Ammar | SC08 |
| T04 | Entity persyaratan dokumen dan pengajuan | MR | Rasil | SC07 |
| T05 | Entity riwayat status dan paket DTO | JA | Jasmine | SC09, SC12 |
| T06 | Login ke sistem | AA | Amirah | UC-01, SC01, IM-01 |
| T07 | Keluar dari akun | MR | Rasil | UC-12, PC-45 |
| T08 | Memilih jenis dokumen | AA | Amirah | UC-03 |
| T09 | Mencantumkan tenggat kebutuhan | AT | Ammar | UC-05, SC02, IM-05 |
| T10 | Mengunggah persyaratan dan submit pengajuan | AA | Amirah | UC-04 |
| T11 | Riwayat status pengajuan (Pengajuan Saya) | MR | Rasil | UC-11 |
| T12 | Antrean pengajuan berdasarkan prioritas | AT | Ammar | UC-06, SC03, IM-06 |
| T13 | Validasi kelengkapan lampiran | JA | Jasmine | UC-07, SC05, IM-07 |
| T14 | Memperbarui status pengajuan | MR | Rasil | UC-08 |
| T15 | Keputusan legalisasi oleh Pejabat | MR | Rasil | UC-09, SC04, IM-09 |
| T16 | Merevisi pengajuan yang ditolak | JA | Jasmine | UC-10 |
| T17 | Melihat dan memperbarui profil | AT | Ammar | UC-02 |
| T18 | Integrasi akhir, README, dan penutupan | AT | Ammar | Semua |

### Alur kerja Git
1. Ambil kode terbaru dengan `git switch main` dan `git pull origin main`.
2. Buat branch baru dari `main` untuk setiap perubahan, misalnya `<nama>/<deskripsi>`.
3. Pastikan build lolos sebelum push:
   ```bash
   ./mvnw -q -DskipTests package
   ```
4. Buat Pull Request ke `main`.
