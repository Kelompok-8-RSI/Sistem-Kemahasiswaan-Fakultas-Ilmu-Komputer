package id.ac.ub.layanan.controller;

import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.entity.JenisDokumen;
import id.ac.ub.layanan.security.AuthInterceptor;
import id.ac.ub.layanan.security.RequiredRole;
import id.ac.ub.layanan.service.PemilihanDokumenService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredRole("Mahasiswa")
@RequestMapping("/pengajuan/baru")
public class PemilihanDokumenController {

    private final PemilihanDokumenService pemilihanDokumenService;

    public PemilihanDokumenController(PemilihanDokumenService pemilihanDokumenService) {
        this.pemilihanDokumenService = pemilihanDokumenService;
    }

    @GetMapping
    public String requestKategoriKebutuhan(@RequestParam(required = false) String kategori,
                                          @RequestParam(required = false) Integer jenis,
                                          Model model) {
        List<String> listKategori = pemilihanDokumenService.requestKategoriKebutuhan();
        model.addAttribute("listKategori", listKategori);
        model.addAttribute("kategoriTerpilih", kategori);

        if (kategori != null && !kategori.isBlank()) {
            List<JenisDokumen> listJenisDokumen = pemilihanDokumenService.requestSubJenisDokumen(kategori);
            model.addAttribute("listJenisDokumen", listJenisDokumen);
            if (listJenisDokumen.isEmpty()) {
                model.addAttribute("pesanTidakTersedia",
                        "Jenis dokumen tidak tersedia. Pilih kategori Lainnya atau hubungi WA Center Kemahasiswaan.");
            }
        }

        if (jenis != null) {
            pemilihanDokumenService.cariJenisDokumen(jenis).ifPresent(j -> {
                model.addAttribute("jenisTerpilih", j);
                model.addAttribute("listPersyaratan", pemilihanDokumenService.cariPersyaratan(j.getIdJenisDokumen()));
            });
        }

        return "pilih-dokumen";
    }

    @PostMapping
    public String submitJenisDokumen(@RequestParam(name = "jenisTerpilih", required = false) Integer jenisTerpilih,
                                     @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun,
                                     Model model) {
        if (jenisTerpilih == null) {
            model.addAttribute("pesanError", "Silakan pilih jenis dokumen terlebih dahulu");
            return requestKategoriKebutuhan(null, null, model);
        }

        Integer idPengajuan = pemilihanDokumenService.submitJenisDokumen(jenisTerpilih, akun);
        if (idPengajuan != null && idPengajuan > 0) {
            return "redirect:/pengajuan/" + idPengajuan + "/tenggat";
        }

        model.addAttribute("pesanError", "Gagal membuat pengajuan baru");
        return requestKategoriKebutuhan(null, jenisTerpilih, model);
    }
}
