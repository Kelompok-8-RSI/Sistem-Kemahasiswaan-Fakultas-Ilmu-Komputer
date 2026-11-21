package id.ac.ub.layanan.service;

import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.entity.JenisDokumen;
import id.ac.ub.layanan.entity.Pengajuan;
import id.ac.ub.layanan.entity.PersyaratanDokumen;
import id.ac.ub.layanan.repository.AkunPenggunaRepository;
import id.ac.ub.layanan.repository.JenisDokumenRepository;
import id.ac.ub.layanan.repository.PengajuanRepository;
import id.ac.ub.layanan.repository.PersyaratanDokumenRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PemilihanDokumenService {

    public static final List<String> DAFTAR_KATEGORI = List.of(
            "Lomba", "Beasiswa", "Organisasi", "Akademik/PKL", "Lainnya"
    );

    private final JenisDokumenRepository jenisDokumenRepository;
    private final PersyaratanDokumenRepository persyaratanDokumenRepository;
    private final PengajuanRepository pengajuanRepository;
    private final AkunPenggunaRepository akunPenggunaRepository;
    private final Clock clock;

    public PemilihanDokumenService(JenisDokumenRepository jenisDokumenRepository,
                                  PersyaratanDokumenRepository persyaratanDokumenRepository,
                                  PengajuanRepository pengajuanRepository,
                                  AkunPenggunaRepository akunPenggunaRepository,
                                  Clock clock) {
        this.jenisDokumenRepository = jenisDokumenRepository;
        this.persyaratanDokumenRepository = persyaratanDokumenRepository;
        this.pengajuanRepository = pengajuanRepository;
        this.akunPenggunaRepository = akunPenggunaRepository;
        this.clock = clock;
    }

    public List<String> requestKategoriKebutuhan() {
        return DAFTAR_KATEGORI;
    }

    public List<JenisDokumen> requestSubJenisDokumen(String kategoriTerpilih) {
        if (kategoriTerpilih == null || kategoriTerpilih.isBlank()) {
            return List.of();
        }
        return jenisDokumenRepository.getJenisDokumenByKategori(kategoriTerpilih);
    }

    public Optional<JenisDokumen> cariJenisDokumen(Integer idJenisDokumen) {
        if (idJenisDokumen == null) {
            return Optional.empty();
        }
        return jenisDokumenRepository.findById(idJenisDokumen);
    }

    public List<PersyaratanDokumen> cariPersyaratan(Integer idJenisDokumen) {
        if (idJenisDokumen == null) {
            return List.of();
        }
        return persyaratanDokumenRepository.findByIdJenisDokumenAndWajibTrue(idJenisDokumen);
    }

    @Transactional
    public Integer submitJenisDokumen(Integer idJenisDokumen, AkunPengguna akun) {
        if (idJenisDokumen == null || akun == null) {
            return null;
        }
        JenisDokumen jenis = jenisDokumenRepository.findById(idJenisDokumen).orElse(null);
        if (jenis == null) {
            return null;
        }
        AkunPengguna pengguna = akunPenggunaRepository.getReferenceById(akun.getIdPengguna());
        Pengajuan pengajuan = new Pengajuan();
        pengajuan.setPengguna(pengguna);
        pengajuan.setJenisDokumen(jenis);
        pengajuan.setStatus("Draft");
        pengajuan.setTanggalPengajuan(LocalDateTime.now(clock));
        Pengajuan tersimpan = pengajuanRepository.save(pengajuan);
        return tersimpan.getIdPengajuan();
    }
}
