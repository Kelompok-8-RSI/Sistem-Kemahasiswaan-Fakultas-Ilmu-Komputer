package id.ac.ub.layanan.dto;

import java.time.LocalDateTime;

public record LangkahRiwayat(String status, String catatan, LocalDateTime waktu,
                             String namaPelaku, String peranPelaku) { }
