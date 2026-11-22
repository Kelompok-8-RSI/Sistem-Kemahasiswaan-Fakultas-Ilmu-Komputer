package id.ac.ub.layanan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AntreanVerifikasi")
public class AntreanVerifikasi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAntrean;
    private Integer idPengajuan;
    private String statusAntrean = "Menunggu";
    private LocalDateTime waktuMasuk;

    public AntreanVerifikasi() { }

    public Integer getIdAntrean() { return idAntrean; }
    public void setIdAntrean(Integer idAntrean) { this.idAntrean = idAntrean; }
    public Integer getIdPengajuan() { return idPengajuan; }
    public void setIdPengajuan(Integer idPengajuan) { this.idPengajuan = idPengajuan; }
    public String getStatusAntrean() { return statusAntrean; }
    public void setStatusAntrean(String statusAntrean) { this.statusAntrean = statusAntrean; }
    public LocalDateTime getWaktuMasuk() { return waktuMasuk; }
    public void setWaktuMasuk(LocalDateTime waktuMasuk) { this.waktuMasuk = waktuMasuk; }
}
