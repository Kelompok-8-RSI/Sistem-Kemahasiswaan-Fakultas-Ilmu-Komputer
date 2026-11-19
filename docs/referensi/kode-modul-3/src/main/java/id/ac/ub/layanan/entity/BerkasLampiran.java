package id.ac.ub.layanan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import id.ac.ub.layanan.config.AppConfig;

@Entity
@Table(name = "BerkasLampiran")
public class BerkasLampiran {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLampiran;
    private Integer idPengajuan;
    private Integer idPersyaratan;
    private String namaFile;
    private String pathFile;
    private Integer ukuranKB;
    private String statusItem = "Terunggah";
    private Integer versi = 1;
    private String statusValidasi = "Belum Diperiksa";
    private String catatanValidasi;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idStafValidator")
    private AkunPengguna stafValidator;
    private LocalDateTime waktuValidasi;
    @Column(nullable = false, updatable = false)
    private LocalDateTime tanggalUnggah;

    protected BerkasLampiran() { }

    @PrePersist
    void saatDibuat() {
        if (tanggalUnggah == null) {
            tanggalUnggah = LocalDateTime.now(AppConfig.ZONA_WIB);
        }
    }

    // BerkasLampiran.updateStatusValidasi (PC-27)
    public void updateStatusValidasi(String statusValidasi, String catatan,
                                     AkunPengguna staf, LocalDateTime waktuValidasi) {
        this.statusValidasi = statusValidasi;
        this.catatanValidasi = catatan;
        this.stafValidator = staf;
        this.waktuValidasi = waktuValidasi;
    }

    public Integer getIdLampiran() { return idLampiran; }
    public void setIdLampiran(Integer idLampiran) { this.idLampiran = idLampiran; }
    public Integer getIdPengajuan() { return idPengajuan; }
    public void setIdPengajuan(Integer idPengajuan) { this.idPengajuan = idPengajuan; }
    public Integer getIdPersyaratan() { return idPersyaratan; }
    public void setIdPersyaratan(Integer idPersyaratan) { this.idPersyaratan = idPersyaratan; }
    public String getNamaFile() { return namaFile; }
    public void setNamaFile(String namaFile) { this.namaFile = namaFile; }
    public String getPathFile() { return pathFile; }
    public void setPathFile(String pathFile) { this.pathFile = pathFile; }
    public Integer getUkuranKB() { return ukuranKB; }
    public void setUkuranKB(Integer ukuranKB) { this.ukuranKB = ukuranKB; }
    public String getStatusItem() { return statusItem; }
    public void setStatusItem(String statusItem) { this.statusItem = statusItem; }
    public Integer getVersi() { return versi; }
    public void setVersi(Integer versi) { this.versi = versi; }
    public String getStatusValidasi() { return statusValidasi; }
    public void setStatusValidasi(String statusValidasi) { this.statusValidasi = statusValidasi; }
    public String getCatatanValidasi() { return catatanValidasi; }
    public void setCatatanValidasi(String catatanValidasi) { this.catatanValidasi = catatanValidasi; }
    public AkunPengguna getStafValidator() { return stafValidator; }
    public void setStafValidator(AkunPengguna stafValidator) { this.stafValidator = stafValidator; }
    public LocalDateTime getWaktuValidasi() { return waktuValidasi; }
    public void setWaktuValidasi(LocalDateTime waktuValidasi) { this.waktuValidasi = waktuValidasi; }
    public LocalDateTime getTanggalUnggah() { return tanggalUnggah; }
    public void setTanggalUnggah(LocalDateTime tanggalUnggah) { this.tanggalUnggah = tanggalUnggah; }
}
