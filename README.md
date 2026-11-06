# Sistem Informasi Pengajuan Dokumen Kemahasiswaan FILKOM UB
**Praktikum Rekayasa Sistem Informasi (RSI) - Kelas SI-E - Kelompok 8**

## Anggota Kelompok
1. **Ayu Amirah Kusumaningtyas A.** - NIM. 255150407111070 (AA)
2. **Mohammad Rahardian Atsil Q.** - NIM. 255150401111028 (MR)
3. **M. Ammar F. Triwahyu** - NIM. 255150400111037 (AT)
4. **Jasmine Aqilah Rahmadia** - NIM. 255150400111053 (JA)

---

## 1. Arsitektur Proyek (Monorepo)
Proyek ini mengadopsi pola arsitektur **Boundary - Control - Entity (BCE)** menggunakan struktur monorepo terpadu:
- **`apps/web` (Boundary)**: Antarmuka pengguna berbasis **Next.js 14 (App Router)**, React, TypeScript, dan Tailwind CSS.
- **`apps/api` (Control & Entity)**: Layanan REST API berbasis **Node.js, Express, dan TypeScript** yang memproses logika bisnis controller serta berinteraksi dengan database MySQL menggunakan driver `mysql2`.
- **`packages/shared`**: Definisi tipe data (types/interfaces) dan skema validasi yang digunakan bersama oleh frontend dan backend.
- **`packages/database`**: Berisi skrip DDL (`schema.sql`) dan dummy data (`seed.sql`).

---

## 2. Prasyarat Sistem (Prerequisites)
Pastikan perangkat Anda telah terpasang perangkat lunak berikut:
1. **Node.js** (versi 18.x, 20.x, atau 24.x LTS)  
   Unduh di: [https://nodejs.org/](https://nodejs.org/)  
   Verifikasi di terminal: `node -v`
2. **pnpm** (Package manager monorepo)  
   Instalasi via npm: `npm install -g pnpm`  
   Verifikasi: `pnpm -v`
3. **MySQL Server 8.0** (atau XAMPP / MariaDB)  
   Pastikan service MySQL aktif pada port 3306.
4. **Git**  
   Verifikasi: `git --version`

---

## 3. Langkah-Langkah Setup & Menjalankan Proyek

### Langkah 1: Clone / Buka Direktori Proyek
```bash
cd "c:\Users\ammar\semester 3\rsi\praktikum\Sistem-Kemahasiswaan-Fakultas-Ilmu-Komputer"
```

### Langkah 2: Setup Database MySQL
Buka MySQL CLI atau aplikasi GUI seperti DBeaver / phpMyAdmin / MySQL Workbench, lalu jalankan:
```sql
-- 1. Jalankan skrip pembuatan tabel (DDL)
SOURCE packages/database/schema.sql;

-- 2. Jalankan skrip seeder data awal
SOURCE packages/database/seed.sql;
```
*Atau via terminal PowerShell:*
```powershell
mysql -u root -p < packages/database/schema.sql
mysql -u root -p < packages/database/seed.sql
```

### Langkah 3: Konfigurasi Environment Variable
Salin file `.env.example` pada `apps/api`:
```bash
cp apps/api/.env.example apps/api/.env
```
Sesuaikan kredensial database pada `apps/api/.env`:
```env
PORT=5000
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=kemahasiswaan_filkom
CORS_ORIGIN=http://localhost:3000
```

### Langkah 4: Instalasi Dependensi (pnpm install)
Jalankan perintah instalasi dari root folder:
```bash
pnpm install
```

### Langkah 5: Menjalankan Aplikasi
Untuk menjalankan frontend Next.js dan backend Express secara bersamaan:
```bash
pnpm dev
```
Atau jika ingin menjalankan secara terpisah:
- **Jalankan Backend (API):**
  ```bash
  pnpm --filter api run dev
  # API berjalan di: http://localhost:5000
  ```
- **Jalankan Frontend (Web):**
  ```bash
  pnpm --filter web run dev
  # Web berjalan di: http://localhost:3000
  ```

---

## 4. Tautan Halaman Implementasi
Buka peramban (browser) di `http://localhost:3000`:
- **Portal Utama**: `http://localhost:3000`
- **[AA] [UI-03] Form Pemilihan Dokumen**: `http://localhost:3000/ajukan/pilih-dokumen`
- **[AT] [UI-05] Form Pengajuan – Tenggat Kebutuhan**: `http://localhost:3000/ajukan/tenggat`
- **[JA] [UI-07] Checklist Validasi Kelengkapan Lampiran**: `http://localhost:3000/staf/pengajuan/1001/validasi`
- **[MR] [UI-08] Halaman Detail Pengajuan – Perbarui Status**: `http://localhost:3000/staf/pengajuan/1001/perbarui-status`
