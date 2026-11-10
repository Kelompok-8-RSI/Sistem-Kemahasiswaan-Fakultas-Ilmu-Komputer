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
| Database | MySQL 8, skema dari `database/schema.sql` | `database/` |
| Storage | Berkas lampiran yang diunggah | `uploads/` |

## 2. Struktur Proyek
```
.
├── pom.xml
├── mvnw, mvnw.cmd, .mvn/          Maven Wrapper (tidak perlu instal Maven)
├── database/
│   ├── schema.sql                 DDL 7 tabel (bagian 3.1)
│   └── seed.sql                   data awal
├── uploads/                       storage berkas lampiran
└── src/main/
    ├── java/id/ac/ub/layanan/
    │   ├── LayananKemahasiswaanApplication.java
    │   ├── config/        AppConfig, WebConfig
    │   ├── security/      AuthInterceptor, RequiredRole
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── entity/
    │   ├── dto/
    │   └── exception/     DataTidakDitemukanException
    └── resources/
        ├── application.properties
        ├── static/css/    app.css
        └── templates/     login, form-tenggat, antrean, checklist,
                           legalisasi-antrean, legalisasi-detail, fragments/layout
```
Folder yang masih kosong berisi `.gitkeep`. Hapus `.gitkeep` setelah folder tersebut berisi file.

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
Skrip ini membuat database `layanan_kemahasiswaan`. Password akun di `seed.sql` disimpan sebagai hash bcrypt. Daftar akunnya ada di bagian Akun demo di bawah.

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
Semua akun di `database/seed.sql` memakai password `password123`.

| Username | Peran |
|---|---|
| `sal.priadi` | Mahasiswa |
| `fajar.nugroho` | Mahasiswa |
| `dwi.lestari` | Staf Kemahasiswaan |
| `bayu.santoso` | Pejabat |

---

## 5. Pembagian Implementasi
Implementasi kode dikerjakan bergiliran dengan bantuan AI agent, satu task per sesi, dengan urutan T01 sampai T18. Urutan ini disusun supaya build tidak pernah rusak.

- Status terkini dan task berikutnya: [`PROGRESS.md`](PROGRESS.md)
- Cara pakai untuk anggota: [`docs/ai/CARA-PAKAI.md`](docs/ai/CARA-PAKAI.md)
- Aturan dan rencana lengkap untuk AI agent: [`docs/ai/PROMPT.md`](docs/ai/PROMPT.md)
- Sumber acuan (kode Modul 3, Modul 1, Modul 2, dan sketsa UI): [`docs/referensi/`](docs/referensi/)

| Task | Judul | Pemilik modul | Pelaksana | Use case / kode modul |
|---|---|---|---|---|
| T01 | Fondasi proyek, workflow AI, entity akun dan jenis dokumen | AA | Ammar (mengambil alih AA) | SC11, SC06 |
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
1. Pastikan task sebelumnya sudah di-merge ke `main`, lalu ambil kode terbaru dengan `git switch main` dan `git pull origin main`.
2. Ketik `lanjut` ke AI agent. Agent mengerjakan satu task dan memberikan perintah Git untuk diketik sendiri.
3. Setiap task memakai branch `<nama-pelaksana>/t<nomor>-<deskripsi>`, misalnya `rasil/t02-konfigurasi-keamanan-layout`.
4. Pastikan build lolos sebelum push:
   ```bash
   ./mvnw -q -DskipTests package
   ```
5. Buat Pull Request ke `main` dan merge sebelum anggota berikutnya mulai.
