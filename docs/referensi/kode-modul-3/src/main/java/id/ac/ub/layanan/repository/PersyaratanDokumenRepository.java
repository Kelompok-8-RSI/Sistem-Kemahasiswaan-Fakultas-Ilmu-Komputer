package id.ac.ub.layanan.repository;

import id.ac.ub.layanan.entity.PersyaratanDokumen;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersyaratanDokumenRepository extends JpaRepository<PersyaratanDokumen, Integer> {

    // PersyaratanDokumen.getPersyaratanByJenisDokumen (PC-25): persyaratan wajib sesuai jenis dokumen
    List<PersyaratanDokumen> findByIdJenisDokumenAndWajibTrue(Integer idJenisDokumen);
}
