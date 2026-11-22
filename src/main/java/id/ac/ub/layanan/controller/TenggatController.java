package id.ac.ub.layanan.controller;

import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.dto.HasilAksi;
import id.ac.ub.layanan.security.AuthInterceptor;
import id.ac.ub.layanan.security.RequiredRole;
import id.ac.ub.layanan.service.TenggatService;
import id.ac.ub.layanan.service.UnggahPersyaratanService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredRole("Mahasiswa")
@RequestMapping("/pengajuan/{idPengajuan}/tenggat")
public class TenggatController {

    private final TenggatService tenggatService;
    private final UnggahPersyaratanService unggahPersyaratanService;

    public TenggatController(TenggatService tenggatService, UnggahPersyaratanService unggahPersyaratanService) {
        this.tenggatService = tenggatService;
        this.unggahPersyaratanService = unggahPersyaratanService;
    }

    @GetMapping
    public String bukaFormTenggat(@PathVariable Integer idPengajuan,
                                  @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun, Model model) {
        pastikanPemilik(idPengajuan, akun);
        return tampilkanForm(idPengajuan, model);
    }

    @PostMapping
    public String submitPengajuan(@PathVariable Integer idPengajuan,
                                  @RequestParam(required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalTenggat,
                                  @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun,
                                  Model model, RedirectAttributes redirect) {
        pastikanPemilik(idPengajuan, akun);
        if (tanggalTenggat == null) {
            model.addAttribute("pesanError", "Tanggal tenggat wajib diisi");
            return tampilkanForm(idPengajuan, model);
        }
        if (!unggahPersyaratanService.cekKelengkapanItemWajib(idPengajuan)) {
            model.addAttribute("pesanError", "Masih ada persyaratan wajib yang belum diunggah");
            model.addAttribute("tanggalTenggat", tanggalTenggat);
            model.addAttribute("tandaiPersyaratanKosong", true);
            return tampilkanForm(idPengajuan, model);
        }

        String hasil = tenggatService.prosesTenggat(idPengajuan, tanggalTenggat);
        switch (hasil) {
            case TenggatService.TENGGAT_TERSIMPAN -> {
                return kirimPengajuan(idPengajuan, akun, model, redirect);
            }
            case TenggatService.TENGGAT_MENDESAK -> model.addAttribute("tenggatMendesak", true);
            default -> model.addAttribute("pesanError",
                    "Tanggal tenggat tidak boleh lebih awal dari tanggal pengajuan");
        }
        model.addAttribute("tanggalTenggat", tanggalTenggat);
        return tampilkanForm(idPengajuan, model);
    }

    @PostMapping("/konfirmasi")
    public String lanjutkanPengajuan(@PathVariable Integer idPengajuan,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalTenggat,
                                     @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun,
                                     Model model, RedirectAttributes redirect) {
        pastikanPemilik(idPengajuan, akun);
        if (!unggahPersyaratanService.cekKelengkapanItemWajib(idPengajuan)) {
            model.addAttribute("pesanError", "Masih ada persyaratan wajib yang belum diunggah");
            model.addAttribute("tanggalTenggat", tanggalTenggat);
            model.addAttribute("tandaiPersyaratanKosong", true);
            return tampilkanForm(idPengajuan, model);
        }
        if (tenggatService.konfirmasiTenggat(idPengajuan, tanggalTenggat)) {
            return kirimPengajuan(idPengajuan, akun, model, redirect);
        }
        model.addAttribute("pesanError", "Tenggat gagal disimpan. Silakan coba lagi.");
        return tampilkanForm(idPengajuan, model);
    }

    private String tampilkanForm(Integer idPengajuan, Model model) {
        model.addAttribute("idPengajuan", idPengajuan);
        model.addAttribute("tanggalPengajuan", tenggatService.getTanggalPengajuan(idPengajuan));
        model.addAttribute("pengajuan", tenggatService.getDataPengajuan(idPengajuan));
        model.addAttribute("persyaratan", unggahPersyaratanService.requestDaftarPersyaratan(idPengajuan));
        model.addAttribute("persyaratanLengkap", unggahPersyaratanService.cekKelengkapanItemWajib(idPengajuan));
        return "form-tenggat";
    }

    private String kirimPengajuan(Integer idPengajuan, AkunPengguna akun, Model model,
                                  RedirectAttributes redirect) {
        HasilAksi hasil = unggahPersyaratanService.submitPengajuan(idPengajuan, akun);
        if (hasil.berhasil()) {
            redirect.addFlashAttribute("terkirim", true);
            return "redirect:/pengajuan/" + idPengajuan + "/tenggat";
        }
        model.addAttribute("pesanError", hasil.pesan());
        model.addAttribute("tandaiPersyaratanKosong", true);
        return tampilkanForm(idPengajuan, model);
    }

    private void pastikanPemilik(Integer idPengajuan, AkunPengguna akun) {
        if (!tenggatService.isPemilikPengajuan(idPengajuan, akun.getIdPengguna())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pengajuan bukan milik Anda");
        }
    }
}
