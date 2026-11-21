package id.ac.ub.layanan.repository;

import id.ac.ub.layanan.entity.JenisDokumen;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JenisDokumenRepository extends JpaRepository<JenisDokumen, Integer> {

    @Query("select distinct j.kategori from JenisDokumen j order by j.kategori")
    List<String> getKategoriList();

    @Query("select j from JenisDokumen j where j.kategori = :kategori order by j.namaJenis")
    List<JenisDokumen> getJenisDokumenByKategori(@Param("kategori") String kategori);
}
