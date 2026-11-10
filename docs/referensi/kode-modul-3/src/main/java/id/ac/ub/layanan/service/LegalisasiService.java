package id.ac.ub.layanan.service;

import id.ac.ub.layanan.dto.BarisLegalisasi;
import id.ac.ub.layanan.dto.DetailLegalisasi;
import id.ac.ub.layanan.dto.HasilAksi;
import id.ac.ub.layanan.dto.LangkahRiwayat;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.entity.Pengajuan;
import id.ac.ub.layanan.entity.RiwayatStatus;
import id.ac.ub.layanan.repository.AkunPenggunaRepository;
import id.ac.ub.layanan.repository.BerkasLampiranRepository;
import id.ac.ub.layanan.repository.PengajuanRepository;
import id.ac.ub.layanan.repository.RiwayatStatusRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LegalisasiService {

    private static final String STATUS_MENUNGGU_LEGALISASI = "Menunggu Legalisasi Pejabat";

    private final PengajuanRepository pengajuanRepository;
    private final BerkasLampiranRepository berkasLampiranRepository;
    private final RiwayatStatusRepository riwayatStatusRepository;
    private final AkunPenggunaRepository akunRepository;
    private final Clock clock;

    public LegalisasiService(PengajuanRepository pengajuanRepository,
                             BerkasLampiranRepository berkasLampiranRepository,
                             RiwayatStatusRepository riwayatStatusRepository,
                             AkunPenggunaRepository akunRepository, Clock clock) {
        this.pengajuanRepository = pengajuanRepository;
        this.berkasLampiranRepository = berkasLampiranRepository;
        this.riwayatStatusRepository = riwayatStatusRepository;
        this.akunRepository = akunRepository;
        this.clock = clock;
    }

    // MR-SR-18: antrean legalisasi pada akun Pejabat (kolom Diteruskan diambil dari RiwayatStatus)
    @Transactional(readOnly = true)
    public List<BarisLegalisasi> requestAntreanLegalisasi() {
        return pengajuanRepository.getDaftarByStatus(STATUS_MENUNGGU_LEGALISASI).stream()
                .map(p -> new BarisLegalisasi(p, susunRiwayat(p.getIdPengajuan()).stream()
                        .filter(l -> STATUS_MENUNGGU_LEGALISASI.equals(l.status()))
                        .findFirst().orElse(null)))
                .toList();
    }

    // PC-33: Menampilkan Detail Pengajuan dan Lampiran Legalisasi
    @Transactional(readOnly = true)
    public Optional<DetailLegalisasi> requestDetailLegalisasi(Integer idPengajuan) {
        return pengajuanRepository.getDetailPengajuan(idPengajuan)
                .filter(p -> STATUS_MENUNGGU_LEGALISASI.equals(p.getStatus()))
                .map(p -> new DetailLegalisasi(p,
                        // hanya lampiran aktif yang sudah divalidasi Lengkap oleh Staf (UI-09)
                        berkasLampiranRepository.findByIdPengajuan(idPengajuan).stream()
                                .filter(l -> "Terunggah".equals(l.getStatusItem())
                                        && "Lengkap".equals(l.getStatusValidasi()))
                                .toList(),
                        susunRiwayat(idPengajuan)));
    }

    // kartu Riwayat pada UI-09: log RiwayatStatus beserta nama dan peran pelaku, terbaru di atas
    private List<LangkahRiwayat> susunRiwayat(Integer idPengajuan) {
        List<RiwayatStatus> log = riwayatStatusRepository.findByIdPengajuanOrderByWaktu(idPengajuan);
        Map<Integer, AkunPengguna> pelaku = akunRepository
                .findAllById(log.stream().map(RiwayatStatus::getIdPelaku).distinct().toList()).stream()
                .collect(Collectors.toMap(AkunPengguna::getIdPengguna, Function.identity()));
        return log.stream()
                .sorted(Comparator.comparing(RiwayatStatus::getWaktu).reversed())
                .map(r -> {
                    AkunPengguna a = pelaku.get(r.getIdPelaku());
                    return new LangkahRiwayat(r.getStatus(), r.getCatatan(), r.getWaktu(),
                            a != null ? a.getNama() : "-", a != null ? a.getPeran() : "");
                })
                .toList();
    }

    // PC-34: Memproses Keputusan Legalisasi Dokumen oleh Pejabat
    @Transactional
    public HasilAksi prosesKeputusan(Integer idPengajuan, String keputusan,
                                     String catatanAlasan, Integer idPejabat) {
        boolean isValid = validasiCatatan(keputusan, catatanAlasan);
        if (!isValid) {
            return new HasilAksi(false, "Keputusan tidak valid atau alasan penolakan wajib diisi");
        }

        Pengajuan pengajuan = pengajuanRepository.findById(idPengajuan).orElse(null);
        // pengaman tambahan: hanya pengajuan berstatus "Menunggu Legalisasi Pejabat" yang boleh diputuskan
        if (pengajuan == null || !STATUS_MENUNGGU_LEGALISASI.equals(pengajuan.getStatus())) {
            return new HasilAksi(false, "Gagal menyimpan keputusan legalisasi di database");
        }

        String statusBaru;
        String catatanFinal;
        if ("Setuju".equals(keputusan)) {
            statusBaru = "Selesai";
            catatanFinal = "Disetujui dan disahkan oleh Pejabat Fakultas";
        } else {
            statusBaru = "Revisi";
            catatanFinal = catatanAlasan;
        }

        pengajuan.updateStatusPengajuan(statusBaru);
        pengajuanRepository.save(pengajuan);

        LocalDateTime timestamp = LocalDateTime.now(clock);
        riwayatStatusRepository.catatLog(idPengajuan, statusBaru, catatanFinal, idPejabat, timestamp);
        return new HasilAksi(true, "Keputusan legalisasi tersimpan. Status pengajuan: " + statusBaru);
    }

    // PC-35: Validasi Catatan Keputusan Legalisasi
    public boolean validasiCatatan(String keputusan, String catatanAlasan) {
        if (!"Setuju".equals(keputusan) && !"Tolak".equals(keputusan)) {
            return false;
        }
        if ("Tolak".equals(keputusan) && (catatanAlasan == null || catatanAlasan.isBlank())) {
            return false;
        }
        return true;
    }
}
