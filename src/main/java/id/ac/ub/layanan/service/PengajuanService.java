package id.ac.ub.layanan.service;

import id.ac.ub.layanan.dto.HasilAksi;
import id.ac.ub.layanan.dto.LangkahRiwayat;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.entity.Pengajuan;
import id.ac.ub.layanan.entity.RiwayatStatus;
import id.ac.ub.layanan.exception.DataTidakDitemukanException;
import id.ac.ub.layanan.repository.AkunPenggunaRepository;
import id.ac.ub.layanan.repository.AntreanVerifikasiRepository;
import id.ac.ub.layanan.repository.PengajuanRepository;
import id.ac.ub.layanan.repository.RiwayatStatusRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PengajuanService {

    private static final List<String> OPSI_STANDAR = List.of(
            "Menunggu Verifikasi", "Sedang Diproses", "Revisi", "Selesai");
    private static final List<String> OPSI_LEGALISASI = List.of(
            "Menunggu Verifikasi", "Sedang Diproses", "Revisi", "Menunggu Legalisasi Pejabat", "Selesai");
    private static final Set<String> STATUS_SAH = Set.copyOf(OPSI_LEGALISASI);

    private final PengajuanRepository pengajuanRepository;
    private final AntreanVerifikasiRepository antreanVerifikasiRepository;
    private final RiwayatStatusRepository riwayatStatusRepository;
    private final AkunPenggunaRepository akunRepository;
    private final ValidasiLampiranService validasiLampiranService;
    private final Clock clock;

    public PengajuanService(PengajuanRepository pengajuanRepository,
                            AntreanVerifikasiRepository antreanVerifikasiRepository,
                            RiwayatStatusRepository riwayatStatusRepository,
                            AkunPenggunaRepository akunRepository,
                            ValidasiLampiranService validasiLampiranService,
                            Clock clock) {
        this.pengajuanRepository = pengajuanRepository;
        this.antreanVerifikasiRepository = antreanVerifikasiRepository;
        this.riwayatStatusRepository = riwayatStatusRepository;
        this.akunRepository = akunRepository;
        this.validasiLampiranService = validasiLampiranService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public Pengajuan requestDataPengajuan(Integer idPengajuan) {
        return pengajuanRepository.getDetailPengajuan(idPengajuan)
                .orElseThrow(() -> new DataTidakDitemukanException("Data pengajuan tidak ditemukan"));
    }

    public List<String> getOpsiStatusByJalur(boolean perluLegalisasi) {
        return perluLegalisasi ? OPSI_LEGALISASI : OPSI_STANDAR;
    }

    @Transactional(readOnly = true)
    public List<LangkahRiwayat> getRiwayatSingkat(Integer idPengajuan) {
        List<RiwayatStatus> log = riwayatStatusRepository.findByIdPengajuanOrderByWaktu(idPengajuan);
        if (log.isEmpty()) {
            return List.of();
        }
        Map<Integer, AkunPengguna> pelaku = akunRepository
                .findAllById(log.stream().map(RiwayatStatus::getIdPelaku).distinct().toList())
                .stream()
                .collect(Collectors.toMap(AkunPengguna::getIdPengguna, Function.identity()));
        return log.stream()
                .sorted(Comparator.comparing(RiwayatStatus::getWaktu)
                        .thenComparing(RiwayatStatus::getIdRiwayat, Comparator.nullsLast(Comparator.naturalOrder()))
                        .reversed())
                .map(r -> {
                    AkunPengguna a = pelaku.get(r.getIdPelaku());
                    return new LangkahRiwayat(r.getStatus(), r.getCatatan(), r.getWaktu(),
                            a != null ? a.getNama() : "-", a != null ? a.getPeran() : "");
                })
                .toList();
    }

    @Transactional
    public HasilAksi prosesPembaruanStatus(Integer idPengajuan, String statusBaru,
                                           String catatan, Integer idStaf) {
        Pengajuan dataPengajuan = pengajuanRepository.getDetailPengajuan(idPengajuan).orElse(null);
        if (dataPengajuan == null) {
            return new HasilAksi(false, "Data pengajuan tidak ditemukan");
        }
        if ("Selesai".equals(dataPengajuan.getStatus()) || "Draft".equals(dataPengajuan.getStatus())) {
            return new HasilAksi(false, "Status pengajuan saat ini tidak dapat diperbarui");
        }

        boolean jalurDokumen = dataPengajuan.getJenisDokumen().getPerluLegalisasi();
        if (!validasiStatus(statusBaru, catatan, jalurDokumen)) {
            return new HasilAksi(false, "Pilihan status tidak sah atau catatan perbaikan belum diisi");
        }

        if ("Menunggu Legalisasi Pejabat".equals(statusBaru) || "Selesai".equals(statusBaru)) {
            String hasilValidasi = validasiLampiranService.hitungHasilValidasi(
                    validasiLampiranService.requestChecklist(idPengajuan));
            if (!"Lengkap".equals(hasilValidasi)) {
                return new HasilAksi(false,
                        "Semua lampiran wajib harus divalidasi Lengkap sebelum mengubah status ke " + statusBaru);
            }
        }

        dataPengajuan.updateStatusPengajuan(statusBaru);
        pengajuanRepository.save(dataPengajuan);

        antreanVerifikasiRepository.findByIdPengajuan(idPengajuan).ifPresent(antrean -> {
            antrean.setStatusAntrean(tentukanStatusAntrean(statusBaru));
            antreanVerifikasiRepository.save(antrean);
        });

        String catatanFinal = (catatan == null || catatan.isBlank()) ? null : catatan.trim();
        riwayatStatusRepository.catatLog(idPengajuan, statusBaru, catatanFinal, idStaf, LocalDateTime.now(clock));
        return new HasilAksi(true, buatPesanBerhasil(statusBaru));
    }

    public boolean validasiStatus(String statusBaru, String catatan, boolean jalurDokumen) {
        if (statusBaru == null || !STATUS_SAH.contains(statusBaru)) {
            return false;
        }
        if (!jalurDokumen && "Menunggu Legalisasi Pejabat".equals(statusBaru)) {
            return false;
        }
        if ("Revisi".equals(statusBaru) && (catatan == null || catatan.isBlank())) {
            return false;
        }
        return true;
    }

    private String tentukanStatusAntrean(String statusBaru) {
        return switch (statusBaru) {
            case "Menunggu Verifikasi" -> "Menunggu";
            case "Sedang Diproses" -> "Diproses";
            default -> "Selesai";
        };
    }

    private String buatPesanBerhasil(String statusBaru) {
        return switch (statusBaru) {
            case "Revisi" -> "Status diperbarui menjadi Revisi. Catatan perbaikan dikirim ke mahasiswa.";
            case "Menunggu Legalisasi Pejabat" ->
                    "Status diperbarui menjadi Menunggu Legalisasi Pejabat. Pengajuan diteruskan ke Pejabat Fakultas.";
            default -> "Status diperbarui menjadi " + statusBaru + ".";
        };
    }
}
