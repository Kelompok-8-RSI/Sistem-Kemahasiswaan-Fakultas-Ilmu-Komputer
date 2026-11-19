package id.ac.ub.layanan.dto;

import java.time.LocalDateTime;

public record ItemChecklist(
        Integer idPersyaratan,
        Integer idLampiran,          // null jika belum diunggah
        String namaPersyaratan,
        String namaFile,
        Integer ukuranKB,            // ditampilkan sebagai "842 KB" / "1,8 MB"
        String statusUnggah,         // "Sudah diunggah" / "Belum diunggah"
        boolean opsiLengkap,         // false: tombol "Lengkap" dinonaktifkan
        String statusValidasi,       // Belum Diperiksa / Lengkap / Tidak Lengkap
        String catatanValidasi,
        String namaStafValidator,
        LocalDateTime waktuValidasi) {

    public String ukuran() {
        if (ukuranKB == null) return "";
        return ukuranKB < 1024 ? ukuranKB + " KB" : String.format("%.1f MB", ukuranKB / 1024.0).replace('.', ',');
    }
}
