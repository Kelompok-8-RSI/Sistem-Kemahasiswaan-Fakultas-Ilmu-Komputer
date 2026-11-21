package id.ac.ub.layanan.dto;

import id.ac.ub.layanan.entity.BerkasLampiran;
import id.ac.ub.layanan.entity.Pengajuan;
import java.util.List;

public record DetailLegalisasi(Pengajuan pengajuan, List<BerkasLampiran> lampiran,
                               List<LangkahRiwayat> riwayat) {

    public LangkahRiwayat diteruskan() {
        return riwayat.stream().filter(l -> "Menunggu Legalisasi Pejabat".equals(l.status()))
                .findFirst().orElse(null);
    }
}
