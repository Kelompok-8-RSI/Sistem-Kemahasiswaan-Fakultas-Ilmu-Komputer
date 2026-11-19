package id.ac.ub.layanan.repository;

import id.ac.ub.layanan.entity.AntreanVerifikasi;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AntreanVerifikasiRepository extends JpaRepository<AntreanVerifikasi, Integer> {

    Optional<AntreanVerifikasi> findByIdPengajuan(Integer idPengajuan);
}
