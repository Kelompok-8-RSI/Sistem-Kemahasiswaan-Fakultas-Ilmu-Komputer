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

    // UI-01: HalamanLogin.bukaHalamanLogin()
    @GetMapping("/login")
    public String bukaHalamanLogin() {
        return "login";
    }

    // UI-01: HalamanLogin.submitLogin() -> PC-01
    @PostMapping("/login")
    public String submitLogin(@RequestParam String username, @RequestParam String password,
                              HttpServletRequest request, Model model) {
        HasilLogin hasil = authService.prosesLogin(username, password);
        if (!hasil.berhasil()) {
            model.addAttribute("pesanError", hasil.pesan());
            return "login";
        }

        // sesi lama dibuang agar ID sesi baru dibuat setelah login (mencegah session fixation)
        HttpSession lama = request.getSession(false);
        if (lama != null) {
            lama.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(AuthInterceptor.ATTR_ID, hasil.idPengguna());
        session.setAttribute(AuthInterceptor.ATTR_TOKEN, hasil.sessionToken());
        session.setAttribute("nama", hasil.nama());
        session.setAttribute("inisial", inisial(hasil.nama())); // avatar pada sidebar
        session.setAttribute("peran", hasil.peran());
        return redirectKeDashboard(hasil.peran());
    }

    // "Sal Priadi" -> "SP"
    private static String inisial(String nama) {
        String[] kata = nama.trim().split("\\s+");
        return (kata[0].substring(0, 1) + (kata.length > 1 ? kata[1].substring(0, 1) : "")).toUpperCase();
    }

    // HalamanLogin.redirectKeDashboard(): dashboard sesuai peran
    private String redirectKeDashboard(String peran) {
        return switch (peran) {
            case "Mahasiswa" -> "redirect:/pengajuan"; // UI-11 Pengajuan Saya (UC-11)
            case "Staf Kemahasiswaan" -> "redirect:/antrean";
            default -> "redirect:/legalisasi";
        };
    }

    // UI-12: Dialog Konfirmasi Keluar "Ya, Keluar" -> PC-45
    @PostMapping("/logout")
    public String prosesLogout(@RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun,
                               HttpServletRequest request, RedirectAttributes redirect) {
        HasilAksi hasil = authService.prosesLogout(akun.getIdPengguna());
        if (hasil.berhasil()) {
            request.getSession().invalidate();
            return "redirect:/login?keluar"; // UI-01c: "Anda berhasil keluar dari akun."
        }
        // gagal: sesi tetap aktif dan pesan error ditampilkan di dashboard
        redirect.addFlashAttribute("pesanError", hasil.pesan());
        return redirectKeDashboard(akun.getPeran());
    }
}
