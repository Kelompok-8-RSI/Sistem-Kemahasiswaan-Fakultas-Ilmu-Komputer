package id.ac.ub.layanan.controller;

import id.ac.ub.layanan.dto.LangkahRiwayat;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.entity.BerkasLampiran;
import id.ac.ub.layanan.entity.Pengajuan;
import id.ac.ub.layanan.exception.DataTidakDitemukanException;
import id.ac.ub.layanan.security.AuthInterceptor;
import id.ac.ub.layanan.security.RequiredRole;
import id.ac.ub.layanan.service.RiwayatService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredRole("Mahasiswa")
@RequestMapping("/pengajuan")
public class RiwayatController {

    private final RiwayatService riwayatService;
    private final Path folderUnggah;

    public RiwayatController(RiwayatService riwayatService,
                             @Value("${app.upload-dir}") String uploadDir) {
        this.riwayatService = riwayatService;
        this.folderUnggah = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @GetMapping
    public String requestDaftarPengajuan(@RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun,
                                         Model model) {
        List<Pengajuan> daftarPengajuan = riwayatService.requestDaftarPengajuan(akun.getIdPengguna());
        long jumlahRevisi = daftarPengajuan.stream().filter(p -> "Revisi".equals(p.getStatus())).count();
        long jumlahSelesai = daftarPengajuan.stream().filter(p -> "Selesai".equals(p.getStatus())).count();
        long jumlahDalamProses = daftarPengajuan.size() - jumlahRevisi - jumlahSelesai;

        model.addAttribute("daftarPengajuan", daftarPengajuan);
        model.addAttribute("jumlahSemua", daftarPengajuan.size());
        model.addAttribute("jumlahRevisi", jumlahRevisi);
        model.addAttribute("jumlahDalamProses", jumlahDalamProses);
        model.addAttribute("jumlahSelesai", jumlahSelesai);
        if (jumlahRevisi > 0) {
            model.addAttribute("ringkasanRevisi", riwayatService.getRingkasanRevisi(daftarPengajuan));
        }
        return "pengajuan-saya";
    }

    @GetMapping("/{idPengajuan}")
    public String requestRiwayatStatus(@PathVariable Integer idPengajuan,
                                       @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun,
                                       Model model) {
        Pengajuan pengajuan = riwayatService.getDetailPengajuanMilikMahasiswa(idPengajuan, akun.getIdPengguna());
        if ("Draft".equals(pengajuan.getStatus())) {
            return "redirect:/pengajuan/" + idPengajuan + "/tenggat";
        }
        List<LangkahRiwayat> riwayat = riwayatService.requestRiwayatStatus(idPengajuan);
        if (riwayat.isEmpty()) {
            model.addAttribute("pesanRiwayatKosong", "Data riwayat status tidak ditemukan");
        }
        model.addAttribute("pengajuan", pengajuan);
        model.addAttribute("riwayat", riwayat);
        model.addAttribute("lampiran", riwayatService.getLampiranPengajuan(pengajuan));
        return "pengajuan-detail";
    }

    @GetMapping("/{idPengajuan}/lampiran/{idLampiran}")
    public ResponseEntity<Resource> bukaLampiran(@PathVariable Integer idPengajuan,
                                                 @PathVariable Integer idLampiran,
                                                 @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun)
            throws IOException {
        BerkasLampiran lampiran = riwayatService.getLampiranMilikMahasiswa(
                idPengajuan, idLampiran, akun.getIdPengguna());
        Path berkas = selesaikanPathBerkas(lampiran.getPathFile());
        if (!berkas.startsWith(folderUnggah) || !Files.isRegularFile(berkas)) {
            throw new DataTidakDitemukanException("Berkas tidak ditemukan di penyimpanan");
        }
        String tipe = Files.probeContentType(berkas);
        String namaAman = lampiran.getNamaFile().replaceAll("[\r\n\"]", "_");
        return ResponseEntity.ok()
                .contentType(tipe != null ? MediaType.parseMediaType(tipe) : MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + namaAman + "\"")
                .header("X-Content-Type-Options", "nosniff")
                .body(new PathResource(berkas));
    }

    private Path selesaikanPathBerkas(String pathFile) {
        Path kandidat = Path.of(pathFile);
        if (kandidat.isAbsolute()) {
            return kandidat.normalize();
        }
        String relatif = pathFile.replaceFirst("^/?uploads/", "");
        return folderUnggah.resolve(relatif).normalize();
    }
}
