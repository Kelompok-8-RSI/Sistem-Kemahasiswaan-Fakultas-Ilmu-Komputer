package id.ac.ub.layanan.repository;

import id.ac.ub.layanan.entity.Pengajuan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PengajuanRepository extends JpaRepository<Pengajuan, Integer> {

    @Query("""
           select p from Pengajuan p
             join fetch p.pengguna
             join fetch p.jenisDokumen
            where p.idPengajuan = :id
           """)
    Optional<Pengajuan> getDetailPengajuan(@Param("id") Integer idPengajuan);

    @Query("""
           select p from Pengajuan p
             join fetch p.pengguna
             join fetch p.jenisDokumen
             join AntreanVerifikasi a on a.idPengajuan = p.idPengajuan
            where a.statusAntrean in ('Menunggu', 'Diproses')
              and p.tanggalTenggat is not null
           """)
    List<Pengajuan> getDaftarAntrean();

    @Query("""
           select p from Pengajuan p
             join fetch p.pengguna
             join fetch p.jenisDokumen
            where p.status = :status
            order by p.tanggalTenggat
           """)
    List<Pengajuan> getDaftarByStatus(@Param("status") String status);

    @Query("""
           select p from Pengajuan p
             join fetch p.pengguna
             join fetch p.jenisDokumen
            where p.pengguna.idPengguna = :id
            order by p.tanggalPengajuan desc, p.idPengajuan desc
           """)
    List<Pengajuan> getPengajuanByMahasiswa(@Param("id") Integer idMahasiswa);

    boolean existsByIdPengajuanAndPenggunaIdPengguna(Integer idPengajuan, Integer idPengguna);
}
