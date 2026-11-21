package id.ac.ub.layanan.dto;

public record HasilValidasiItem(
        boolean berhasil,
        String pesan,
        String statusItem,
        String hasilValidasi,
        String keteranganValidasi) {
}
