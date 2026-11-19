# PROMPT IMPLEMENTASI KODE: SISTEM PENGAJUAN DOKUMEN KEMAHASISWAAN FILKOM UB

Praktikum Rekayasa Sistem Informasi, Kelas SI-E, Kelompok 8.
Dokumen ini adalah instruksi lengkap untuk AI agent (Claude Code, Codex, Antigravity, Cursor, Gemini CLI, atau agent lain). Baca seluruhnya sebelum mengubah file apa pun. Jika ada instruksi yang bertentangan dengan kebiasaan bawaanmu, ikuti dokumen ini.

---

## 1. Peranmu

Kamu adalah software engineer yang mengimplementasikan kode proyek ini **sesuai laporan Modul 1, Modul 2, dan Modul 3** milik kelompok. Pekerjaan dibagi menjadi task berurutan (T01 sampai T18). Setiap sesi, kamu mengerjakan **tepat satu task**, lalu:

1. memperbarui `PROGRESS.md`,
2. memberikan perintah Git (branch, add, commit, push) untuk diketik sendiri oleh anggota,
3. memberi tahu kepada siapa pekerjaan dilanjutkan (Ammar, Amirah, Jasmine, atau Rasil).

Anggota berikutnya cukup mengetik `lanjut` kepada AI agent miliknya, dan agent tersebut melanjutkan dari `PROGRESS.md`.

---

## 2. Konteks proyek

- **Repository:** https://github.com/Kelompok-8-RSI/Sistem-Kemahasiswaan-Fakultas-Ilmu-Komputer
- **Stack (wajib, tidak boleh diganti):** Spring Boot 3.3.5, Java 17, Spring Web MVC, Thymeleaf, Spring Data JPA (Hibernate), MySQL 8, `spring-security-crypto` (BCrypt saja, tanpa Spring Security penuh), Maven Wrapper (`mvnw`, `mvnw.cmd`).
- **Arsitektur:** satu aplikasi berlapis Boundary-Control-Entity. View berupa template Thymeleaf, Control berupa Controller dan Service, Entity berupa Entity JPA dan Repository. Tidak ada frontend atau REST API terpisah.
- **Package dasar:** `id.ac.ub.layanan` dengan subpackage `config`, `security`, `controller`, `service`, `repository`, `entity`, `dto`, `exception`.
- **Sistem operasi tim:** kebanyakan Windows (PowerShell atau Git Bash). Tulis perintah yang berjalan di keduanya.

### 2.1 Anggota tim

| Inisial | Nama lengkap | Nama panggilan | Prefix branch |
|---|---|---|---|
| AA | Ayu Amirah Kusumaningtyas A. | Amirah | `amirah/` |
| MR | Mohammad Rahardian Atsil Q. | Rasil | `rasil/` |
| AT | M. Ammar F. Triwahyu | Ammar | `ammar/` |
| JA | Jasmine Aqilah Rahmadia | Jasmine | `jasmine/` |

Saat menyebut orang dalam laporan, pakai nama panggilan dan inisial, misalnya "Rasil (MR)". Nama "Amira" dan "Amirah" merujuk ke orang yang sama.

---

## 3. Sumber kebenaran

Semua referensi ada di folder `docs/referensi/` di dalam repository.

| Berkas | Isi | Cara pakai |
|---|---|---|
| `kode-modul-3/` | Salinan **persis** kode sumber pada laporan Modul 3 (bagian 3.1 sampai 3.3), dengan struktur folder sama seperti proyek | Sumber utama untuk semua file yang sudah ditulis di Modul 3. Salin isinya, lalu hapus seluruh komentar. |
| `modul-3.md` | Ekspor laporan Modul 3 (arsitektur, pemetaan use case ke controller, penyesuaian pseudo-code, tabel traceability) | Baca untuk konteks dan keputusan desain. **Jangan ambil kode dari sini**: blok kode pada ekspor ini rusak (baris tergabung dan karakter ter-escape). |
| `modul-2.pdf` dan `modul-2.txt` | Modul 2: use case scenario, activity diagram, sequence diagram (SEQ), class diagram (CD), pseudo-code PC-01 sampai PC-45, sketsa UI | Sumber logika untuk use case yang belum ada kodenya di Modul 3. `.txt` adalah teks hasil ekstraksi dari PDF. |
| `modul-1.pdf` dan `modul-1.txt` | Modul 1: SRS, user requirement, system requirement (SR-01 sampai SR-25), kebutuhan non-fungsional (NFR-SRS-xx) | Sumber aturan bisnis, validasi, dan batasan. |
| `sketsa-ui/UI-xx-nnn.jpg` | Gambar hi-fi setiap UI dari Modul 2 bagian 2.7 (UI-01 sampai UI-12), sesuai desain Figma page "02 Hi-Fi FIX" | Acuan tampilan untuk UI yang belum ada template-nya di Modul 3. Teks keterangan gambar ada di `modul-2.txt` bagian 2.7. |

### 3.1 Urutan prioritas jika ada perbedaan

1. Keputusan yang tercatat di bagian 3.2 di bawah ini.
2. `docs/referensi/kode-modul-3/`.
3. `modul-3.md`.
4. Modul 2 (pseudo-code, sequence diagram, class diagram, use case scenario, sketsa UI).
5. Modul 1 (SRS dan NFR).
6. Kode yang sudah ada di repository dari task sebelumnya (tetap konsisten dengannya).

Jika desain Figma atau sketsa UI berbeda dengan dokumen, **ikuti dokumen**.

### 3.2 Keputusan yang sudah ditetapkan tim

- Setelah Pejabat menolak legalisasi, status pengajuan menjadi **Revisi** (bukan "Sedang Diproses").
- Ukuran maksimal setiap berkas lampiran **5 MB** (NFR-SRS-05), bukan 2 MB.
- Tanggal tenggat tidak boleh lebih awal dari **tanggal pengajuan** (bukan "hari ini").
- Dialog Lupa password (OV-15) berisi: judul "Lupa password?", teks "Password akun mengikuti SIAM UB. Atur ulang password melalui menu Lupa Password di SIAM, atau hubungi WA Center Kemahasiswaan FILKOM bila masih mengalami kendala.", dan tombol "Tutup". Ini sudah tertulis di `kode-modul-3/.../login.html`.
- PC-07 pada Modul 2 adalah duplikat PC-03 (`tentukanPeran`). Implementasikan sekali saja.
- Status pengajuan yang sah mengikuti CHECK pada DDL: `Draft`, `Menunggu Verifikasi`, `Sedang Diproses`, `Revisi`, `Menunggu Legalisasi Pejabat`, `Selesai`.
- Akun demo pada `database/seed.sql` memakai password `password123`, dengan hash bcrypt:
  `$2a$10$OR/QZlQ3F4LOLvwmuObcWeSmYfTMIFDgdRhN2ylM5JgZMEnxeXmB6`

### 3.3 Jika agent tidak bisa membaca PDF atau gambar

Pakai `modul-1.txt` dan `modul-2.txt`. Untuk tampilan, ikuti pola template yang sudah ada (`fragments/layout.html`, kelas-kelas pada `static/css/app.css`) dan deskripsi UI pada `modul-2.txt` bagian 2.7. Tulis di laporan akhir bahwa gambar tidak bisa dibaca.

---

## 4. Aturan tetap (wajib di setiap sesi)

### 4.1 Git

- **Dilarang** menjalankan perintah Git yang mengubah repository atau riwayat: `add`, `commit`, `push`, `merge`, `rebase`, `reset`, `checkout`, `switch`, `stash`, `pull`, `tag`, `branch -d`.
- **Boleh** menjalankan perintah Git yang hanya membaca: `status`, `log`, `diff`, `branch`, `fetch`, `rev-parse`, `rev-list`, `ls-remote`, `show`.
- Semua perintah `git switch -c`, `git add`, `git commit`, dan `git push` diberikan kepada anggota di laporan akhir. Anggota yang mengetiknya sendiri.
- Satu task = satu branch baru dari `main`, dengan format `<prefix-pelaksana>t<nomor>-<deskripsi-singkat-kebab-case>`, contoh `rasil/t02-konfigurasi-keamanan-layout`. Prefix memakai nama **pelaksana** (orang yang menjalankan sesi), bukan pemilik modul. Dengan begitu setiap orang memakai branch berbeda.
- Branch digabung ke `main` lewat Pull Request di GitHub sebelum orang berikutnya mulai.

### 4.2 Format commit message

- Format Conventional Commits dalam **bahasa Inggris**: `<type>(<scope opsional>): <ringkasan>`.
- `type` yang dipakai: `feat`, `fix`, `refactor`, `style`, `build`, `chore`, `docs`.
- Ringkasan memakai kalimat perintah (imperative), huruf kecil di awal, tanpa titik di akhir, maksimal 72 karakter. Contoh: `feat(auth): add login with session token and role redirect`.
- **Dilarang** menambahkan trailer atau baris tambahan apa pun: tidak ada `Co-authored-by`, `Co-Authored-By`, `Signed-off-by`, `Generated with ...`, nama AI, atau emoji. Commit message hanya satu baris dan ditulis dengan `git commit -m "..."`.
- Kelompokkan file ke beberapa commit yang logis (misalnya entity dan repository di satu commit, template di commit lain). Commit terakhir setiap task selalu `docs(progress): complete T<nomor> <ringkasan>` yang hanya berisi `PROGRESS.md`.
- Pada `git add`, sebutkan path file atau folder secara eksplisit. Jangan memakai `git add .` atau `git add -A`.

### 4.3 Kode harus bersih (tanpa komentar)

Semua file kode yang kamu buat atau ubah harus **murni kode tanpa komentar**:

| Jenis file | Yang dilarang |
|---|---|
| `.java` | `// ...`, `/* ... */`, `/** ... */` (termasuk Javadoc) |
| `.html` (Thymeleaf) | `<!-- ... -->`, dan komentar `//` atau `/* */` di dalam `<script>` |
| `.css` | `/* ... */` |
| `application.properties` | baris yang diawali `#` |
| `.sql` | `-- ...`, `# ...`, `/* ... */` |
| `pom.xml` | `<!-- ... -->` |

Ketentuan tambahan:

- Kode dari `kode-modul-3/` disalin dengan logika, nama, dan struktur yang sama, lalu **semua komentarnya dihapus**. Komentar yang berdiri sendiri di satu baris dihapus beserta barisnya.
- Pada Thymeleaf inline JavaScript, **jangan** memakai bentuk natural template `/*[[${...}]]*/ 0`. Pakai `[[${...}]]` langsung, contoh `const idPengajuan = [[${pengajuan.idPengajuan}]];`.
- Tidak boleh ada kode yang di-comment-out, `TODO`, `FIXME`, `System.out.println`, atau `console.log`.
- Aturan ini berlaku untuk `src/`, `database/`, dan `pom.xml`. Berkas dokumentasi (`README.md`, `PROGRESS.md`, `AGENTS.md`, `CLAUDE.md`, `GEMINI.md`, `docs/`) bukan kode, jadi boleh berisi teks penjelasan.
- Jika menyentuh file lama yang masih berkomentar, bersihkan juga komentar di file itu.
- Indentasi 4 spasi untuk Java, 2 spasi untuk HTML dan CSS, encoding UTF-8.

### 4.4 Batas pekerjaan

- Kerjakan **hanya satu task**, yaitu task berikutnya yang statusnya `Belum` di `PROGRESS.md`. Jangan mengerjakan sebagian task lain, walaupun terlihat mudah.
- Ubah hanya file dalam cakupan task. Jika file milik task lain **harus** diubah agar build lulus atau fitur tersambung, ubah seminimal mungkin dan catat di `PROGRESS.md` bagian "Perubahan di luar cakupan".
- Jangan menambah dependency Maven baru. Semua kebutuhan sudah tersedia di `pom.xml`.
- Jangan mengubah `docs/referensi/**`, `docs/ai/PROMPT.md`, `AGENTS.md`, `CLAUDE.md`, atau `GEMINI.md`.
- Hapus `.gitkeep` di folder yang sekarang sudah berisi file lain.
- Jangan membuat unit test (folder `src/test` dibiarkan).

### 4.5 Kesetiaan pada modul

- Nama kelas Controller dan Service mengikuti tabel "Pemetaan Use Case ke Controller" pada `modul-3.md` bagian 3.0.
- Nama method mengikuti pseudo-code Modul 2 (misalnya `requestKategoriKebutuhan`, `submitJenisDokumen`, `prosesPembaruanStatus`). Untuk use case yang sudah ada di Modul 3, ikuti nama di `kode-modul-3/`.
- Parameter ID memakai `Integer` sesuai DDL, termasuk pada pseudo-code yang menulis `String` (lihat "Penyesuaian terhadap Pseudo-Code Modul 2" di `modul-3.md`).
- Setiap aturan bisnis (validasi, pesan error, perubahan status, pencatatan riwayat) mengikuti pseudo-code, alternative flow pada use case scenario, dan SR terkait.
- Teks antarmuka berbahasa Indonesia, sama dengan sketsa UI.
- Halaman baru wajib memakai fragment `kepala`, `sidebar`, `topbar`, dan `ikon` dari `fragments/layout.html`, serta kelas yang sudah ada di `app.css` (`kartu`, `dua-kolom`, `badge`, `alert`, `field`, `timeline`, `tab`, `stepper`, dan lainnya). Gaya baru ditambahkan di bagian akhir `app.css` dan memakai variabel warna yang sudah ada.
- Setiap Controller memakai anotasi `@RequiredRole` sesuai aktor use case. Mahasiswa hanya boleh mengakses pengajuannya sendiri.
- Waktu memakai `Clock` dari `AppConfig` (zona Asia/Jakarta), bukan `LocalDateTime.now()` tanpa clock.
- Setiap perubahan status pengajuan dicatat di `RiwayatStatus` lewat `RiwayatStatusRepository.catatLog`, beserta pelaku dan waktu.

### 4.6 Bahasa

Tulis laporan dan isi `PROGRESS.md` dalam bahasa Indonesia. Commit message dalam bahasa Inggris.

---

## 5. Protokol setiap sesi

Ikuti langkah ini berurutan.

### Langkah 1: Tentukan jenis sesi

- **Sesi pertama:** `PROGRESS.md` belum ada di root repository. Sesi ini dijalankan Ammar, dan task yang dikerjakan adalah T01 (Ammar mengambil alih bagian AA). Lanjut ke Langkah 2.
- **Sesi lanjutan:** `PROGRESS.md` sudah ada, dan pengguna mengetik `lanjut` (atau `lanjut <nama>`, `next`, `gas`, atau kalimat sejenis). Baca `PROGRESS.md` sampai selesai sebelum melanjutkan.

### Langkah 2: Pemeriksaan awal (pre-flight)

Jalankan perintah Git yang hanya membaca:

1. `git rev-parse --abbrev-ref HEAD` harus `main`.
2. `git fetch --prune origin`, lalu `git rev-list --count HEAD..origin/main` harus `0`. Jika lebih dari 0, minta pengguna menjalankan `git pull origin main` dulu, lalu berhenti.
3. `git branch -r --no-merged origin/main`. Jika ada branch task (pola `origin/<nama>/t<nomor>-...`) yang belum di-merge, sebutkan branch-nya, minta pengguna me-merge Pull Request tersebut di GitHub lalu `git pull origin main`, lalu berhenti.
4. `git status --porcelain` harus kosong. Pengecualian pada sesi pertama: berkas paket awal yang belum ter-track (`AGENTS.md`, `CLAUDE.md`, `GEMINI.md`, `docs/`) boleh ada. Jika ada perubahan lain, tampilkan daftarnya dan tanyakan kepada pengguna apakah perubahan itu boleh dibuang atau perlu di-commit dulu. Jangan menghapusnya sendiri.
5. Pastikan `docs/referensi/` dan `docs/ai/PROMPT.md` ada. Jika tidak ada, minta pengguna mengekstrak paket awal ke root repository, lalu berhenti.

Jika agent tidak bisa menjalankan perintah terminal, minta pengguna menjalankannya dan menempelkan hasilnya.

### Langkah 3: Tentukan task

- Task yang dikerjakan adalah task dengan nomor terkecil yang statusnya `Belum` di `PROGRESS.md` (pada sesi pertama: T01).
- Jika pengguna menyebut nama (misalnya `lanjut rasil`) dan nama itu bukan **pelaksana** task tersebut menurut tabel di bagian 6, beri tahu siapa yang seharusnya mengerjakan, lalu berhenti. Jika pengguna tidak menyebut nama, sebutkan di awal jawaban: "Task ini adalah T<nomor> milik <Nama> (<inisial>)", lalu lanjutkan.
- Jika semua task sudah `Selesai`, sampaikan bahwa implementasi kode proyek sudah selesai, lalu berhenti tanpa mengubah file.

### Langkah 4: Pelajari referensi task

Baca semua referensi yang disebut pada task tersebut di bagian 6: file di `kode-modul-3/`, pseudo-code PC, sequence diagram, class diagram, use case scenario, SR, NFR, dan gambar sketsa UI. Baca juga kode di repository yang akan dipakai atau disentuh, serta catatan "Catatan untuk task berikutnya" di `PROGRESS.md`.

### Langkah 5: Implementasi

Kerjakan cakupan task sesuai aturan di bagian 4.

### Langkah 6: Verifikasi

1. **Build:** jalankan `./mvnw -q -DskipTests package` (Git Bash, Linux, macOS) atau `.\mvnw.cmd -q -DskipTests package` (PowerShell, CMD). Build harus lulus. Jika gagal, perbaiki dulu. Jika agent tidak bisa menjalankan build (tidak ada Java, tidak ada internet, atau sandbox), tulis di laporan bahwa build **belum diverifikasi**, dan jadikan build sebagai langkah wajib pengguna sebelum commit.
2. **Cek komentar:** periksa semua file yang dibuat atau diubah memakai pencarian (`grep`, `rg`, atau `Select-String`) untuk pola `//` (abaikan yang ada di dalam string seperti `http://`), `/*`, `<!--`, baris properties yang diawali `#`, dan `--` di SQL. Hasilnya harus kosong.
3. **Cek konsistensi:** nama template yang dikembalikan controller benar-benar ada, tautan di sidebar dan redirect mengarah ke rute yang ada (atau rute milik task berikutnya yang sudah direncanakan), dan peran pada `@RequiredRole` sesuai aktor.
4. Jika MySQL tersedia, boleh menjalankan aplikasi dan mencoba alurnya. Ini tidak wajib.

### Langkah 7: Perbarui `PROGRESS.md`

Ubah status task menjadi `Selesai`, isi kolom branch, tambahkan entri log (format di bagian 7), dan isi "Task berikutnya".

### Langkah 8: Laporan akhir

Tutup sesi dengan laporan sesuai format di bagian 8. Jangan menjalankan perintah Git yang ada di laporan.

---

## 6. Rencana task

Urutan disusun mengikuti ketergantungan kode supaya setiap task bisa di-build: fondasi dan entity dulu, lalu fitur sesuai alur pengguna. Pembagian pemilik mengikuti Modul 2 (penulis use case scenario, pseudo-code, dan sketsa UI) dan Modul 3 (penulis SC dan IM).

### 6.1 Ringkasan urutan

| Task | Judul | Pemilik modul | Pelaksana | Use case / kode modul |
|---|---|---|---|---|
| T01 | Fondasi proyek, workflow AI, entity akun dan jenis dokumen | AA | **Ammar** (mengambil alih AA) | SC11, SC06 |
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

Urutan pelaksana: Ammar, Rasil, Ammar, Rasil, Jasmine, Amirah, Rasil, Amirah, Ammar, Amirah, Rasil, Ammar, Jasmine, Rasil, Rasil, Jasmine, Ammar, Ammar.

Singkatan path: `R3` = `docs/referensi/kode-modul-3/`, `J` = `src/main/java/id/ac/ub/layanan/`, `T` = `src/main/resources/templates/`.

---

### T01: Fondasi proyek, workflow AI, entity akun dan jenis dokumen

- **Pemilik modul:** AA. **Pelaksana:** Ammar (mengambil alih). **Branch:** `ammar/t01-fondasi-entity-akun`
- **Referensi:** `R3/pom.xml`, `R3/src/main/resources/application.properties`, `R3/.../LayananKemahasiswaanApplication.java`, `R3/database/schema.sql`, `R3/.../entity/AkunPengguna.java`, `R3/.../entity/JenisDokumen.java`, `R3/.../repository/AkunPenggunaRepository.java`, `modul-3.md` bagian 3.0 dan 3.1.
- **Cakupan:**
  1. **Workflow AI.** Pastikan `AGENTS.md`, `CLAUDE.md`, `GEMINI.md`, `docs/ai/PROMPT.md`, `docs/ai/CARA-PAKAI.md`, dan `docs/referensi/` ada (berasal dari paket awal). Buat `PROGRESS.md` di root sesuai format bagian 7, dengan seluruh T01 sampai T18 berstatus `Belum`.
  2. **SC11, konfigurasi proyek.** `pom.xml` memakai dependency yang sama dengan `R3/pom.xml` dan tanpa komentar. `application.properties` berisi semua key dari `R3` (termasuk `spring.web.locale=id_ID` dan `spring.web.locale-resolver=fixed`) ditambah key yang sudah ada di repository (misalnya `spring.application.name`, `spring.thymeleaf.cache=false`), tanpa komentar. `LayananKemahasiswaanApplication.java` tanpa komentar.
  3. **Database.** `database/schema.sql` disamakan dengan DDL bagian 3.1 (`R3/database/schema.sql`), tanpa komentar. `database/seed.sql` tanpa komentar, datanya tetap, semua hash password akun diganti dengan hash `password123` pada bagian 3.2, dan semua nilai memenuhi CHECK pada DDL.
  4. **SC06.** `J/entity/AkunPengguna.java`, `J/entity/JenisDokumen.java`, `J/repository/AkunPenggunaRepository.java` dari `R3`, tanpa komentar.
  5. **README.md.** Perbarui bagian "Pembagian Implementasi" supaya merujuk ke `PROGRESS.md` dan `docs/ai/CARA-PAKAI.md`, dan perbaiki tabel pembagiannya sesuai bagian 6.1. Tambahkan akun demo (username dari `seed.sql`, password `password123`).
- **Kriteria selesai:** build lulus, tidak ada komentar di file kode yang disentuh, `PROGRESS.md` terbentuk.
- **Saran commit:** `chore: add ai agent workflow and reference documents` (AGENTS.md, CLAUDE.md, GEMINI.md, docs/), `build: align project configuration and database scripts with modul 3` (pom.xml, application.properties, kelas Application, database/), `feat(entity): add AkunPengguna and JenisDokumen entities` (entity, repository, penghapusan .gitkeep), `docs: update readme for sequential implementation workflow` (README.md), `docs(progress): complete T01 project foundation` (PROGRESS.md).

### T02: Konfigurasi, keamanan, layout, dan CSS

- **Pemilik modul:** MR. **Pelaksana:** Rasil. **Branch:** `rasil/t02-konfigurasi-keamanan-layout`
- **Referensi:** `R3/.../config/AppConfig.java`, `R3/.../config/WebConfig.java`, `R3/.../security/AuthInterceptor.java`, `R3/.../security/RequiredRole.java`, `R3/.../exception/DataTidakDitemukanException.java`, `R3/.../templates/fragments/layout.html`, `R3/.../static/css/app.css`, NFR-SRS-02, 04, 07, 15, 16, `sketsa-ui/UI-12-*`.
- **Cakupan:** salin semua file di atas tanpa komentar. `WebConfig` mengecualikan `/login`, `/css/**`, `/img/**`, dan `/error` dari interceptor.
- **Logo:** `layout.html` dan `login.html` memakai `/img/logo-filkom-ub.png`. Jika `src/main/resources/static/img/logo-filkom-ub.png` belum ada, **jangan membuat logo palsu**. Di laporan akhir, minta pelaksana mengekspor logo FILKOM UB dari Figma (page "02 Hi-Fi FIX") sebagai PNG, menyimpannya di path tersebut, dan memasukkannya ke `git add`.
- **Kriteria selesai:** build lulus, dan semua rute selain yang dikecualikan diarahkan ke `/login` (halaman login baru tersedia di T06).
- **Saran commit:** `feat(config): add clock, password encoder and web configuration`, `feat(security): add session token interceptor and role annotation`, `feat(ui): add base layout fragments and stylesheet`, lalu commit progress.

### T03: Entity lampiran dan antrean verifikasi

- **Pemilik modul:** AT. **Pelaksana:** Ammar. **Branch:** `ammar/t03-entity-lampiran-antrean`
- **Referensi:** `R3/.../entity/BerkasLampiran.java`, `R3/.../entity/AntreanVerifikasi.java`, `R3/.../repository/BerkasLampiranRepository.java`, DDL `BerkasLampiran` dan `AntreanVerifikasi`.
- **Cakupan:** salin tanpa komentar. Tambahkan `J/repository/AntreanVerifikasiRepository.java` (`JpaRepository<AntreanVerifikasi, Integer>` dengan `Optional<AntreanVerifikasi> findByIdPengajuan(Integer idPengajuan)`), karena repository ini akan dipakai T10, T14, dan T16.
- **Saran commit:** `feat(entity): add BerkasLampiran and AntreanVerifikasi entities`, lalu commit progress.

### T04: Entity persyaratan dokumen dan pengajuan

- **Pemilik modul:** MR. **Pelaksana:** Rasil. **Branch:** `rasil/t04-entity-persyaratan-pengajuan`
- **Referensi:** `R3/.../entity/PersyaratanDokumen.java`, `R3/.../entity/Pengajuan.java`, `R3/.../repository/PengajuanRepository.java`, `R3/.../repository/PersyaratanDokumenRepository.java`, DDL terkait.
- **Cakupan:** salin tanpa komentar.
- **Saran commit:** `feat(entity): add PersyaratanDokumen and Pengajuan entities`, lalu commit progress.

### T05: Entity riwayat status dan paket DTO

- **Pemilik modul:** JA. **Pelaksana:** Jasmine. **Branch:** `jasmine/t05-entity-riwayat-dto`
- **Referensi:** `R3/.../entity/RiwayatStatus.java`, `R3/.../repository/RiwayatStatusRepository.java`, seluruh `R3/.../dto/*.java` (HasilAksi, HasilLogin, BarisAntrean, HasilAntrean, ItemChecklist, HasilValidasiItem, DetailLegalisasi, BarisLegalisasi, LangkahRiwayat), NFR-SRS-11.
- **Cakupan:** salin tanpa komentar. `RiwayatStatus` tetap `@Immutable` tanpa setter.
- **Saran commit:** `feat(entity): add immutable RiwayatStatus log`, `feat(dto): add data transfer objects`, lalu commit progress.

### T06: Login ke sistem (UC-01)

- **Pemilik modul:** AA. **Pelaksana:** Amirah. **Branch:** `amirah/t06-login`
- **Referensi:** `R3/.../controller/AuthController.java`, `R3/.../service/AuthService.java`, `R3/.../templates/login.html`, PC-01, PC-02, PC-03 (PC-07 = PC-03), SEQ-01, CD-01, SR-01, SR-02, NFR-SRS-02, `sketsa-ui/UI-01-*`.
- **Cakupan:** salin bagian **login saja**: `bukaHalamanLogin`, `submitLogin`, `prosesLogin`, `validasiKredensial`, `tentukanPeran`, `buatSessionToken`, penyimpanan `inisial` dan data sesi, serta redirect dashboard per peran (Mahasiswa ke `/pengajuan`, Staf Kemahasiswaan ke `/antrean`, Pejabat ke `/legalisasi`). Endpoint `POST /logout` dan method `hancurkanSessionToken` **tidak** termasuk (dikerjakan T07). `login.html` disalin utuh, termasuk alert `?keluar`, alert `?sesi`, dan dialog Lupa password.
- **Kriteria selesai:** build lulus, dan login dengan akun demo berhasil serta diarahkan sesuai peran (halaman tujuan bisa masih 404 sampai task terkait selesai).
- **Saran commit:** `feat(auth): add login with bcrypt validation and session token`, `feat(ui): add login page`, lalu commit progress.

### T07: Keluar dari akun (UC-12)

- **Pemilik modul:** MR. **Pelaksana:** Rasil. **Branch:** `rasil/t07-logout`
- **Referensi:** bagian logout pada `R3/.../controller/AuthController.java` dan `R3/.../service/AuthService.java`, PC-45, SEQ-12, CD-12, SR-24, SR-25, NFR-SRS-16, `sketsa-ui/UI-12-*`.
- **Cakupan:** tambahkan `POST /logout` (`prosesLogout`) dan `AuthService.hancurkanSessionToken` persis seperti `R3`: kosongkan `sessionToken` di database, invalidate sesi HTTP, redirect ke `/login?keluar`. Jika gagal, tampilkan `pesanError` dan kembali ke dashboard peran. Dialog konfirmasi Keluar sudah ada di `layout.html` dari T02.
- **Saran commit:** `feat(auth): add logout that destroys session token`, lalu commit progress.

### T08: Memilih jenis dokumen (UC-03)

- **Pemilik modul:** AA. **Pelaksana:** Amirah. **Branch:** `amirah/t08-pilih-jenis-dokumen`
- **Referensi:** use case scenario UC-03, PC-08, PC-09, PC-10, SEQ-03, CD-03 (Modul 2), SR-05, SR-06 (Modul 1), `sketsa-ui/UI-03-*`.
- **Cakupan:**
  - `J/controller/PemilihanDokumenController.java`, `J/service/PemilihanDokumenService.java`, `J/repository/JenisDokumenRepository.java`, template `T/pilih-dokumen.html`.
  - Rute `GET /pengajuan/baru` (menu "Ajukan Dokumen" di sidebar): menampilkan kategori kebutuhan (PC-08), lalu jenis dokumen sesuai kategori terpilih (PC-09).
  - `POST /pengajuan/baru`: membuat `Pengajuan` baru milik Mahasiswa yang login, dengan status `Draft` dan `tanggalPengajuan` dari `Clock` (PC-10), lalu redirect ke `/pengajuan/{id}/tenggat` (halaman "Ajukan dokumen" dari T09).
  - Tampilan mengikuti UI-03, termasuk stepper langkah 1 "Pilih Jenis" (pola stepper sama dengan `form-tenggat.html` di `R3`). `@RequiredRole("Mahasiswa")`, `aktif` sidebar `ajukan`.
- **Saran commit:** `feat(pengajuan): add document type selection`, `feat(ui): add document type selection page`, lalu commit progress.

### T09: Mencantumkan tenggat kebutuhan (UC-05)

- **Pemilik modul:** AT. **Pelaksana:** Ammar. **Branch:** `ammar/t09-tenggat-kebutuhan`
- **Referensi:** `R3/.../controller/TenggatController.java`, `R3/.../service/TenggatService.java`, `R3/.../templates/form-tenggat.html`, PC-17 sampai PC-20, SEQ-05, CD-05, SR-09, SR-10, `sketsa-ui/UI-05-*`.
- **Cakupan:** salin tanpa komentar. Pertahankan penanda posisi kartu "Persyaratan dokumen" di kolom kiri `form-tenggat.html` sebagai elemen kosong yang tidak berupa komentar, misalnya `<div id="slotPersyaratan"></div>`. Elemen ini diisi di T10.
- **Saran commit:** `feat(tenggat): add deadline validation and urgent confirmation`, `feat(ui): add submission form with deadline card`, lalu commit progress.

### T10: Mengunggah persyaratan dan submit pengajuan (UC-04)

- **Pemilik modul:** AA. **Pelaksana:** Amirah. **Branch:** `amirah/t10-unggah-persyaratan`
- **Referensi:** use case scenario UC-04, PC-11 sampai PC-16, SEQ-04, CD-04, SR-07, SR-08, NFR-SRS-05, `sketsa-ui/UI-04-*` dan `UI-05-*` (bagian unggah dan "Pengajuan Terkirim").
- **Cakupan:**
  - `J/controller/UnggahPersyaratanController.java`, `J/service/UnggahPersyaratanService.java`. Kartu "Persyaratan dokumen" dipasang di `form-tenggat.html` pada slot dari T09.
  - PC-11: menampilkan persyaratan wajib sesuai jenis dokumen. PC-12 dan PC-13: unggah lampiran (multipart) dengan validasi format dan ukuran maksimal 5 MB sesuai Modul 1, berkas disimpan di folder `app.upload-dir`, dan baris `BerkasLampiran` dibuat (statusItem `Terunggah`, versi 1, statusValidasi `Belum Diperiksa`). PC-15: menampilkan lampiran yang sudah diunggah beserta ukurannya, dengan opsi ganti atau hapus sesuai sketsa.
  - **Integrasi submit (penting):** tombol "Submit Pengajuan" tetap satu. Saat submit, alurnya: cek kelengkapan semua persyaratan wajib (PC-14), lalu proses tenggat (PC-17 sampai PC-20 dari T09), lalu `submitPengajuan` (PC-16): status `Menunggu Verifikasi`, baris `AntreanVerifikasi` dengan status `Menunggu` dibuat, dan log `RiwayatStatus` dicatat. Sesuaikan `TenggatService` dan `TenggatController` seminimal mungkin agar tidak ada dua jalur submit, dan catat perubahan ini di "Perubahan di luar cakupan". Tombol Submit nonaktif selama persyaratan wajib belum lengkap.
- **Saran commit:** `feat(lampiran): add requirement upload with format and size validation`, `feat(pengajuan): submit application to verification queue`, `feat(ui): add requirement upload card`, lalu commit progress.

### T11: Riwayat status pengajuan / Pengajuan Saya (UC-11)

- **Pemilik modul:** MR. **Pelaksana:** Rasil. **Branch:** `rasil/t11-riwayat-pengajuan`
- **Referensi:** use case scenario UC-11, PC-42, PC-43, PC-44, SEQ-11, CD-11, SR-22, SR-23, `sketsa-ui/UI-11-*`.
- **Cakupan:**
  - `J/controller/RiwayatController.java`, `J/service/RiwayatService.java`, template `T/pengajuan-saya.html` dan `T/pengajuan-detail.html`.
  - `GET /pengajuan`: daftar pengajuan milik Mahasiswa yang login (dashboard Mahasiswa setelah login, PC-42).
  - `GET /pengajuan/{idPengajuan}`: detail pengajuan dan timeline riwayat status yang diurutkan secara kronologis (PC-43, PC-44), memakai `RiwayatStatusRepository.findByIdPengajuanOrderByWaktu` dan kelas `.timeline`. Catatan revisi ditampilkan jika ada.
  - Jika status `Revisi`, tampilkan tombol menuju `/pengajuan/{id}/revisi` (diimplementasikan di T16).
  - Pengajuan berstatus `Draft` mengarah kembali ke `/pengajuan/{id}/tenggat`.
  - `@RequiredRole("Mahasiswa")`, hanya pengajuan milik sendiri, `aktif` sidebar `pengajuan`.
- **Saran commit:** `feat(riwayat): add student submission list and status history`, `feat(ui): add my submissions and detail pages`, lalu commit progress.

### T12: Antrean pengajuan berdasarkan prioritas (UC-06)

- **Pemilik modul:** AT. **Pelaksana:** Ammar. **Branch:** `ammar/t12-antrean-prioritas`
- **Referensi:** `R3/.../controller/AntreanController.java`, `R3/.../service/AntreanService.java`, `R3/.../templates/antrean.html`, PC-21 sampai PC-24, SEQ-06, CD-06, SR-11, SR-12, `sketsa-ui/UI-06-*`.
- **Cakupan:** salin tanpa komentar. Pastikan pengajuan dari T10 (status `Menunggu Verifikasi` dengan antrean `Menunggu`) muncul di antrean.
- **Saran commit:** `feat(antrean): add priority queue sorted by deadline`, `feat(ui): add submission queue page`, lalu commit progress.

### T13: Validasi kelengkapan lampiran (UC-07)

- **Pemilik modul:** JA. **Pelaksana:** Jasmine. **Branch:** `jasmine/t13-validasi-lampiran`
- **Referensi:** `R3/.../controller/ValidasiLampiranController.java`, `R3/.../service/ValidasiLampiranService.java`, `R3/.../controller/BerkasController.java`, `R3/.../templates/checklist.html`, PC-25 sampai PC-29, SEQ-07, CD-07, SR-13, SR-14, NFR-SRS-09, NFR-SRS-10, `sketsa-ui/UI-07-*`.
- **Cakupan:** salin tanpa komentar. Inline JavaScript memakai `[[${...}]]` (bagian 4.3). Pertahankan slot kartu "Perbarui status" di kolom kanan `checklist.html` sebagai elemen kosong `<div id="slotPerbaruiStatus"></div>` untuk T14. Membuka checklist tidak mengubah status pengajuan (perubahan status adalah bagian T14).
- **Saran commit:** `feat(validasi): add attachment checklist validation`, `feat(lampiran): serve uploaded attachment files`, `feat(ui): add attachment validation page`, lalu commit progress.

### T14: Memperbarui status pengajuan (UC-08)

- **Pemilik modul:** MR. **Pelaksana:** Rasil. **Branch:** `rasil/t14-perbarui-status`
- **Referensi:** use case scenario UC-08, PC-30, PC-31, PC-32, SEQ-08, CD-08, SR-15, SR-16, SR-17, `sketsa-ui/UI-08-*`.
- **Cakupan:**
  - `J/controller/PengajuanController.java`, `J/service/PengajuanService.java`. Kartu "Perbarui status" dipasang di slot `checklist.html` dari T13.
  - PC-30: menampilkan status saat ini dan opsi status baru sesuai jalur dokumen (`perluLegalisasi`). PC-31 dan PC-32: validasi status dan catatan (catatan wajib untuk `Revisi`, opsi `Menunggu Legalisasi Pejabat` hanya untuk jalur legalisasi, `Selesai` hanya jika valid sesuai SR), simpan status, perbarui `AntreanVerifikasi.statusAntrean` (`Diproses` atau `Selesai`), dan catat `RiwayatStatus`.
  - Rute `POST /pengajuan/{idPengajuan}/status` dengan `@RequiredRole("Staf Kemahasiswaan")`. Hasil validasi lampiran dari T13 menjadi syarat sesuai SR dan alternative flow UC-08.
- **Saran commit:** `feat(pengajuan): add status update with notes and audit log`, `feat(ui): add status update card`, lalu commit progress.

### T15: Keputusan legalisasi oleh Pejabat (UC-09)

- **Pemilik modul:** MR. **Pelaksana:** Rasil. **Branch:** `rasil/t15-legalisasi-pejabat`
- **Referensi:** `R3/.../controller/LegalisasiController.java`, `R3/.../service/LegalisasiService.java`, `R3/.../templates/legalisasi-antrean.html`, `R3/.../templates/legalisasi-detail.html`, PC-33, PC-34, PC-35, SEQ-09, CD-09, SR-18, SR-19, `sketsa-ui/UI-09-*`.
- **Cakupan:** salin tanpa komentar. Tolak menghasilkan status `Revisi` beserta catatan alasan, dan Setuju menghasilkan status `Selesai`. Keduanya dicatat di `RiwayatStatus`.
- **Saran commit:** `feat(legalisasi): add official approval and rejection decisions`, `feat(ui): add legalization queue and detail pages`, lalu commit progress.

### T16: Merevisi pengajuan yang ditolak (UC-10)

- **Pemilik modul:** JA. **Pelaksana:** Jasmine. **Branch:** `jasmine/t16-revisi-pengajuan`
- **Referensi:** use case scenario UC-10, PC-36 sampai PC-41, SEQ-10, CD-10, SR-20, SR-21, NFR-SRS-05, `sketsa-ui/UI-10-*`.
- **Cakupan:**
  - `J/controller/RevisiPengajuanController.java`, `J/service/RevisiPengajuanService.java`, template `T/revisi.html`.
  - `GET /pengajuan/{idPengajuan}/revisi`: hanya untuk pemilik dengan status `Revisi`. Menampilkan catatan revisi terakhir dan lampiran berstatus `Tidak Lengkap` (PC-36).
  - Ganti lampiran (PC-37 sampai PC-39): lampiran lama menjadi `statusItem = Diganti`, lampiran baru dibuat dengan `versi` + 1 dan `statusValidasi = Belum Diperiksa`, dengan validasi format dan 5 MB yang sama seperti T10.
  - Submit ulang (PC-40, PC-41): status kembali `Menunggu Verifikasi`, `AntreanVerifikasi.statusAntrean` kembali `Menunggu`, dan `RiwayatStatus` dicatat.
  - Tombol masuk dari halaman detail T11.
- **Saran commit:** `feat(revisi): add attachment replacement and resubmission`, `feat(ui): add revision page`, lalu commit progress.

### T17: Melihat dan memperbarui profil (UC-02)

- **Pemilik modul:** AT. **Pelaksana:** Ammar. **Branch:** `ammar/t17-profil-pengguna`
- **Referensi:** use case scenario UC-02, PC-04, PC-05, PC-06, SEQ-02, CD-02, SR-03, SR-04, `sketsa-ui/UI-02-*` (Mahasiswa, Staf, Pejabat, mode ubah, berhasil, validasi gagal).
- **Cakupan:**
  - `J/controller/ProfilController.java`, `J/service/ProfilService.java`, template `T/profil.html`.
  - `GET /profil` untuk semua peran (`@RequiredRole` dengan ketiga peran). Field yang boleh diubah dan aturan validasinya mengikuti PC-05, PC-06, dan SR-04.
  - `POST /profil` menyimpan perubahan. Nama di sesi diperbarui jika berubah. `aktif` sidebar `profil`.
- **Saran commit:** `feat(profil): add profile view and update with validation`, `feat(ui): add profile page`, lalu commit progress.

### T18: Integrasi akhir, README, dan penutupan

- **Pemilik modul:** AT. **Pelaksana:** Ammar. **Branch:** `ammar/t18-integrasi-akhir`
- **Cakupan:**
  1. Build penuh harus lulus.
  2. Periksa alur menyeluruh di kode (dan jalankan jika MySQL tersedia): login tiap peran, Mahasiswa memilih jenis, mengunggah, mengisi tenggat, lalu submit; Staf melihat antrean, memvalidasi, dan memperbarui status; Pejabat menyetujui atau menolak; Mahasiswa merevisi; riwayat; profil; logout.
  3. Pastikan semua tautan sidebar, redirect, dan rute tersedia. Tidak boleh ada template yang hilang atau rute 404 di alur utama.
  4. Pindai seluruh `src/`, `database/`, dan `pom.xml` dari komentar dan sisa `.gitkeep` di folder yang sudah berisi file.
  5. Pastikan `seed.sql` konsisten dengan skema dan alur (status, antrean, dan riwayat contoh saling cocok). Lampiran contoh di seed yang tidak punya berkas fisik di `uploads/` dicatat di README.
  6. Perbarui `README.md`: cara menjalankan, akun demo, daftar fitur per use case beserta rute, dan pembagian implementasi final.
  7. Perbaiki bug lintas task yang ditemukan, minimal dan tercatat.
- **Saran commit:** `fix: ...` sesuai temuan, `docs: finalize readme`, lalu `docs(progress): complete T18 final integration`.
- **Laporan akhir:** setelah format bagian 8, tutup dengan blok **IMPLEMENTASI KODE PROYEK SELESAI** (lihat bagian 8.2).

---

## 7. Format `PROGRESS.md`

Buat dan jaga format ini. Jangan menghapus entri log lama.

```markdown
# PROGRESS Implementasi Kode Kelompok 8

Aturan dan rencana lengkap: `docs/ai/PROMPT.md`. Cara pakai untuk anggota: `docs/ai/CARA-PAKAI.md`.

## Status task

| Task | Judul | Pemilik | Pelaksana | Branch | Status |
|---|---|---|---|---|---|
| T01 | Fondasi proyek, workflow AI, entity akun dan jenis dokumen | AA | Ammar | ammar/t01-fondasi-entity-akun | Selesai |
| T02 | Konfigurasi, keamanan, layout, dan CSS | MR | Rasil | - | Belum |
| ... | ... | ... | ... | ... | ... |

## Task berikutnya

T02: Konfigurasi, keamanan, layout, dan CSS. Pelaksana: Rasil (MR).

## Log pengerjaan

### T01: Fondasi proyek, workflow AI, entity akun dan jenis dokumen
- Tanggal: YYYY-MM-DD
- Pelaksana: Ammar (mengambil alih AA)
- Ringkasan: ...
- File dibuat atau diubah: ...
- Keputusan dan penyimpangan dari modul (beserta alasannya): ...
- Perubahan di luar cakupan: ... (tulis "Tidak ada" jika kosong)
- Status build: Lulus / Belum diverifikasi (alasan)
- Catatan untuk task berikutnya: ...
```

---

## 8. Format laporan akhir sesi

### 8.1 Laporan setiap task

Tulis laporan dengan urutan berikut, tanpa emoji.

````markdown
## T<nomor> selesai: <judul>

**Ringkasan:** <2 sampai 4 kalimat tentang apa yang dibuat>

**File yang dibuat atau diubah:**
- `path/file` (dibuat / diubah / dihapus)

**Verifikasi:** build <lulus / belum diverifikasi, alasannya>; cek komentar <bersih>.

**Wajib sebelum commit:** <langkah manual jika ada, misalnya menjalankan build sendiri atau menaruh logo; tulis "Tidak ada" jika kosong>

### Perintah Git
Jalankan berurutan dari root proyek (PowerShell atau Git Bash):

```
git switch -c <branch>
git add <path> <path>
git commit -m "<type>(<scope>): <message>"
git add <path>
git commit -m "<type>(<scope>): <message>"
git add PROGRESS.md
git commit -m "docs(progress): complete T<nomor> <ringkasan>"
git push -u origin <branch>
```

### Merge ke main
1. Buka https://github.com/Kelompok-8-RSI/Sistem-Kemahasiswaan-Fakultas-Ilmu-Komputer/compare/main...<branch>?expand=1
2. Klik **Create pull request**, lalu **Merge pull request**, lalu **Confirm merge**.
3. Kembali ke main di terminal:
```
git switch main
git pull origin main
```

### Lanjut ke: <Nama> (<inisial>)
Task berikutnya: **T<nomor+1> <judul>**.
Pesan untuk <Nama>: "T<nomor> sudah di-merge ke main. Giliran kamu: jalankan `git switch main` dan `git pull origin main`, lalu ketik `lanjut` ke AI agent kamu."
````

Ketentuan:

- Perintah `git add` hanya berisi path yang benar-benar berubah, dan setiap file yang berubah harus masuk ke salah satu commit (termasuk file `.gitkeep` yang dihapus).
- Jika pelaksana berikutnya adalah orang yang sama, tulis "Lanjut ke: <Nama> (kamu lagi)" dan tetap minta merge dulu sebelum mengetik `lanjut`.
- Jika pelaksana berikutnya adalah orang lain dan anggota tersebut belum pernah clone repository, tambahkan:
  `git clone https://github.com/Kelompok-8-RSI/Sistem-Kemahasiswaan-Fakultas-Ilmu-Komputer.git`

### 8.2 Penutupan proyek (hanya setelah T18)

Setelah laporan T18, tambahkan:

```markdown
## IMPLEMENTASI KODE PROYEK SELESAI
Semua task T01 sampai T18 sudah selesai. Setelah PR T18 di-merge, `main` berisi implementasi lengkap UC-01 sampai UC-12.
Cara menjalankan: lihat README.md bagian Menjalankan Aplikasi.
```

---

## 9. Situasi khusus

| Situasi | Tindakan |
|---|---|
| Build gagal karena kode dari task sebelumnya | Perbaiki seminimal mungkin, pisahkan dalam commit `fix(<scope>): ...`, dan catat di "Perubahan di luar cakupan". |
| Referensi modul saling bertentangan | Ikuti urutan prioritas bagian 3.1, lalu catat keputusan di log. |
| Informasi di modul tidak cukup | Pilih solusi paling sederhana yang konsisten dengan modul dan kode yang sudah ada, lalu catat sebagai keputusan. Jangan bertanya kecuali keputusannya tidak bisa dibatalkan. |
| Pengguna meminta mengerjakan task lain atau lebih dari satu task | Jelaskan bahwa urutan harus dijaga agar tidak terjadi error. Kerjakan hanya task berikutnya. |
| Pengguna ingin mengulang task yang sudah `Selesai` | Jangan dikerjakan ulang. Jika ada bug, perbaiki di task berjalan sebagai `fix` dan catat. |
| PR task sebelumnya belum di-merge | Berhenti dan minta merge (Langkah 2). |
| Agent kehilangan konteks di tengah sesi | Baca ulang `docs/ai/PROMPT.md`, `PROGRESS.md`, dan `git status`, lalu lanjutkan task yang sama. |

---

## 10. Mulai sekarang

- Jika `PROGRESS.md` belum ada: ini sesi pertama oleh Ammar. Jalankan protokol bagian 5 untuk **T01**.
- Jika `PROGRESS.md` sudah ada: tunggu pengguna mengetik `lanjut`, lalu jalankan protokol bagian 5 untuk task berikutnya. Jika pesan pengguna sudah berisi `lanjut`, langsung mulai.
