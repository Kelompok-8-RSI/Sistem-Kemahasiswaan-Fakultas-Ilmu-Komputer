package id.ac.ub.layanan.service;

import id.ac.ub.layanan.dto.BarisAntrean;
import id.ac.ub.layanan.dto.HasilAntrean;
import id.ac.ub.layanan.entity.Pengajuan;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import id.ac.ub.layanan.repository.PengajuanRepository;

@Service
public class AntreanService {

    private static final int BATAS_MERAH = 3;   // sisa waktu < 3 hari -> Merah
    private static final int BATAS_KUNING = 7;  // sisa waktu 3-7 hari -> Kuning, selebihnya Hijau

    private final PengajuanRepository pengajuanRepository;
    private final Clock clock;

    public AntreanService(PengajuanRepository pengajuanRepository, Clock clock) {
        this.pengajuanRepository = pengajuanRepository;
        this.clock = clock;
    }

    // PC-21: Menampilkan Antrean Pengajuan Berdasarkan Prioritas
    @Transactional(readOnly = true)
    public HasilAntrean requestAntreanPengajuan() {
        List<Pengajuan> daftarAntrean = pengajuanRepository.getDaftarAntrean();
        if (daftarAntrean.isEmpty()) {
            return new HasilAntrean(List.of(), "Antrean kosong");
        }
        List<Integer> daftarSisaWaktu = hitungSisaWaktu(daftarAntrean);
        List<Pengajuan> daftarAntreanTerurut = urutkanAntrean(daftarAntrean, daftarSisaWaktu);
        List<String> daftarLabel = beriLabelPrioritas(daftarAntreanTerurut);

        List<BarisAntrean> baris = new ArrayList<>();
        for (int i = 0; i < daftarAntreanTerurut.size(); i++) {
            Pengajuan p = daftarAntreanTerurut.get(i);
            baris.add(new BarisAntrean(p, selisihHariDariHariIni(p.getTanggalTenggat()), daftarLabel.get(i)));
        }
        return new HasilAntrean(baris, null);
    }

    // PC-22: Menghitung Sisa Waktu Pengajuan (hari; <= 0 jika tenggat terlewat)
    public List<Integer> hitungSisaWaktu(List<Pengajuan> daftarAntrean) {
        return daftarAntrean.stream()
                .map(p -> selisihHariDariHariIni(p.getTanggalTenggat()))
                .toList();
    }

    // PC-23: Mengurutkan Antrean (ascending berdasarkan sisa waktu)
    public List<Pengajuan> urutkanAntrean(List<Pengajuan> daftarAntrean, List<Integer> daftarSisaWaktu) {
        return IntStream.range(0, daftarAntrean.size())
                .boxed()
                .sorted(Comparator.comparingInt(daftarSisaWaktu::get))
                .map(daftarAntrean::get)
                .toList();
    }

    // PC-24: Memberi Label Prioritas Antrean
    public List<String> beriLabelPrioritas(List<Pengajuan> daftarAntreanTerurut) {
        return daftarAntreanTerurut.stream().map(p -> {
            int sisaWaktu = selisihHariDariHariIni(p.getTanggalTenggat());
            if (sisaWaktu <= 0) return "Merah (Terlewat)";
            if (sisaWaktu < BATAS_MERAH) return "Merah";
            if (sisaWaktu <= BATAS_KUNING) return "Kuning";
            return "Hijau";
        }).toList();
    }

    private int selisihHariDariHariIni(LocalDate tanggal) {
        return Math.toIntExact(ChronoUnit.DAYS.between(LocalDate.now(clock), tanggal));
    }
}
