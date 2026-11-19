package id.ac.ub.layanan.controller;

import id.ac.ub.layanan.entity.BerkasLampiran;
import id.ac.ub.layanan.exception.DataTidakDitemukanException;
import id.ac.ub.layanan.repository.BerkasLampiranRepository;
import id.ac.ub.layanan.security.RequiredRole;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// tombol "Buka Lampiran" (UI-07) dan "Buka" (UI-09): membaca berkas dari Storage (uploads/)
@Controller
@RequiredRole({"Staf Kemahasiswaan", "Pejabat"})
public class BerkasController {

    private final BerkasLampiranRepository berkasLampiranRepository;
    private final Path folderUnggah;

    public BerkasController(BerkasLampiranRepository berkasLampiranRepository,
                            @Value("${app.upload-dir}") String uploadDir) {
        this.berkasLampiranRepository = berkasLampiranRepository;
        this.folderUnggah = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @GetMapping("/lampiran/{idLampiran}/berkas")
    public ResponseEntity<Resource> bukaBerkas(@PathVariable Integer idLampiran) throws IOException {
        BerkasLampiran lampiran = berkasLampiranRepository.findById(idLampiran)
                .orElseThrow(() -> new DataTidakDitemukanException("Lampiran tidak ditemukan"));

        // pathFile tersimpan sebagai /uploads/<idPengajuan>/<nama>_v<versi>.<ext>
        String relatif = lampiran.getPathFile().replaceFirst("^/?uploads/", "");
        Path berkas = folderUnggah.resolve(relatif).normalize();
        if (!berkas.startsWith(folderUnggah) || !Files.isRegularFile(berkas)) { // cegah path traversal
            throw new DataTidakDitemukanException("Berkas tidak ditemukan di penyimpanan");
        }

        String tipe = Files.probeContentType(berkas);
        return ResponseEntity.ok()
                .contentType(tipe != null ? MediaType.parseMediaType(tipe) : MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + lampiran.getNamaFile() + "\"")
                .header("X-Content-Type-Options", "nosniff")
                .body(new PathResource(berkas));
    }
}
