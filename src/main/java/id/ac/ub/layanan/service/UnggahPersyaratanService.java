package id.ac.ub.layanan.service;

import id.ac.ub.layanan.dto.HasilAksi;
import id.ac.ub.layanan.dto.ItemChecklist;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.entity.AntreanVerifikasi;
import id.ac.ub.layanan.entity.BerkasLampiran;
import id.ac.ub.layanan.entity.Pengajuan;
import id.ac.ub.layanan.entity.PersyaratanDokumen;
import id.ac.ub.layanan.exception.DataTidakDitemukanException;
import id.ac.ub.layanan.repository.AntreanVerifikasiRepository;
import id.ac.ub.layanan.repository.BerkasLampiranRepository;
import id.ac.ub.layanan.repository.PengajuanRepository;
import id.ac.ub.layanan.repository.PersyaratanDokumenRepository;
import id.ac.ub.layanan.repository.RiwayatStatusRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.LocalDateTime;
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
public class UnggahPersyaratanService {

    private static final long UKURAN_MAKSIMAL = 5L * 1024 * 1024;
    private static final Set<String> FORMAT_DIIJINKAN = Set.of("pdf", "jpg", "jpeg", "png");

    private final PengajuanRepository pengajuanRepository;
    private final PersyaratanDokumenRepository persyaratanDokumenRepository;
    private final BerkasLampiranRepository berkasLampiranRepository;
    private final AntreanVerifikasiRepository antreanVerifikasiRepository;
    private final RiwayatStatusRepository riwayatStatusRepository;
    private final Clock clock;
    private final Path direktoriUnggah;

    public UnggahPersyaratanService(PengajuanRepository pengajuanRepository,
                                    PersyaratanDokumenRepository persyaratanDokumenRepository,
                                    BerkasLampiranRepository berkasLampiranRepository,
                                    AntreanVerifikasiRepository antreanVerifikasiRepository,
                                    RiwayatStatusRepository riwayatStatusRepository,
                                    Clock clock,
                                    @Value("${app.upload-dir}") String direktoriUnggah) {
        this.pengajuanRepository = pengajuanRepository;
        this.persyaratanDokumenRepository = persyaratanDokumenRepository;
        this.berkasLampiranRepository = berkasLampiranRepository;
        this.antreanVerifikasiRepository = antreanVerifikasiRepository;
        this.riwayatStatusRepository = riwayatStatusRepository;
        this.clock = clock;
        this.direktoriUnggah = Path.of(direktoriUnggah).toAbsolutePath().normalize();
    }

    @Transactional(readOnly = true)
    public List<ItemChecklist> requestDaftarPersyaratan(Integer idPengajuan) {
        Pengajuan pengajuan = ambilPengajuanDetail(idPengajuan);
        List<BerkasLampiran> lampiran = berkasLampiranRepository.findByIdPengajuan(idPengajuan);
        return persyaratanDokumenRepository
                .findByIdJenisDokumenAndWajibTrue(pengajuan.getJenisDokumen().getIdJenisDokumen())
                .stream()
                .map(persyaratan -> buatItem(persyaratan, lampiran))
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean cekKelengkapanItemWajib(Integer idPengajuan) {
        return requestDaftarPersyaratan(idPengajuan).stream()
                .allMatch(item -> "Terunggah".equals(item.statusUnggah()));
    }

    @Transactional
    public HasilAksi requestUnggahLampiran(Integer idPengajuan, Integer idPersyaratan, MultipartFile file) {
        String hasilValidasi = validasiFormatUkuran(file);
        if (hasilValidasi != null) {
            return new HasilAksi(false, hasilValidasi);
        }

        Pengajuan pengajuan = ambilPengajuanDetail(idPengajuan);
        if (!"Draft".equals(pengajuan.getStatus())) {
            return new HasilAksi(false, "Lampiran hanya dapat diubah pada pengajuan draft");
        }
        PersyaratanDokumen persyaratan = persyaratanDokumenRepository.findById(idPersyaratan)
                .orElseThrow(() -> new DataTidakDitemukanException("Persyaratan tidak ditemukan"));
        if (!persyaratan.getIdJenisDokumen().equals(pengajuan.getJenisDokumen().getIdJenisDokumen())) {
            return new HasilAksi(false, "Persyaratan tidak sesuai dengan jenis dokumen");
        }

        String ekstensi = ekstensiFile(file.getOriginalFilename());
        String namaTersimpan = UUID.randomUUID() + "." + ekstensi;
        Path tujuan = direktoriUnggah.resolve(namaTersimpan);
        try {
            Files.createDirectories(direktoriUnggah);
            file.transferTo(tujuan);
        } catch (IOException e) {
            return new HasilAksi(false, "Lampiran gagal disimpan");
        }

        try {
            hapusLampiranAktif(idPengajuan, idPersyaratan);
            BerkasLampiran lampiran = new BerkasLampiran();
            lampiran.setIdPengajuan(idPengajuan);
            lampiran.setIdPersyaratan(idPersyaratan);
            lampiran.setNamaFile(file.getOriginalFilename());
            lampiran.setPathFile(tujuan.toString());
            lampiran.setUkuranKB((int) Math.ceil(file.getSize() / 1024.0));
            lampiran.setStatusItem("Terunggah");
            lampiran.setVersi(1);
            lampiran.setStatusValidasi("Belum Diperiksa");
            berkasLampiranRepository.save(lampiran);
            return new HasilAksi(true, "Lampiran berhasil diunggah");
        } catch (RuntimeException e) {
            hapusBerkas(tujuan);
            throw e;
        }
    }

    @Transactional
    public HasilAksi hapusLampiran(Integer idPengajuan, Integer idLampiran) {
        if (!"Draft".equals(ambilPengajuan(idPengajuan).getStatus())) {
            return new HasilAksi(false, "Lampiran hanya dapat diubah pada pengajuan draft");
        }
        BerkasLampiran lampiran = berkasLampiranRepository.findById(idLampiran)
                .orElseThrow(() -> new DataTidakDitemukanException("Lampiran tidak ditemukan"));
        if (!lampiran.getIdPengajuan().equals(idPengajuan)) {
            return new HasilAksi(false, "Lampiran tidak sesuai dengan pengajuan");
        }
        berkasLampiranRepository.delete(lampiran);
        hapusBerkas(Path.of(lampiran.getPathFile()));
        return new HasilAksi(true, "Lampiran berhasil dihapus");
    }

    @Transactional
    public HasilAksi submitPengajuan(Integer idPengajuan, AkunPengguna akun) {
        if (!cekKelengkapanItemWajib(idPengajuan)) {
            return new HasilAksi(false, "Masih ada persyaratan wajib yang belum diunggah");
        }
        Pengajuan pengajuan = ambilPengajuan(idPengajuan);
        if (!"Draft".equals(pengajuan.getStatus())) {
            return new HasilAksi(false, "Pengajuan tidak dapat dikirim ulang");
        }
        pengajuan.updateStatusPengajuan("Menunggu Verifikasi");
        pengajuanRepository.save(pengajuan);

        AntreanVerifikasi antrean = new AntreanVerifikasi();
        antrean.setIdPengajuan(idPengajuan);
        antrean.setStatusAntrean("Menunggu");
        antrean.setWaktuMasuk(LocalDateTime.now(clock));
        antreanVerifikasiRepository.save(antrean);
        riwayatStatusRepository.catatLog(idPengajuan, "Menunggu Verifikasi", null,
                akun.getIdPengguna(), LocalDateTime.now(clock));
        return new HasilAksi(true, "Pengajuan berhasil dikirim");
    }

    public String validasiFormatUkuran(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "File tidak boleh kosong. Silakan pilih file terlebih dahulu.";
        }
        if (file.getSize() > UKURAN_MAKSIMAL) {
            return "Ukuran file melebihi batas maksimal 5 MB. Pilih file berukuran maksimal 5 MB.";
        }
        if (!FORMAT_DIIJINKAN.contains(ekstensiFile(file.getOriginalFilename()))) {
            return "Ekstensi file tidak sesuai ketentuan. Unggah file berformat PDF, JPG, atau PNG.";
        }
        return null;
    }

    private ItemChecklist buatItem(PersyaratanDokumen persyaratan, List<BerkasLampiran> lampiran) {
        BerkasLampiran berkas = lampiran.stream()
                .filter(item -> item.getIdPersyaratan().equals(persyaratan.getIdPersyaratan()))
                .filter(item -> "Terunggah".equals(item.getStatusItem()))
                .max(Comparator.comparing(BerkasLampiran::getTanggalUnggah,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);
        if (berkas == null) {
            return new ItemChecklist(persyaratan.getIdPersyaratan(), null, persyaratan.getNamaPersyaratan(),
                    null, null, "Belum diunggah", false, null, null, null, null);
        }
        return new ItemChecklist(persyaratan.getIdPersyaratan(), berkas.getIdLampiran(), persyaratan.getNamaPersyaratan(),
                berkas.getNamaFile(), berkas.getUkuranKB(), "Terunggah", true, berkas.getStatusValidasi(),
                null, null, null);
    }

    private void hapusLampiranAktif(Integer idPengajuan, Integer idPersyaratan) {
        berkasLampiranRepository.findByIdPengajuan(idPengajuan).stream()
                .filter(lampiran -> lampiran.getIdPersyaratan().equals(idPersyaratan))
                .filter(lampiran -> "Terunggah".equals(lampiran.getStatusItem()))
                .forEach(lampiran -> {
                    berkasLampiranRepository.delete(lampiran);
                    hapusBerkas(Path.of(lampiran.getPathFile()));
                });
    }

    private void hapusBerkas(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
        }
    }

    private String ekstensiFile(String namaFile) {
        if (namaFile == null || !namaFile.contains(".")) {
            return "";
        }
        return namaFile.substring(namaFile.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private Pengajuan ambilPengajuan(Integer idPengajuan) {
        return pengajuanRepository.findById(idPengajuan)
                .orElseThrow(() -> new DataTidakDitemukanException("Pengajuan tidak ditemukan"));
    }

    private Pengajuan ambilPengajuanDetail(Integer idPengajuan) {
        return pengajuanRepository.getDetailPengajuan(idPengajuan)
                .orElseThrow(() -> new DataTidakDitemukanException("Pengajuan tidak ditemukan"));
    }
}
