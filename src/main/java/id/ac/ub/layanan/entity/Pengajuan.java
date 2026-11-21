package id.ac.ub.layanan.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import id.ac.ub.layanan.config.AppConfig;

@Entity
@Table(name = "Pengajuan")
public class Pengajuan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPengajuan;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPengguna", nullable = false)
    private AkunPengguna pengguna;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idJenisDokumen", nullable = false)
    private JenisDokumen jenisDokumen;
    @Column(nullable = false, updatable = false)
    private LocalDateTime tanggalPengajuan;
    private LocalDate tanggalTenggat;
    private String status = "Draft";

    public Pengajuan() { }

    @PrePersist
    void saatDibuat() {
        if (tanggalPengajuan == null) {
            tanggalPengajuan = LocalDateTime.now(AppConfig.ZONA_WIB);
        }
    }

    public void simpanTenggat(LocalDate tanggalTenggat) {
        this.tanggalTenggat = tanggalTenggat;
    }

    public void updateStatusPengajuan(String statusBaru) {
        this.status = statusBaru;
    }

    public Integer getIdPengajuan() { return idPengajuan; }
    public void setIdPengajuan(Integer idPengajuan) { this.idPengajuan = idPengajuan; }
    public AkunPengguna getPengguna() { return pengguna; }
    public void setPengguna(AkunPengguna pengguna) { this.pengguna = pengguna; }
    public JenisDokumen getJenisDokumen() { return jenisDokumen; }
    public void setJenisDokumen(JenisDokumen jenisDokumen) { this.jenisDokumen = jenisDokumen; }
    public LocalDateTime getTanggalPengajuan() { return tanggalPengajuan; }
    public void setTanggalPengajuan(LocalDateTime tanggalPengajuan) { this.tanggalPengajuan = tanggalPengajuan; }
    public LocalDate getTanggalTenggat() { return tanggalTenggat; }
    public void setTanggalTenggat(LocalDate tanggalTenggat) { this.tanggalTenggat = tanggalTenggat; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
