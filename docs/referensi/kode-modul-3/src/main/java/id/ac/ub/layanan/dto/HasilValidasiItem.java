package id.ac.ub.layanan.dto;

public record HasilValidasiItem(
        boolean berhasil,
        String pesan,
        String statusItem,
        String hasilValidasi,        // Lengkap / Tidak Lengkap / Belum Selesai
        String keteranganValidasi) { // "nama staf, tanggal dan waktu"
}
