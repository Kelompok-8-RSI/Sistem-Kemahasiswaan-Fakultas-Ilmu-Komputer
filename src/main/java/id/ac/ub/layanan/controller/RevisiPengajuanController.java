package id.ac.ub.layanan.controller;

import id.ac.ub.layanan.dto.HasilAksi;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.entity.Pengajuan;
import id.ac.ub.layanan.security.AuthInterceptor;
import id.ac.ub.layanan.security.RequiredRole;
import id.ac.ub.layanan.service.RevisiPengajuanService;
import id.ac.ub.layanan.service.RevisiPengajuanService.CatatanRevisi;
import id.ac.ub.layanan.service.RevisiPengajuanService.ItemRevisi;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredRole("Mahasiswa")
@RequestMapping("/pengajuan/{idPengajuan}/revisi")
public class RevisiPengajuanController {

    private final RevisiPengajuanService revisiPengajuanService;

    public RevisiPengajuanController(RevisiPengajuanService revisiPengajuanService) {
        this.revisiPengajuanService = revisiPengajuanService;
    }

    @GetMapping
    public String requestDetailRevisi(@PathVariable Integer idPengajuan,
                                      @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun,
                                      Model model) {
        Pengajuan pengajuan = revisiPengajuanService.getPengajuanRevisi(idPengajuan, akun.getIdPengguna());
        if (!"Revisi".equals(pengajuan.getStatus()) && !model.containsAttribute("terkirim")) {
            return "redirect:/pengajuan/" + idPengajuan;
        }

        CatatanRevisi catatanRevisi = revisiPengajuanService.getCatatanRevisiTerakhir(idPengajuan);
        List<ItemRevisi> items = revisiPengajuanService.getDaftarItemRevisi(idPengajuan);

        int totalWajib = items.size();
        long jumlahSesuai = items.stream().filter(ItemRevisi::sesuai).count();
        boolean semuaSesuai = totalWajib > 0 && jumlahSesuai == totalWajib;
        int persentaseSesuai = totalWajib == 0 ? 100 : (int) (jumlahSesuai * 100 / totalWajib);

        model.addAttribute("pengajuan", pengajuan);
        model.addAttribute("catatanRevisi", catatanRevisi);
        model.addAttribute("items", items);
        model.addAttribute("totalWajib", totalWajib);
        model.addAttribute("jumlahSesuai", jumlahSesuai);
        model.addAttribute("semuaSesuai", semuaSesuai);
        model.addAttribute("persentaseSesuai", persentaseSesuai);

        return "revisi";
    }

    @PostMapping("/lampiran")
    public String unggahLampiranRevisi(@PathVariable Integer idPengajuan,
                                       @RequestParam Integer idPersyaratan,
                                       @RequestParam MultipartFile file,
                                       @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun,
                                       RedirectAttributes redirect) {
        HasilAksi hasil = revisiPengajuanService.gantiLampiran(idPengajuan, idPersyaratan, file, akun.getIdPengguna());
        if (hasil.berhasil()) {
            redirect.addFlashAttribute("pesanSuksesLampiran", hasil.pesan());
        } else {
            redirect.addFlashAttribute("pesanErrorLampiran", hasil.pesan());
        }
        return "redirect:/pengajuan/" + idPengajuan + "/revisi";
    }

    @PostMapping("/submit")
    public String submitRevisi(@PathVariable Integer idPengajuan,
                               @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun,
                               RedirectAttributes redirect) {
        HasilAksi hasil = revisiPengajuanService.submitRevisi(idPengajuan, akun.getIdPengguna());
        if (hasil.berhasil()) {
            redirect.addFlashAttribute("terkirim", true);
        } else {
            redirect.addFlashAttribute("pesanErrorLampiran", hasil.pesan());
        }
        return "redirect:/pengajuan/" + idPengajuan + "/revisi";
    }
}
