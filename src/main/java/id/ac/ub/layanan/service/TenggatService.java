package id.ac.ub.layanan.service;

import id.ac.ub.layanan.entity.Pengajuan;
import id.ac.ub.layanan.exception.DataTidakDitemukanException;
import id.ac.ub.layanan.repository.PengajuanRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenggatService {

    public static final String TENGGAT_TIDAK_VALID = "Tanggal tenggat tidak valid";
    public static final String TENGGAT_MENDESAK = "Tenggat mendesak";
    public static final String TENGGAT_TERSIMPAN = "Tenggat tersimpan";

    private static final int BATAS_HARI_MENDESAK = 3;

    private final PengajuanRepository pengajuanRepository;

    public TenggatService(PengajuanRepository pengajuanRepository) {
        this.pengajuanRepository = pengajuanRepository;
    }

    @Transactional
    public String prosesTenggat(Integer idPengajuan, LocalDate tanggalTenggat) {
        Pengajuan pengajuan = ambilPengajuan(idPengajuan);
        LocalDate tanggalPengajuan = pengajuan.getTanggalPengajuan().toLocalDate();

        boolean isValid = validasiTanggalTenggat(tanggalTenggat, tanggalPengajuan);
        if (!isValid) {
            return TENGGAT_TIDAK_VALID;
        }

        int sisaHariKerja = hitungSisaHariKerja(tanggalPengajuan, tanggalTenggat);
        if (sisaHariKerja < BATAS_HARI_MENDESAK) {
            return TENGGAT_MENDESAK;
        }

        pengajuan.simpanTenggat(tanggalTenggat);
        pengajuan.updateStatusPengajuan("Menunggu Verifikasi");
        pengajuanRepository.save(pengajuan);
        return TENGGAT_TERSIMPAN;
    }

    public boolean validasiTanggalTenggat(LocalDate tanggalTenggat, LocalDate tanggalPengajuan) {
        return !tanggalTenggat.isBefore(tanggalPengajuan);
    }

    public int hitungSisaHariKerja(LocalDate tanggalPengajuan, LocalDate tanggalTenggat) {
        int sisaHariKerja = 0;
        for (LocalDate hari = tanggalPengajuan; !hari.isAfter(tanggalTenggat); hari = hari.plusDays(1)) {
            DayOfWeek hariKe = hari.getDayOfWeek();
            if (hariKe != DayOfWeek.SATURDAY && hariKe != DayOfWeek.SUNDAY) {
                sisaHariKerja++;
            }
        }
        return sisaHariKerja;
    }

    @Transactional
    public boolean konfirmasiTenggat(Integer idPengajuan, LocalDate tanggalTenggat) {
        Pengajuan pengajuan = ambilPengajuan(idPengajuan);
        if (!validasiTanggalTenggat(tanggalTenggat, pengajuan.getTanggalPengajuan().toLocalDate())) {
            return false;
        }
        pengajuan.simpanTenggat(tanggalTenggat);
        pengajuan.updateStatusPengajuan("Menunggu Verifikasi");
        pengajuanRepository.save(pengajuan);
        return true;
    }

    @Transactional(readOnly = true)
    public LocalDate getTanggalPengajuan(Integer idPengajuan) {
        return ambilPengajuan(idPengajuan).getTanggalPengajuan().toLocalDate();
    }

    @Transactional(readOnly = true)
    public Pengajuan getDataPengajuan(Integer idPengajuan) {
        return pengajuanRepository.getDetailPengajuan(idPengajuan)
                .orElseThrow(() -> new DataTidakDitemukanException("Pengajuan tidak ditemukan"));
    }

    @Transactional(readOnly = true)
    public boolean isPemilikPengajuan(Integer idPengajuan, Integer idPengguna) {
        return pengajuanRepository.existsByIdPengajuanAndPenggunaIdPengguna(idPengajuan, idPengguna);
    }

    private Pengajuan ambilPengajuan(Integer idPengajuan) {
        return pengajuanRepository.findById(idPengajuan)
                .orElseThrow(() -> new DataTidakDitemukanException("Pengajuan tidak ditemukan"));
    }
}
