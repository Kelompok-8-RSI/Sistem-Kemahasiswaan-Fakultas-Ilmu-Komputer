package id.ac.ub.layanan.dto;

public record HasilLogin(
        boolean berhasil,
        String pesan,
        Integer idPengguna,
        String nama,
        String peran,
        String sessionToken) { }
