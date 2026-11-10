package id.ac.ub.layanan.repository;

import id.ac.ub.layanan.entity.AkunPengguna;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AkunPenggunaRepository extends JpaRepository<AkunPengguna, Integer> {

    // AkunPengguna.getAkunByUsername (PC-01)
    Optional<AkunPengguna> findByUsername(String username);

    // dipakai AuthInterceptor untuk memeriksa sessionToken pada setiap permintaan
    Optional<AkunPengguna> findBySessionToken(String sessionToken);
}
