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

    @Transactional
    public HasilLogin prosesLogin(String username, String password) {
        AkunPengguna dataAkun = akunRepository.findByUsername(username).orElse(null);
        boolean isValid = validasiKredensial(password, dataAkun);

        if (!isValid) {
            return new HasilLogin(false, "Username atau password salah", null, null, null, null);
        }

        String peran = tentukanPeran(dataAkun);
        String sessionToken = buatSessionToken(dataAkun.getIdPengguna());
        return new HasilLogin(true, null, dataAkun.getIdPengguna(), dataAkun.getNama(), peran, sessionToken);
    }

    public boolean validasiKredensial(String password, AkunPengguna dataAkun) {
        if (dataAkun == null || password == null) {
            return false;
        }
        return passwordEncoder.matches(password, dataAkun.getPassword());
    }

    public String tentukanPeran(AkunPengguna dataAkun) {
        return switch (dataAkun.getPeran()) {
            case "Mahasiswa" -> "Mahasiswa";
            case "Staf Kemahasiswaan" -> "Staf Kemahasiswaan";
            default -> "Pejabat";
        };
    }

    @Transactional
    public HasilAksi prosesLogout(Integer idPengguna) {
        boolean isDestroyed = hancurkanSessionToken(idPengguna);
        if (isDestroyed) {
            return new HasilAksi(true, null);
        }
        return new HasilAksi(false, "Gagal mengakhiri sesi akun. Silakan coba lagi.");
    }

    private String buatSessionToken(Integer idPengguna) {
        byte[] acak = new byte[32];
        secureRandom.nextBytes(acak);
        String sessionToken = HexFormat.of().formatHex(acak);
        AkunPengguna akun = akunRepository.getReferenceById(idPengguna);
        akun.setSessionToken(sessionToken);
        akunRepository.save(akun);
        return sessionToken;
    }

    private boolean hancurkanSessionToken(Integer idPengguna) {
        return akunRepository.findById(idPengguna).map(akun -> {
            akun.setSessionToken(null);
            akunRepository.save(akun);
            return true;
        }).orElse(false);
    }
}
