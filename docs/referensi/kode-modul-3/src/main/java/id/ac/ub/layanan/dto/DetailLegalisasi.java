package id.ac.ub.layanan.dto;

import id.ac.ub.layanan.entity.BerkasLampiran;
import id.ac.ub.layanan.entity.Pengajuan;
import java.util.List;

// data HalamanDetailLegalisasi: pengajuan, lampiran berstatus Lengkap, dan riwayat (terbaru di atas)
public record DetailLegalisasi(Pengajuan pengajuan, List<BerkasLampiran> lampiran,
                               List<LangkahRiwayat> riwayat) {

    // langkah ketika Staf meneruskan pengajuan ke Pejabat (subjudul "diteruskan oleh ...")
    public LangkahRiwayat diteruskan() {
        return riwayat.stream().filter(l -> "Menunggu Legalisasi Pejabat".equals(l.status()))
                .findFirst().orElse(null);
    }
}
