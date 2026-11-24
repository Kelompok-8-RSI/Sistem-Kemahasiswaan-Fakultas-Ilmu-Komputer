package id.ac.ub.layanan.service;

import id.ac.ub.layanan.dto.DataProfil;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.exception.DataTidakDitemukanException;
import id.ac.ub.layanan.repository.AkunPenggunaRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfilService {

    private static final Pattern POLA_EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern POLA_TELEPON = Pattern.compile("^0[0-9]{9,14}$");
    private static final int PANJANG_MAKSIMAL_EMAIL = 100;

    private final AkunPenggunaRepository akunRepository;

    public ProfilService(AkunPenggunaRepository akunRepository) {
        this.akunRepository = akunRepository;
    }

    @Transactional(readOnly = true)
    public AkunPengguna requestDataProfil(Integer idPengguna) {
        return akunRepository.findById(idPengguna)
                .orElseThrow(() -> new DataTidakDitemukanException("Akun tidak ditemukan"));
    }

    @Transactional
    public boolean updateDataProfil(Integer idPengguna, DataProfil dataProfilBaru) {
        boolean isValid = validasiDataProfil(idPengguna, dataProfilBaru);
        if (!isValid) {
            return false;
        }
        AkunPengguna akun = requestDataProfil(idPengguna);
        akun.setEmail(dataProfilBaru.email());
        akun.setNoTelepon(dataProfilBaru.noTelepon());
        akunRepository.save(akun);
        return true;
    }

    public boolean validasiDataProfil(Integer idPengguna, DataProfil dataProfilBaru) {
        return periksaDataProfil(idPengguna, dataProfilBaru).isEmpty();
    }

    public Map<String, String> periksaDataProfil(Integer idPengguna, DataProfil dataProfilBaru) {
        Map<String, String> kesalahan = new LinkedHashMap<>();
        String email = dataProfilBaru.email();
        String noTelepon = dataProfilBaru.noTelepon();

        if (email == null || email.isBlank()) {
            kesalahan.put("email", "Email wajib diisi.");
        } else if (email.length() > PANJANG_MAKSIMAL_EMAIL || !POLA_EMAIL.matcher(email).matches()) {
            kesalahan.put("email", "Format email tidak valid, contoh nama@student.ub.ac.id.");
        } else if (akunRepository.existsByEmailAndIdPenggunaNot(email, idPengguna)) {
            kesalahan.put("email", "Email sudah dipakai akun lain.");
        }

        if (noTelepon == null || noTelepon.isBlank()) {
            kesalahan.put("noTelepon", "Nomor telepon wajib diisi.");
        } else if (!POLA_TELEPON.matcher(noTelepon).matches()) {
            kesalahan.put("noTelepon", "Nomor telepon harus 10 sampai 15 digit angka dan diawali 0.");
        }
        return kesalahan;
    }
}
