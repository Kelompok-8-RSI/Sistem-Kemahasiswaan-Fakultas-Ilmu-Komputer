package id.ac.ub.layanan.repository;

import id.ac.ub.layanan.entity.BerkasLampiran;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BerkasLampiranRepository extends JpaRepository<BerkasLampiran, Integer> {

    // BerkasLampiran.getLampiranByPengajuan (PC-25, PC-27, PC-33)
    List<BerkasLampiran> findByIdPengajuan(Integer idPengajuan);
}
