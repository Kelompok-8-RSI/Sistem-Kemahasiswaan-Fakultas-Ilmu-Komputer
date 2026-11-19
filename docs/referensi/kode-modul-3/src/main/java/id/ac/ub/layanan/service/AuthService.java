package id.ac.ub.layanan.service;

import id.ac.ub.layanan.dto.HasilAksi;
import id.ac.ub.layanan.dto.HasilLogin;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.repository.AkunPenggunaRepository;
import java.security.SecureRandom;
import java.util.HexFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AkunPenggunaRepository akunRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(AkunPenggunaRepository akunRepository, PasswordEncoder passwordEncoder) {
        this.akunRepository = akunRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // PC-01: Proses Login Pengguna
    @Transactional
    public HasilLogin prosesLogin(String username, String password) {
        AkunPengguna dataAkun = akunRepository.findByUsername(username).orElse(null);
        boolean isValid = validasiKredensial(password, dataAkun);

        if (!isValid) {
            return new HasilLogin(false, "Username atau password salah", null, null, null, null);
        }

        String peran = tentukanPeran(dataAkun);
        String sessionToken = buatSessionToken(dataAkun.getIdPengguna());
        // peran dipakai AuthController untuk redirect ke dashboard sesuai peran
        return new HasilLogin(true, null, dataAkun.getIdPengguna(), dataAkun.getNama(), peran, sessionToken);
    }

    // PC-02: Validasi Kredensial Login
    public boolean validasiKredensial(String password, AkunPengguna dataAkun) {
        if (dataAkun == null || password == null) {
            return false;
        }
        // kolom password pada AkunPengguna menyimpan hash bcrypt
        return passwordEncoder.matches(password, dataAkun.getPassword());
    }

    // PC-03: Menentukan Peran Pengguna
    public String tentukanPeran(AkunPengguna dataAkun) {
        return switch (dataAkun.getPeran()) {
            case "Mahasiswa" -> "Mahasiswa";
            case "Staf Kemahasiswaan" -> "Staf Kemahasiswaan";
            default -> "Pejabat";
        };
    }

    // PC-45: Proses Logout Pengguna (UC-12)
    @Transactional
    public HasilAksi prosesLogout(Integer idPengguna) {
        boolean isDestroyed = hancurkanSessionToken(idPengguna);
        if (isDestroyed) {
            return new HasilAksi(true, null); // AuthController mengarahkan ke halaman login
        }
        return new HasilAksi(false, "Gagal mengakhiri sesi akun. Silakan coba lagi.");
    }

    // AkunPengguna.buatSessionToken: token acak 64 karakter heksadesimal (VARCHAR(64))
    private String buatSessionToken(Integer idPengguna) {
        byte[] acak = new byte[32];
        secureRandom.nextBytes(acak);
        String sessionToken = HexFormat.of().formatHex(acak);
        AkunPengguna akun = akunRepository.getReferenceById(idPengguna);
        akun.setSessionToken(sessionToken);
        akunRepository.save(akun);
        return sessionToken;
    }

    // AkunPengguna.hancurkanSessionToken (NFR-SRS-16): token dihapus di sisi server
    private boolean hancurkanSessionToken(Integer idPengguna) {
        return akunRepository.findById(idPengguna).map(akun -> {
            akun.setSessionToken(null);
            akunRepository.save(akun);
            return true;
        }).orElse(false);
    }
}
