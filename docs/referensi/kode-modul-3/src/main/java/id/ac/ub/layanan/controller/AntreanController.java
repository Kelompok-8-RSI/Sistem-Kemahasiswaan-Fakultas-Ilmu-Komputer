package id.ac.ub.layanan.controller;

import id.ac.ub.layanan.dto.HasilAntrean;
import id.ac.ub.layanan.security.RequiredRole;
import id.ac.ub.layanan.service.AntreanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredRole("Staf Kemahasiswaan")
public class AntreanController {

    private final AntreanService antreanService;

    public AntreanController(AntreanService antreanService) {
        this.antreanService = antreanService;
    }

    // UI-06: HalamanAntreanPengajuan -> PC-21
    @GetMapping("/antrean")
    public String bukaHalamanAntrean(Model model) {
        HasilAntrean hasil = antreanService.requestAntreanPengajuan();
        model.addAttribute("antrean", hasil.baris());
        model.addAttribute("pesanKosong", hasil.pesan());
        return "antrean";
    }
}
