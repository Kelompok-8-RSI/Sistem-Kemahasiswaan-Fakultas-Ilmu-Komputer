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
Skrip ini membuat database `layanan_kemahasiswaan`. Password akun di `seed.sql` sudah dalam bentuk hash bcrypt.

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

---

## 5. Pembagian Implementasi (Modul 3)
Setup proyek (`pom.xml`, `application.properties`, kelas `LayananKemahasiswaanApplication`, skema database) sudah tersedia. Kode aplikasi dikerjakan per anggota sesuai laporan:

| Anggota | Kode Sumber | UI (templates) |
|---|---|---|
| **AA** | SC01 `AuthController`, `AuthService` · SC06 entity `AkunPengguna`, `JenisDokumen`, `AkunPenggunaRepository` | IM-01 `login.html` · IM-12 `fragments/layout.html`, `static/css/app.css` |
| **AT** | SC02 `TenggatController`, `TenggatService` · SC03 `AntreanController`, `AntreanService` · SC07 entity `Pengajuan`, `AntreanVerifikasi`, `PengajuanRepository` | IM-05 `form-tenggat.html` · IM-06 `antrean.html` |
| **MR** | SC04 `LegalisasiController`, `LegalisasiService` · SC09 entity `RiwayatStatus`, `RiwayatStatusRepository` · SC10 `AppConfig`, `WebConfig`, `RequiredRole`, `AuthInterceptor`, `DataTidakDitemukanException` | IM-09 `legalisasi-antrean.html`, `legalisasi-detail.html` |
| **JA** | SC05 `ValidasiLampiranController`, `ValidasiLampiranService` · SC08 entity `PersyaratanDokumen`, `BerkasLampiran`, repository-nya, `BerkasController` · SC12 paket `dto` | IM-07 `checklist.html` |

Hampir semua controller bergantung pada SC10 (`AppConfig`, `AuthInterceptor`, `RequiredRole`) dan pada kelas Entity. Karena itu, sebaiknya bagian tersebut di-merge lebih dulu.

### Alur kerja Git
1. Buat branch dari `main` untuk tiap bagian, misalnya `feat/auth`, `feat/tenggat`, atau `feat/legalisasi`.
2. Pastikan build lolos sebelum push:
   ```bash
   ./mvnw -q package -DskipTests
   ```
3. Buat Pull Request ke `main`.
