package id.ac.ub.layanan.controller;

import id.ac.ub.layanan.dto.HasilAksi;
import id.ac.ub.layanan.dto.HasilLogin;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.security.AuthInterceptor;
import id.ac.ub.layanan.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String bukaHalamanLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String submitLogin(@RequestParam String username, @RequestParam String password,
                              HttpServletRequest request, Model model) {
        HasilLogin hasil = authService.prosesLogin(username, password);
        if (!hasil.berhasil()) {
            model.addAttribute("pesanError", hasil.pesan());
            return "login";
        }

        HttpSession lama = request.getSession(false);
        if (lama != null) {
            lama.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(AuthInterceptor.ATTR_ID, hasil.idPengguna());
        session.setAttribute(AuthInterceptor.ATTR_TOKEN, hasil.sessionToken());
        session.setAttribute("nama", hasil.nama());
        session.setAttribute("inisial", inisial(hasil.nama()));
        session.setAttribute("peran", hasil.peran());
        return redirectKeDashboard(hasil.peran());
    }

    private static String inisial(String nama) {
        String[] kata = nama.trim().split("\\s+");
        return (kata[0].substring(0, 1) + (kata.length > 1 ? kata[1].substring(0, 1) : "")).toUpperCase();
    }

    private String redirectKeDashboard(String peran) {
        return switch (peran) {
            case "Mahasiswa" -> "redirect:/pengajuan";
            case "Staf Kemahasiswaan" -> "redirect:/antrean";
            default -> "redirect:/legalisasi";
        };
    }

    @PostMapping("/logout")
    public String prosesLogout(@RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun,
                               HttpServletRequest request, RedirectAttributes redirect) {
        HasilAksi hasil = authService.prosesLogout(akun.getIdPengguna());
        if (hasil.berhasil()) {
            request.getSession().invalidate();
            return "redirect:/login?keluar";
        }
        redirect.addFlashAttribute("pesanError", hasil.pesan());
        return redirectKeDashboard(akun.getPeran());
    }
}
