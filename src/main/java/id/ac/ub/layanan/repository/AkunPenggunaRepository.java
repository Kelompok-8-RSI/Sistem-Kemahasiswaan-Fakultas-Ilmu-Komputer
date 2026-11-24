package id.ac.ub.layanan.repository;

import id.ac.ub.layanan.entity.AkunPengguna;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AkunPenggunaRepository extends JpaRepository<AkunPengguna, Integer> {

    Optional<AkunPengguna> findByUsername(String username);

    Optional<AkunPengguna> findBySessionToken(String sessionToken);

    boolean existsByEmailAndIdPenggunaNot(String email, Integer idPengguna);
}
