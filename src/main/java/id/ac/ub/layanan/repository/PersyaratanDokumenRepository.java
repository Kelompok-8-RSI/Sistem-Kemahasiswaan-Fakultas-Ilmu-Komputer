package id.ac.ub.layanan.repository;

import id.ac.ub.layanan.entity.PersyaratanDokumen;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersyaratanDokumenRepository extends JpaRepository<PersyaratanDokumen, Integer> {

    List<PersyaratanDokumen> findByIdJenisDokumenAndWajibTrue(Integer idJenisDokumen);
}
