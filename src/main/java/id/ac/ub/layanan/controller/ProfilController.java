package id.ac.ub.layanan.controller;

import id.ac.ub.layanan.dto.DataProfil;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.security.AuthInterceptor;
import id.ac.ub.layanan.security.RequiredRole;
import id.ac.ub.layanan.service.ProfilService;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredRole({"Mahasiswa", "Staf Kemahasiswaan", "Pejabat"})
@RequestMapping("/profil")
public class ProfilController {

    private final ProfilService profilService;

    public ProfilController(ProfilService profilService) {
        this.profilService = profilService;
    }

    @GetMapping
    public String bukaMenuProfil(@RequestParam(required = false) String ubah,
                                 @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun, Model model) {
        AkunPengguna dataProfil = profilService.requestDataProfil(akun.getIdPengguna());
        model.addAttribute("modeUbah", ubah != null);
        model.addAttribute("dataProfilBaru", new DataProfil(dataProfil.getEmail(), dataProfil.getNoTelepon()));
        model.addAttribute("kesalahan", Map.of());
        return tampilkanProfil(dataProfil, model);
    }

    @PostMapping
    public String simpanDataProfil(@RequestParam(defaultValue = "") String email,
                                   @RequestParam(defaultValue = "") String noTelepon,
                                   @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun,
                                   Model model, RedirectAttributes redirect) {
        DataProfil dataProfilBaru = new DataProfil(email.trim(), noTelepon.trim());
        if (profilService.updateDataProfil(akun.getIdPengguna(), dataProfilBaru)) {
            redirect.addFlashAttribute("pesanSukses", "Perubahan data profil berhasil disimpan.");
            return "redirect:/profil";
        }
        model.addAttribute("modeUbah", true);
        model.addAttribute("dataProfilBaru", dataProfilBaru);
        model.addAttribute("kesalahan", profilService.periksaDataProfil(akun.getIdPengguna(), dataProfilBaru));
        model.addAttribute("pesanError", "Perubahan data profil gagal disimpan. Periksa kembali data yang ditandai.");
        return tampilkanProfil(profilService.requestDataProfil(akun.getIdPengguna()), model);
    }

    private String tampilkanProfil(AkunPengguna dataProfil, Model model) {
        model.addAttribute("profil", dataProfil);
        return "profil";
    }
}
