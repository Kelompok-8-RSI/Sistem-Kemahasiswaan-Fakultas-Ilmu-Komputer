package id.ac.ub.layanan.repository;

import id.ac.ub.layanan.entity.Pengajuan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PengajuanRepository extends JpaRepository<Pengajuan, Integer> {

    // Pengajuan.getDetailPengajuan (PC-25, PC-33): pengajuan beserta pemohon dan jenis dokumen
    @Query("""
           select p from Pengajuan p
             join fetch p.pengguna
             join fetch p.jenisDokumen
            where p.idPengajuan = :id
           """)
    Optional<Pengajuan> getDetailPengajuan(@Param("id") Integer idPengajuan);

    // Pengajuan.getDaftarAntrean (PC-21): pengajuan yang masih mengantre di AntreanVerifikasi
    @Query("""
           select p from Pengajuan p
             join fetch p.pengguna
             join fetch p.jenisDokumen
             join AntreanVerifikasi a on a.idPengajuan = p.idPengajuan
            where a.statusAntrean in ('Menunggu', 'Diproses')
              and p.tanggalTenggat is not null
           """)
    List<Pengajuan> getDaftarAntrean();

    // antrean akun Pejabat (MR-SR-18): pengajuan berstatus Menunggu Legalisasi Pejabat
    @Query("""
           select p from Pengajuan p
             join fetch p.pengguna
             join fetch p.jenisDokumen
            where p.status = :status
            order by p.tanggalTenggat
           """)
    List<Pengajuan> getDaftarByStatus(@Param("status") String status);

    // pemeriksaan kepemilikan pengajuan oleh Mahasiswa yang sedang login
    boolean existsByIdPengajuanAndPenggunaIdPengguna(Integer idPengajuan, Integer idPengguna);
}
