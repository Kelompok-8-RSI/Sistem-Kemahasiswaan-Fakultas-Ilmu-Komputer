package id.ac.ub.layanan.controller;

import id.ac.ub.layanan.dto.HasilAksi;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.security.AuthInterceptor;
import id.ac.ub.layanan.security.RequiredRole;
import id.ac.ub.layanan.service.TenggatService;
import id.ac.ub.layanan.service.UnggahPersyaratanService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredRole("Mahasiswa")
@RequestMapping("/pengajuan/{idPengajuan}/lampiran")
public class UnggahPersyaratanController {

    private final UnggahPersyaratanService unggahPersyaratanService;
    private final TenggatService tenggatService;

    public UnggahPersyaratanController(UnggahPersyaratanService unggahPersyaratanService,
                                       TenggatService tenggatService) {
        this.unggahPersyaratanService = unggahPersyaratanService;
        this.tenggatService = tenggatService;
    }

    @PostMapping
    public String requestUnggahLampiran(@PathVariable Integer idPengajuan,
                                        @RequestParam Integer idPersyaratan,
                                        @RequestParam MultipartFile file,
                                        @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun,
                                        RedirectAttributes redirect) {
        pastikanPemilik(idPengajuan, akun);
        HasilAksi hasil = unggahPersyaratanService.requestUnggahLampiran(idPengajuan, idPersyaratan, file);
        redirect.addFlashAttribute(hasil.berhasil() ? "pesanSuksesLampiran" : "pesanErrorLampiran", hasil.pesan());
        return "redirect:/pengajuan/" + idPengajuan + "/tenggat";
    }

    @PostMapping("/{idLampiran}/hapus")
    public String hapusLampiran(@PathVariable Integer idPengajuan, @PathVariable Integer idLampiran,
                                @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun,
                                RedirectAttributes redirect) {
        pastikanPemilik(idPengajuan, akun);
        HasilAksi hasil = unggahPersyaratanService.hapusLampiran(idPengajuan, idLampiran);
        redirect.addFlashAttribute(hasil.berhasil() ? "pesanSuksesLampiran" : "pesanErrorLampiran", hasil.pesan());
        return "redirect:/pengajuan/" + idPengajuan + "/tenggat";
    }

    private void pastikanPemilik(Integer idPengajuan, AkunPengguna akun) {
        if (!tenggatService.isPemilikPengajuan(idPengajuan, akun.getIdPengguna())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pengajuan bukan milik Anda");
        }
    }
}
