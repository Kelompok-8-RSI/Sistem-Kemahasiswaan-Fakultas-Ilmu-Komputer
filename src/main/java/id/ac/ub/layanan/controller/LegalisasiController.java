package id.ac.ub.layanan.controller;

import id.ac.ub.layanan.dto.DetailLegalisasi;
import id.ac.ub.layanan.dto.HasilAksi;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.security.AuthInterceptor;
import id.ac.ub.layanan.security.RequiredRole;
import id.ac.ub.layanan.service.LegalisasiService;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredRole("Pejabat")
@RequestMapping("/legalisasi")
public class LegalisasiController {

    private final LegalisasiService legalisasiService;

    public LegalisasiController(LegalisasiService legalisasiService) {
        this.legalisasiService = legalisasiService;
    }

    @GetMapping
    public String bukaAntrean(Model model) {
        model.addAttribute("antrean", legalisasiService.requestAntreanLegalisasi());
        return "legalisasi-antrean";
    }

    @GetMapping("/{idPengajuan}")
    public String bukaDetail(@PathVariable Integer idPengajuan, Model model) {
        return tampilkanDetail(idPengajuan, model);
    }

    @PostMapping("/{idPengajuan}/keputusan")
    public String prosesKeputusan(@PathVariable Integer idPengajuan,
                                  @RequestParam String keputusan,
                                  @RequestParam(required = false) String catatanAlasan,
                                  @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun,
                                  Model model, RedirectAttributes redirect) {
        HasilAksi hasil = legalisasiService.prosesKeputusan(
                idPengajuan, keputusan, catatanAlasan, akun.getIdPengguna());
        if (hasil.berhasil()) {
            redirect.addFlashAttribute("konfirmasi", hasil.pesan());
            return "redirect:/legalisasi";
        }
        model.addAttribute("pesanError", hasil.pesan());
        return tampilkanDetail(idPengajuan, model);
    }

    private String tampilkanDetail(Integer idPengajuan, Model model) {
        Optional<DetailLegalisasi> detail = legalisasiService.requestDetailLegalisasi(idPengajuan);
        if (detail.isEmpty()) {
            model.addAttribute("pesanError", "Data pengajuan legalisasi tidak ditemukan");
        } else {
            model.addAttribute("detail", detail.get());
        }
        return "legalisasi-detail";
    }
}
