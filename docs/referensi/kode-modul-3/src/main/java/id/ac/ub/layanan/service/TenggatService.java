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

    private static final int BATAS_HARI_MENDESAK = 3; // peringatan jika sisa < 3 hari kerja (UC-05)

    private final PengajuanRepository pengajuanRepository;

    public TenggatService(PengajuanRepository pengajuanRepository) {
        this.pengajuanRepository = pengajuanRepository;
    }

    // PC-17: Memproses Tenggat Pengajuan
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
            // Mahasiswa dapat memperbaiki tanggal atau melanjutkan (konfirmasiTenggat)
            return TENGGAT_MENDESAK;
        }

        pengajuan.simpanTenggat(tanggalTenggat);
        pengajuan.updateStatusPengajuan("Menunggu Verifikasi");
        pengajuanRepository.save(pengajuan);
        return TENGGAT_TERSIMPAN;
    }

    // PC-18: Validasi Tanggal Tenggat
    public boolean validasiTanggalTenggat(LocalDate tanggalTenggat, LocalDate tanggalPengajuan) {
        return !tanggalTenggat.isBefore(tanggalPengajuan);
    }

    // PC-19: Menghitung Sisa Hari Kerja (Senin sampai Jumat)
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

    // PC-20: Konfirmasi Tenggat Mendesak
    @Transactional
    public boolean konfirmasiTenggat(Integer idPengajuan, LocalDate tanggalTenggat) {
        Pengajuan pengajuan = ambilPengajuan(idPengajuan);
        // pengaman tambahan: tanggal divalidasi ulang agar tidak melanggar CHECK pada tabel Pengajuan
        if (!validasiTanggalTenggat(tanggalTenggat, pengajuan.getTanggalPengajuan().toLocalDate())) {
            return false;
        }
        pengajuan.simpanTenggat(tanggalTenggat);
        pengajuan.updateStatusPengajuan("Menunggu Verifikasi");
        pengajuanRepository.save(pengajuan);
        return true;
    }

    // dipakai TenggatController untuk membatasi tanggal pada Date Picker (UI-05)
    @Transactional(readOnly = true)
    public LocalDate getTanggalPengajuan(Integer idPengajuan) {
        return ambilPengajuan(idPengajuan).getTanggalPengajuan().toLocalDate();
    }

    // data pengajuan (jenis dokumen, kategori, estimasi, legalisasi) untuk kartu Ringkasan pengajuan
    @Transactional(readOnly = true)
    public Pengajuan getDataPengajuan(Integer idPengajuan) {
        return pengajuanRepository.getDetailPengajuan(idPengajuan)
                .orElseThrow(() -> new DataTidakDitemukanException("Pengajuan tidak ditemukan"));
    }

    // hanya Mahasiswa pemilik pengajuan yang boleh mencantumkan tenggat
    @Transactional(readOnly = true)
    public boolean isPemilikPengajuan(Integer idPengajuan, Integer idPengguna) {
        return pengajuanRepository.existsByIdPengajuanAndPenggunaIdPengguna(idPengajuan, idPengguna);
    }

    private Pengajuan ambilPengajuan(Integer idPengajuan) {
        return pengajuanRepository.findById(idPengajuan)
                .orElseThrow(() -> new DataTidakDitemukanException("Pengajuan tidak ditemukan"));
    }
}
