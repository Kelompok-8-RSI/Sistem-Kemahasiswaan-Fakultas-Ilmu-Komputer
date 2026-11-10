package id.ac.ub.layanan.dto;

import java.time.LocalDateTime;

// satu baris riwayat status (RiwayatStatus) beserta nama dan peran pelakunya
public record LangkahRiwayat(String status, String catatan, LocalDateTime waktu,
                             String namaPelaku, String peranPelaku) { }
