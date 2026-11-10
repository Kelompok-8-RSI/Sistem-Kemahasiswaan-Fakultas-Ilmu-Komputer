# Cara Pakai Workflow AI Agent (untuk anggota Kelompok 8)

Implementasi kode dikerjakan bergiliran. AI agent menulis kode, sedangkan kamu sendiri yang menjalankan perintah Git. Urutan dan pembagian task ada di `docs/ai/PROMPT.md` bagian 6. Status terkini selalu ada di `PROGRESS.md`.

## Sekali saja (setiap anggota)

1. Pastikan sudah terpasang: Git, JDK 17 atau lebih baru (`java -version`), dan MySQL 8.
2. Clone repository (lewati jika sudah):
   ```
   git clone https://github.com/Kelompok-8-RSI/Sistem-Kemahasiswaan-Fakultas-Ilmu-Komputer.git
   cd Sistem-Kemahasiswaan-Fakultas-Ilmu-Komputer
   ```
3. Buka folder proyek di AI agent kamu (Claude Code, Codex, Antigravity, Cursor, atau yang lain).

## Setiap kali giliranmu

1. Pastikan task sebelumnya sudah di-merge ke `main` (lihat Pull Request di GitHub).
2. Ambil kode terbaru:
   ```
   git switch main
   git pull origin main
   ```
3. Di AI agent, ketik:
   ```
   lanjut
   ```
   Jika agent tidak otomatis membaca `AGENTS.md` atau `CLAUDE.md`, ketik:
   ```
   Baca docs/ai/PROMPT.md dan PROGRESS.md, lalu lanjut.
   ```
4. Tunggu sampai AI selesai dan menampilkan laporan. Lakukan langkah di bagian "Wajib sebelum commit" jika ada.
5. Jalankan perintah Git dari laporan AI satu per satu (buat branch, add, commit, push).
6. Buka link Pull Request yang diberikan AI, lalu klik **Create pull request**, **Merge pull request**, dan **Confirm merge**.
7. Kembali ke main:
   ```
   git switch main
   git pull origin main
   ```
8. Kabari orang berikutnya sesuai bagian "Lanjut ke" pada laporan AI.

## Aturan penting

- Jangan menjalankan perintah Git yang tidak ada di laporan AI, kecuali untuk mengambil kode terbaru.
- Jangan mengubah urutan task. Urutannya sudah disusun supaya build tidak error.
- Jika build gagal atau ada error saat menjalankan aplikasi, tempelkan pesan error ke AI agent yang sama sebelum commit.
- Jika AI berhenti karena ada PR yang belum di-merge atau `main` belum terbaru, ikuti instruksinya lalu ketik `lanjut` lagi.
