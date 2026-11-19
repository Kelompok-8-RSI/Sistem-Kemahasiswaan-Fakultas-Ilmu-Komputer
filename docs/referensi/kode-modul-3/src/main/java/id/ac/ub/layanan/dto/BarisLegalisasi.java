package id.ac.ub.layanan.dto;

import id.ac.ub.layanan.entity.Pengajuan;

// satu baris antrean legalisasi: pengajuan + siapa dan kapan diteruskan oleh Staf (kolom "Diteruskan")
public record BarisLegalisasi(Pengajuan pengajuan, LangkahRiwayat diteruskan) { }
