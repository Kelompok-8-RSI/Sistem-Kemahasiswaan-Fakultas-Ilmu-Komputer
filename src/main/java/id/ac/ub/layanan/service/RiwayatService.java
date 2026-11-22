package id.ac.ub.layanan.service;

import id.ac.ub.layanan.dto.ItemChecklist;
import id.ac.ub.layanan.dto.LangkahRiwayat;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.entity.BerkasLampiran;
import id.ac.ub.layanan.entity.Pengajuan;
import id.ac.ub.layanan.entity.PersyaratanDokumen;
import id.ac.ub.layanan.entity.RiwayatStatus;
import id.ac.ub.layanan.exception.DataTidakDitemukanException;
import id.ac.ub.layanan.repository.AkunPenggunaRepository;
import id.ac.ub.layanan.repository.BerkasLampiranRepository;
import id.ac.ub.layanan.repository.PengajuanRepository;
import id.ac.ub.layanan.repository.PersyaratanDokumenRepository;
import id.ac.ub.layanan.repository.RiwayatStatusRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RiwayatService {

    private final PengajuanRepository pengajuanRepository;
    private final RiwayatStatusRepository riwayatStatusRepository;
    private final AkunPenggunaRepository akunPenggunaRepository;
    private final PersyaratanDokumenRepository persyaratanDokumenRepository;
    private final BerkasLampiranRepository berkasLampiranRepository;

    public RiwayatService(PengajuanRepository pengajuanRepository,
                          RiwayatStatusRepository riwayatStatusRepository,
                          AkunPenggunaRepository akunPenggunaRepository,
                          PersyaratanDokumenRepository persyaratanDokumenRepository,
                          BerkasLampiranRepository berkasLampiranRepository) {
        this.pengajuanRepository = pengajuanRepository;
        this.riwayatStatusRepository = riwayatStatusRepository;
        this.akunPenggunaRepository = akunPenggunaRepository;
        this.persyaratanDokumenRepository = persyaratanDokumenRepository;
        this.berkasLampiranRepository = berkasLampiranRepository;
    }

    @Transactional(readOnly = true)
    public List<Pengajuan> requestDaftarPengajuan(Integer idMahasiswa) {
        return pengajuanRepository.getPengajuanByMahasiswa(idMahasiswa);
    }

    @Transactional(readOnly = true)
    public Pengajuan getDetailPengajuanMilikMahasiswa(Integer idPengajuan, Integer idMahasiswa) {
        Pengajuan pengajuan = pengajuanRepository.getDetailPengajuan(idPengajuan)
                .orElseThrow(() -> new DataTidakDitemukanException("Pengajuan tidak ditemukan"));
        if (!pengajuan.getPengguna().getIdPengguna().equals(idMahasiswa)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pengajuan bukan milik Anda");
        }
        return pengajuan;
    }

    @Transactional(readOnly = true)
    public List<LangkahRiwayat> requestRiwayatStatus(Integer idPengajuan) {
        List<RiwayatStatus> listLogRiwayat = riwayatStatusRepository.findByIdPengajuanOrderByWaktu(idPengajuan);
        if (listLogRiwayat.isEmpty()) {
            return List.of();
        }
        List<RiwayatStatus> listRiwayatTerurut = urutkanKronologis(listLogRiwayat);
        Map<Integer, AkunPengguna> pelaku = akunPenggunaRepository
                .findAllById(listRiwayatTerurut.stream().map(RiwayatStatus::getIdPelaku).distinct().toList())
                .stream()
                .collect(Collectors.toMap(AkunPengguna::getIdPengguna, Function.identity()));
        List<LangkahRiwayat> hasil = new ArrayList<>();
        for (RiwayatStatus r : listRiwayatTerurut) {
            AkunPengguna a = pelaku.get(r.getIdPelaku());
            hasil.add(new LangkahRiwayat(r.getStatus(), r.getCatatan(), r.getWaktu(),
                    a != null ? a.getNama() : "-", a != null ? a.getPeran() : ""));
        }
        Collections.reverse(hasil);
        return hasil;
    }

    public List<RiwayatStatus> urutkanKronologis(List<RiwayatStatus> listLogRiwayat) {
        if (listLogRiwayat == null || listLogRiwayat.isEmpty()) {
            return List.of();
        }
        return listLogRiwayat.stream()
                .sorted(Comparator.comparing(RiwayatStatus::getWaktu)
                        .thenComparing(RiwayatStatus::getIdRiwayat, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ItemChecklist> getLampiranPengajuan(Pengajuan pengajuan) {
        Map<Integer, String> namaPersyaratan = persyaratanDokumenRepository
                .findByIdJenisDokumenAndWajibTrue(pengajuan.getJenisDokumen().getIdJenisDokumen())
                .stream()
                .collect(Collectors.toMap(PersyaratanDokumen::getIdPersyaratan, PersyaratanDokumen::getNamaPersyaratan));
        return berkasLampiranRepository.findByIdPengajuan(pengajuan.getIdPengajuan()).stream()
                .filter(l -> "Terunggah".equals(l.getStatusItem()))
                .sorted(Comparator.comparing(BerkasLampiran::getIdPersyaratan)
                        .thenComparing(BerkasLampiran::getVersi, Comparator.reverseOrder()))
                .map(l -> new ItemChecklist(
                        l.getIdPersyaratan(),
                        l.getIdLampiran(),
                        namaPersyaratan.getOrDefault(l.getIdPersyaratan(), "Lampiran"),
                        l.getNamaFile(),
                        l.getUkuranKB(),
                        l.getStatusItem(),
                        true,
                        l.getStatusValidasi(),
                        l.getCatatanValidasi(),
                        null,
                        l.getWaktuValidasi()))
                .toList();
    }

    @Transactional(readOnly = true)
    public BerkasLampiran getLampiranMilikMahasiswa(Integer idPengajuan, Integer idLampiran, Integer idMahasiswa) {
        getDetailPengajuanMilikMahasiswa(idPengajuan, idMahasiswa);
        BerkasLampiran lampiran = berkasLampiranRepository.findById(idLampiran)
                .orElseThrow(() -> new DataTidakDitemukanException("Lampiran tidak ditemukan"));
        if (!lampiran.getIdPengajuan().equals(idPengajuan)) {
            throw new DataTidakDitemukanException("Lampiran tidak ditemukan");
        }
        return lampiran;
    }

    @Transactional(readOnly = true)
    public String getRingkasanRevisi(List<Pengajuan> daftarPengajuan) {
        Pengajuan revisiPertama = daftarPengajuan.stream()
                .filter(p -> "Revisi".equals(p.getStatus()))
                .findFirst()
                .orElse(null);
        if (revisiPertama == null) {
            return null;
        }
        String pengembali = requestRiwayatStatus(revisiPertama.getIdPengajuan()).stream()
                .filter(l -> "Revisi".equals(l.status()))
                .map(l -> "Pejabat".equals(l.peranPelaku()) ? "Pejabat Fakultas" : l.peranPelaku())
                .filter(p -> p != null && !p.isBlank())
                .findFirst()
                .orElse("Staf Kemahasiswaan");
        return "Pengajuan #" + revisiPertama.getIdPengajuan() + " dikembalikan " + pengembali
                + " dengan catatan. Perbaiki lampiran agar pengajuan diproses kembali.";
    }
}
