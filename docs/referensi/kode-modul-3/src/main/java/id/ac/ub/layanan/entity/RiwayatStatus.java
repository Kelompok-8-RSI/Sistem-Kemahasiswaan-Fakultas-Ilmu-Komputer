package id.ac.ub.layanan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "RiwayatStatus")
@Immutable // NFR-SRS-11: log riwayat tidak dapat diubah
public class RiwayatStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRiwayat;
    private Integer idPengajuan;
    private Integer idPelaku;
    private String status;
    private String catatan;
    @Column(name = "`timestamp`") // kolom PDM bernama timestamp
    private LocalDateTime waktu;

    protected RiwayatStatus() { }

    public RiwayatStatus(Integer idPengajuan, Integer idPelaku, String status,
                         String catatan, LocalDateTime waktu) {
        this.idPengajuan = idPengajuan;
        this.idPelaku = idPelaku;
        this.status = status;
        this.catatan = catatan;
        this.waktu = waktu;
    }

    public Integer getIdRiwayat() { return idRiwayat; }
    public Integer getIdPengajuan() { return idPengajuan; }
    public Integer getIdPelaku() { return idPelaku; }
    public String getStatus() { return status; }
    public String getCatatan() { return catatan; }
    public LocalDateTime getWaktu() { return waktu; }
}
