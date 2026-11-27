package id.ac.ub.layanan.service;

import id.ac.ub.layanan.dto.HasilAksi;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.entity.AntreanVerifikasi;
import id.ac.ub.layanan.entity.BerkasLampiran;
import id.ac.ub.layanan.entity.Pengajuan;
import id.ac.ub.layanan.entity.PersyaratanDokumen;
import id.ac.ub.layanan.entity.RiwayatStatus;
import id.ac.ub.layanan.exception.DataTidakDitemukanException;
import id.ac.ub.layanan.repository.AkunPenggunaRepository;
import id.ac.ub.layanan.repository.AntreanVerifikasiRepository;
import id.ac.ub.layanan.repository.BerkasLampiranRepository;
import id.ac.ub.layanan.repository.PengajuanRepository;
import id.ac.ub.layanan.repository.PersyaratanDokumenRepository;
import id.ac.ub.layanan.repository.RiwayatStatusRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RevisiPengajuanService {

    private static final long UKURAN_MAKSIMAL = 5L * 1024 * 1024;
    private static final Set<String> FORMAT_DIIJINKAN = Set.of("pdf", "jpg", "jpeg", "png");

    private final PengajuanRepository pengajuanRepository;
    private final PersyaratanDokumenRepository persyaratanDokumenRepository;
    private final BerkasLampiranRepository berkasLampiranRepository;
    private final AntreanVerifikasiRepository antreanVerifikasiRepository;
    private final RiwayatStatusRepository riwayatStatusRepository;
    private final AkunPenggunaRepository akunPenggunaRepository;
    private final Clock clock;
    private final Path direktoriUnggah;

    public record CatatanRevisi(
            String catatan,
            LocalDateTime waktu,
            String namaPelaku,
            String peranPelaku) { }

    public record ItemRevisi(
            Integer idPersyaratan,
            Integer idLampiran,
            String namaPersyaratan,
            String namaFile,
            Integer ukuranKB,
            Integer versi,
            String statusItem,
            String statusValidasi,
            String catatanValidasi,
            boolean sesuai) {

        public String ukuran() {
            if (ukuranKB == null) return "";
            return ukuranKB < 1024 ? ukuranKB + " KB" : String.format("%.1f MB", ukuranKB / 1024.0).replace('.', ',');
        }
    }

    public RevisiPengajuanService(PengajuanRepository pengajuanRepository,
                                  PersyaratanDokumenRepository persyaratanDokumenRepository,
                                  BerkasLampiranRepository berkasLampiranRepository,
                                  AntreanVerifikasiRepository antreanVerifikasiRepository,
                                  RiwayatStatusRepository riwayatStatusRepository,
                                  AkunPenggunaRepository akunPenggunaRepository,
                                  Clock clock,
                                  @Value("${app.upload-dir}") String direktoriUnggah) {
        this.pengajuanRepository = pengajuanRepository;
        this.persyaratanDokumenRepository = persyaratanDokumenRepository;
        this.berkasLampiranRepository = berkasLampiranRepository;
        this.antreanVerifikasiRepository = antreanVerifikasiRepository;
        this.riwayatStatusRepository = riwayatStatusRepository;
        this.akunPenggunaRepository = akunPenggunaRepository;
        this.clock = clock;
        this.direktoriUnggah = Path.of(direktoriUnggah).toAbsolutePath().normalize();
    }

    @Transactional(readOnly = true)
    public Pengajuan getPengajuanRevisi(Integer idPengajuan, Integer idMahasiswa) {
        Pengajuan pengajuan = pengajuanRepository.getDetailPengajuan(idPengajuan)
                .orElseThrow(() -> new DataTidakDitemukanException("Pengajuan tidak ditemukan"));
        if (!pengajuan.getPengguna().getIdPengguna().equals(idMahasiswa)) {
            throw new DataTidakDitemukanException("Pengajuan bukan milik Anda");
        }
        return pengajuan;
    }

    @Transactional(readOnly = true)
    public CatatanRevisi getCatatanRevisiTerakhir(Integer idPengajuan) {
        List<RiwayatStatus> riwayat = riwayatStatusRepository.findByIdPengajuanOrderByWaktu(idPengajuan);
        RiwayatStatus logRevisi = riwayat.stream()
                .filter(r -> "Revisi".equals(r.getStatus()))
                .reduce((first, second) -> second)
                .orElse(null);

        if (logRevisi == null) {
            return new CatatanRevisi("Pengajuan memerlukan perbaikan dokumen.", null, null, null);
        }

        String namaPelaku = null;
        String peranPelaku = null;
        if (logRevisi.getIdPelaku() != null) {
            AkunPengguna pelaku = akunPenggunaRepository.findById(logRevisi.getIdPelaku()).orElse(null);
            if (pelaku != null) {
                namaPelaku = pelaku.getNama();
                peranPelaku = pelaku.getPeran();
            }
        }
        return new CatatanRevisi(logRevisi.getCatatan(), logRevisi.getWaktu(), namaPelaku, peranPelaku);
    }

    @Transactional(readOnly = true)
    public List<ItemRevisi> getDaftarItemRevisi(Integer idPengajuan) {
        Pengajuan pengajuan = pengajuanRepository.getDetailPengajuan(idPengajuan)
                .orElseThrow(() -> new DataTidakDitemukanException("Pengajuan tidak ditemukan"));
        List<PersyaratanDokumen> listPersyaratan = persyaratanDokumenRepository
                .findByIdJenisDokumenAndWajibTrue(pengajuan.getJenisDokumen().getIdJenisDokumen());
        List<BerkasLampiran> listLampiran = berkasLampiranRepository.findByIdPengajuan(idPengajuan);

        List<ItemRevisi> hasil = new ArrayList<>();
        for (PersyaratanDokumen p : listPersyaratan) {
            BerkasLampiran lampiranAktif = listLampiran.stream()
                    .filter(l -> l.getIdPersyaratan().equals(p.getIdPersyaratan()))
                    .filter(l -> "Terunggah".equals(l.getStatusItem()))
                    .max(Comparator.comparing(BerkasLampiran::getVersi, Comparator.nullsLast(Comparator.naturalOrder())))
                    .orElse(null);

            if (lampiranAktif == null) {
                hasil.add(new ItemRevisi(
                        p.getIdPersyaratan(),
                        null,
                        p.getNamaPersyaratan(),
                        null,
                        null,
                        1,
                        null,
                        null,
                        null,
                        false
                ));
            } else {
                boolean sesuai = !"Tidak Lengkap".equals(lampiranAktif.getStatusValidasi());
                hasil.add(new ItemRevisi(
                        p.getIdPersyaratan(),
                        lampiranAktif.getIdLampiran(),
                        p.getNamaPersyaratan(),
                        lampiranAktif.getNamaFile(),
                        lampiranAktif.getUkuranKB(),
                        lampiranAktif.getVersi(),
                        lampiranAktif.getStatusItem(),
                        lampiranAktif.getStatusValidasi(),
                        lampiranAktif.getCatatanValidasi(),
                        sesuai
                ));
            }
        }
        return hasil;
    }

    @Transactional(readOnly = true)
    public boolean cekSemuaItemSesuai(Integer idPengajuan) {
        List<ItemRevisi> items = getDaftarItemRevisi(idPengajuan);
        if (items.isEmpty()) {
            return false;
        }
        return items.stream().allMatch(ItemRevisi::sesuai);
    }

    @Transactional
    public HasilAksi gantiLampiran(Integer idPengajuan, Integer idPersyaratan, MultipartFile file, Integer idMahasiswa) {
        Pengajuan pengajuan = getPengajuanRevisi(idPengajuan, idMahasiswa);
        if (!"Revisi".equals(pengajuan.getStatus())) {
            return new HasilAksi(false, "Pengajuan tidak berstatus Revisi");
        }

        String validasi = validasiFormatDanUkuran(file);
        if (validasi != null) {
            return new HasilAksi(false, validasi);
        }

        PersyaratanDokumen persyaratan = persyaratanDokumenRepository.findById(idPersyaratan)
                .orElseThrow(() -> new DataTidakDitemukanException("Persyaratan dokumen tidak ditemukan"));
        if (!persyaratan.getIdJenisDokumen().equals(pengajuan.getJenisDokumen().getIdJenisDokumen())) {
            return new HasilAksi(false, "Persyaratan dokumen tidak sesuai dengan jenis dokumen");
        }

        List<BerkasLampiran> lampiranPengajuan = berkasLampiranRepository.findByIdPengajuan(idPengajuan);
        int maxVersi = lampiranPengajuan.stream()
                .filter(l -> l.getIdPersyaratan().equals(idPersyaratan))
                .mapToInt(l -> l.getVersi() != null ? l.getVersi() : 1)
                .max()
                .orElse(0);

        String ekstensi = ekstensiFile(file.getOriginalFilename());
        String namaTersimpan = UUID.randomUUID() + "." + ekstensi;
        Path tujuan = direktoriUnggah.resolve(namaTersimpan);
        try {
            Files.createDirectories(direktoriUnggah);
            file.transferTo(tujuan);
        } catch (IOException e) {
            return new HasilAksi(false, "Lampiran gagal disimpan di penyimpanan");
        }

        for (BerkasLampiran lama : lampiranPengajuan) {
            if (lama.getIdPersyaratan().equals(idPersyaratan) && "Terunggah".equals(lama.getStatusItem())) {
                lama.setStatusItem("Diganti");
                berkasLampiranRepository.save(lama);
            }
        }

        BerkasLampiran baru = new BerkasLampiran();
        baru.setIdPengajuan(idPengajuan);
        baru.setIdPersyaratan(idPersyaratan);
        baru.setNamaFile(file.getOriginalFilename());
        baru.setPathFile(tujuan.toString());
        baru.setUkuranKB((int) Math.ceil(file.getSize() / 1024.0));
        baru.setStatusItem("Terunggah");
        baru.setVersi(maxVersi + 1);
        baru.setStatusValidasi("Belum Diperiksa");
        berkasLampiranRepository.save(baru);

        return new HasilAksi(true, "Lampiran berhasil diperbarui (versi " + baru.getVersi() + ")");
    }

    @Transactional
    public HasilAksi submitRevisi(Integer idPengajuan, Integer idMahasiswa) {
        Pengajuan pengajuan = getPengajuanRevisi(idPengajuan, idMahasiswa);
        if (!"Revisi".equals(pengajuan.getStatus())) {
            return new HasilAksi(false, "Hanya pengajuan berstatus Revisi yang dapat dikirim ulang");
        }

        if (!cekSemuaItemSesuai(idPengajuan)) {
            return new HasilAksi(false, "Masih ada lampiran bermasalah yang belum diganti");
        }

        pengajuan.updateStatusPengajuan("Menunggu Verifikasi");
        pengajuanRepository.save(pengajuan);

        AntreanVerifikasi antrean = antreanVerifikasiRepository.findByIdPengajuan(idPengajuan)
                .orElseGet(() -> {
                    AntreanVerifikasi baru = new AntreanVerifikasi();
                    baru.setIdPengajuan(idPengajuan);
                    return baru;
                });
        antrean.setStatusAntrean("Menunggu");
        antrean.setWaktuMasuk(LocalDateTime.now(clock));
        antreanVerifikasiRepository.save(antrean);

        riwayatStatusRepository.catatLog(
                idPengajuan,
                "Menunggu Verifikasi",
                "Pengajuan direvisi dan dikirim ulang",
                idMahasiswa,
                LocalDateTime.now(clock)
        );

        return new HasilAksi(true, "Revisi berhasil dikirim");
    }

    public String validasiFormatDanUkuran(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "File tidak boleh kosong. Silakan unggah ulang dokumen revisi.";
        }
        if (file.getSize() > UKURAN_MAKSIMAL) {
            return "Ukuran file melebihi batas maksimal 5 MB. Unggah ulang dokumen revisi maksimal 5 MB.";
        }
        String ekstensi = ekstensiFile(file.getOriginalFilename());
        if (!FORMAT_DIIJINKAN.contains(ekstensi)) {
            return "Ekstensi file tidak sesuai ketentuan. Unggah ulang dokumen revisi berformat PDF, JPG, atau PNG.";
        }
        return null;
    }

    private String ekstensiFile(String namaFile) {
        if (namaFile == null || !namaFile.contains(".")) {
            return "";
        }
        return namaFile.substring(namaFile.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
