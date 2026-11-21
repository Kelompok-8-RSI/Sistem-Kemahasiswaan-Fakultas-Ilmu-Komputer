package id.ac.ub.layanan.dto;

import java.time.LocalDateTime;

public record ItemChecklist(
        Integer idPersyaratan,
        Integer idLampiran,
        String namaPersyaratan,
        String namaFile,
        Integer ukuranKB,
        String statusUnggah,
        boolean opsiLengkap,
        String statusValidasi,
        String catatanValidasi,
        String namaStafValidator,
        LocalDateTime waktuValidasi) {

    public String ukuran() {
        if (ukuranKB == null) return "";
        return ukuranKB < 1024 ? ukuranKB + " KB" : String.format("%.1f MB", ukuranKB / 1024.0).replace('.', ',');
    }
}
