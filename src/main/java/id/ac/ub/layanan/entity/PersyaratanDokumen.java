package id.ac.ub.layanan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PersyaratanDokumen")
public class PersyaratanDokumen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPersyaratan;
    private Integer idJenisDokumen;
    private String namaPersyaratan;
    private boolean wajib = true;

    protected PersyaratanDokumen() { }

    public Integer getIdPersyaratan() { return idPersyaratan; }
    public void setIdPersyaratan(Integer idPersyaratan) { this.idPersyaratan = idPersyaratan; }
    public Integer getIdJenisDokumen() { return idJenisDokumen; }
    public void setIdJenisDokumen(Integer idJenisDokumen) { this.idJenisDokumen = idJenisDokumen; }
    public String getNamaPersyaratan() { return namaPersyaratan; }
    public void setNamaPersyaratan(String namaPersyaratan) { this.namaPersyaratan = namaPersyaratan; }
    public boolean getWajib() { return wajib; }
    public void setWajib(boolean wajib) { this.wajib = wajib; }
}
