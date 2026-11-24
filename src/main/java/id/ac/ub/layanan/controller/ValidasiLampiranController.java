package id.ac.ub.layanan.controller;

import id.ac.ub.layanan.dto.BarisAntrean;
import id.ac.ub.layanan.dto.HasilValidasiItem;
import id.ac.ub.layanan.dto.ItemChecklist;
import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.entity.Pengajuan;
import id.ac.ub.layanan.security.AuthInterceptor;
import id.ac.ub.layanan.security.RequiredRole;
import id.ac.ub.layanan.service.AntreanService;
import id.ac.ub.layanan.service.ValidasiLampiranService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredRole("Staf Kemahasiswaan")
public class ValidasiLampiranController {

    private final ValidasiLampiranService validasiService;
    private final AntreanService antreanService;

    public ValidasiLampiranController(ValidasiLampiranService validasiService, AntreanService antreanService) {
        this.validasiService = validasiService;
        this.antreanService = antreanService;
    }

    @GetMapping("/pengajuan/{idPengajuan}/checklist")
    public String bukaChecklist(@PathVariable Integer idPengajuan, Model model) {
        Pengajuan pengajuan = validasiService.getDataPengajuan(idPengajuan);
        List<ItemChecklist> checklist = validasiService.requestChecklist(idPengajuan);

        model.addAttribute("pengajuan", pengajuan);
        model.addAttribute("checklist", checklist);
        model.addAttribute("hasilValidasi", validasiService.hitungHasilValidasi(checklist));
        if (pengajuan.getTanggalTenggat() != null) {
            model.addAttribute("prioritas", new BarisAntrean(pengajuan,
                    antreanService.hitungSisaWaktu(List.of(pengajuan)).get(0),
                    antreanService.beriLabelPrioritas(List.of(pengajuan)).get(0)));
        }
        return "checklist";
    }

    @PostMapping("/lampiran/{idLampiran}/validasi")
    @ResponseBody
    public ResponseEntity<HasilValidasiItem> validasiItem(
            @PathVariable Integer idLampiran,
            @RequestParam Integer idPengajuan,
            @RequestParam String statusItem,
            @RequestParam(defaultValue = "") String catatan,
            @RequestAttribute(AuthInterceptor.ATTR_AKUN) AkunPengguna akun) {
        HasilValidasiItem hasil = validasiService.prosesValidasiItem(
                idPengajuan, idLampiran, statusItem, catatan, akun.getIdPengguna());
        return ResponseEntity.status(hasil.berhasil() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(hasil);
    }
}
