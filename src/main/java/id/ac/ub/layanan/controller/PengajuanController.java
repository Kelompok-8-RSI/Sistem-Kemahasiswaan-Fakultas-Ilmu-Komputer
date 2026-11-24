package id.ac.ub.layanan.controller;

import id.ac.ub.layanan.dto.HasilAksi;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.security.AuthInterceptor;
import id.ac.ub.layanan.security.RequiredRole;
import id.ac.ub.layanan.service.PengajuanService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredRole("Staf Kemahasiswaan")
public class PengajuanController {

    private final PengajuanService pengajuanService;

    public PengajuanController(PengajuanService pengajuanService) {
        this.pengajuanService = pengajuanService;
    }

    @PostMapping("/pengajuan/{idPengajuan}/status")
    public String prosesPembaruanStatus(@PathVariable Integer idPengajuan,
                                        @RequestParam(defaultValue = "") String statusBaru,
                                        @RequestParam(defaultValue = "") String catatan,
                                        @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun,
                                        RedirectAttributes redirect) {
        HasilAksi hasil = pengajuanService.prosesPembaruanStatus(
                idPengajuan, statusBaru, catatan, akun.getIdPengguna());
        if (hasil.berhasil()) {
            redirect.addFlashAttribute("pesanSuksesStatus", hasil.pesan());
        } else {
            redirect.addFlashAttribute("statusBaruDipilih", statusBaru);
            redirect.addFlashAttribute("catatanStatus", catatan);
            if ("Revisi".equals(statusBaru) && catatan.isBlank()) {
                redirect.addFlashAttribute("pesanErrorCatatanStatus", "Catatan perbaikan wajib diisi");
            } else {
                redirect.addFlashAttribute("pesanErrorStatus", hasil.pesan());
            }
        }
        return "redirect:/pengajuan/" + idPengajuan + "/checklist";
    }
}
