package id.ac.ub.layanan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "JenisDokumen")
public class JenisDokumen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idJenisDokumen;
    private String kategori;
    private String namaJenis;
    private String deskripsi;
    private Integer estimasiHariKerja = 3;
    private boolean perluLegalisasi = false;

    protected JenisDokumen() { }

    public Integer getIdJenisDokumen() { return idJenisDokumen; }
    public void setIdJenisDokumen(Integer idJenisDokumen) { this.idJenisDokumen = idJenisDokumen; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public String getNamaJenis() { return namaJenis; }
    public void setNamaJenis(String namaJenis) { this.namaJenis = namaJenis; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public Integer getEstimasiHariKerja() { return estimasiHariKerja; }
    public void setEstimasiHariKerja(Integer estimasiHariKerja) { this.estimasiHariKerja = estimasiHariKerja; }
    public boolean getPerluLegalisasi() { return perluLegalisasi; }
    public void setPerluLegalisasi(boolean perluLegalisasi) { this.perluLegalisasi = perluLegalisasi; }
}
