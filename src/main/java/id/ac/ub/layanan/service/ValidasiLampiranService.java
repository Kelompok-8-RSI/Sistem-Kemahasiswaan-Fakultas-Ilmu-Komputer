package id.ac.ub.layanan.service;

import id.ac.ub.layanan.dto.HasilValidasiItem;
import id.ac.ub.layanan.dto.ItemChecklist;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.entity.BerkasLampiran;
import id.ac.ub.layanan.entity.Pengajuan;
import id.ac.ub.layanan.entity.PersyaratanDokumen;
import id.ac.ub.layanan.exception.DataTidakDitemukanException;
import id.ac.ub.layanan.repository.AkunPenggunaRepository;
import id.ac.ub.layanan.repository.BerkasLampiranRepository;
import id.ac.ub.layanan.repository.PengajuanRepository;
import id.ac.ub.layanan.repository.PersyaratanDokumenRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ValidasiLampiranService {

    private static final DateTimeFormatter FORMAT_WAKTU = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PengajuanRepository pengajuanRepository;
    private final PersyaratanDokumenRepository persyaratanRepository;
    private final BerkasLampiranRepository berkasLampiranRepository;
    private final AkunPenggunaRepository akunRepository;
    private final Clock clock;

    public ValidasiLampiranService(PengajuanRepository pengajuanRepository,
                                   PersyaratanDokumenRepository persyaratanRepository,
                                   BerkasLampiranRepository berkasLampiranRepository,
                                   AkunPenggunaRepository akunRepository, Clock clock) {
        this.pengajuanRepository = pengajuanRepository;
        this.persyaratanRepository = persyaratanRepository;
        this.berkasLampiranRepository = berkasLampiranRepository;
        this.akunRepository = akunRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public Pengajuan getDataPengajuan(Integer idPengajuan) {
        return pengajuanRepository.getDetailPengajuan(idPengajuan)
                .orElseThrow(() -> new DataTidakDitemukanException("Pengajuan tidak ditemukan"));
    }

    @Transactional(readOnly = true)
    public List<ItemChecklist> requestChecklist(Integer idPengajuan) {
        Pengajuan dataPengajuan = pengajuanRepository.getDetailPengajuan(idPengajuan).orElse(null);
        if (dataPengajuan == null) {
            return List.of();
        }
        List<PersyaratanDokumen> listPersyaratanWajib = persyaratanRepository
                .findByIdJenisDokumenAndWajibTrue(dataPengajuan.getJenisDokumen().getIdJenisDokumen());
        List<BerkasLampiran> listLampiran = berkasLampiranRepository.findByIdPengajuan(idPengajuan);
        return susunChecklist(listPersyaratanWajib, listLampiran);
    }

    public List<ItemChecklist> susunChecklist(List<PersyaratanDokumen> listPersyaratanWajib,
                                              List<BerkasLampiran> listLampiran) {
        List<ItemChecklist> listChecklist = new ArrayList<>();
        for (PersyaratanDokumen persyaratan : listPersyaratanWajib) {
            BerkasLampiran lampiran = listLampiran.stream()
                    .filter(l -> l.getIdPersyaratan().equals(persyaratan.getIdPersyaratan())
                            && "Terunggah".equals(l.getStatusItem()))
                    .findFirst().orElse(null);

            if (lampiran == null) {
                listChecklist.add(new ItemChecklist(persyaratan.getIdPersyaratan(), null,
                        persyaratan.getNamaPersyaratan(), null, null, "Belum diunggah", false,
                        "Tidak Lengkap", null, null, null));
            } else {
                AkunPengguna staf = lampiran.getStafValidator();
                listChecklist.add(new ItemChecklist(persyaratan.getIdPersyaratan(), lampiran.getIdLampiran(),
                        persyaratan.getNamaPersyaratan(), lampiran.getNamaFile(), lampiran.getUkuranKB(),
                        "Sudah diunggah", true,
                        lampiran.getStatusValidasi(),
                        lampiran.getCatatanValidasi(),
                        staf != null ? staf.getNama() : null, lampiran.getWaktuValidasi()));
            }
        }
        return listChecklist;
    }

    @Transactional
    public HasilValidasiItem prosesValidasiItem(Integer idPengajuan,
                                                Integer idLampiran, String statusItem,
                                                String catatan, Integer idStaf) {
        BerkasLampiran lampiran = berkasLampiranRepository.findByIdPengajuan(idPengajuan).stream()
                .filter(l -> l.getIdLampiran().equals(idLampiran))
                .findFirst().orElse(null);
        boolean sudahDiunggah = lampiran != null && "Terunggah".equals(lampiran.getStatusItem());

        boolean isValid = lampiran != null && validasiInputItem(statusItem, catatan, sudahDiunggah);
        if (!isValid) {
            return new HasilValidasiItem(false, "Status item atau catatan tidak sesuai ketentuan",
                    null, null, null);
        }

        LocalDateTime timestamp = LocalDateTime.now(clock);
        AkunPengguna staf = akunRepository.getReferenceById(idStaf);
        lampiran.updateStatusValidasi(statusItem, catatan, staf, timestamp);
        berkasLampiranRepository.save(lampiran);

        String hasilValidasi = hitungHasilValidasi(requestChecklist(idPengajuan));
        String keterangan = staf.getNama() + ", " + timestamp.format(FORMAT_WAKTU);
        return new HasilValidasiItem(true, "Hasil validasi tersimpan", statusItem, hasilValidasi, keterangan);
    }

    public boolean validasiInputItem(String statusItem, String catatan, boolean sudahDiunggah) {
        if (!"Lengkap".equals(statusItem) && !"Tidak Lengkap".equals(statusItem)) {
            return false;
        }
        if ("Lengkap".equals(statusItem) && !sudahDiunggah) {
            return false;
        }
        if ("Tidak Lengkap".equals(statusItem) && (catatan == null || catatan.isBlank())) {
            return false;
        }
        return true;
    }

    public String hitungHasilValidasi(List<ItemChecklist> listChecklist) {
        int jumlahBelum = 0;
        int jumlahTidak = 0;
        for (ItemChecklist item : listChecklist) {
            if ("Belum Diperiksa".equals(item.statusValidasi())) {
                jumlahBelum++;
            } else if ("Tidak Lengkap".equals(item.statusValidasi())) {
                jumlahTidak++;
            }
        }
        if (jumlahBelum > 0) return "Belum Selesai";
        if (jumlahTidak > 0) return "Tidak Lengkap";
        return "Lengkap";
    }
}
