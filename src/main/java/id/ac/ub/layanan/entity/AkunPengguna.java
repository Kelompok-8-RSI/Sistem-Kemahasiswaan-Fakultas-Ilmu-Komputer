package id.ac.ub.layanan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "AkunPengguna")
public class AkunPengguna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPengguna;
    private String username;
    private String password;
    private String nama;
    private String nimNip;
    private String email;
    private String noTelepon;
    private String peran;
    private String sessionToken;

    protected AkunPengguna() { }

    public Integer getIdPengguna() { return idPengguna; }
    public void setIdPengguna(Integer idPengguna) { this.idPengguna = idPengguna; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getNimNip() { return nimNip; }
    public void setNimNip(String nimNip) { this.nimNip = nimNip; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNoTelepon() { return noTelepon; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon; }
    public String getPeran() { return peran; }
    public void setPeran(String peran) { this.peran = peran; }
    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }
}
