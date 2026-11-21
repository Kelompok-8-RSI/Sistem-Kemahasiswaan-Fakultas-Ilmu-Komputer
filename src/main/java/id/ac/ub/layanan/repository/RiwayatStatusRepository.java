package id.ac.ub.layanan.repository;

import id.ac.ub.layanan.entity.RiwayatStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiwayatStatusRepository extends JpaRepository<RiwayatStatus, Integer> {

    List<RiwayatStatus> findByIdPengajuanOrderByWaktu(Integer idPengajuan);

    default boolean catatLog(Integer idPengajuan, String status, String catatan,
                             Integer idPelaku, LocalDateTime waktu) {
        save(new RiwayatStatus(idPengajuan, idPelaku, status, catatan, waktu));
        return true;
    }
}
