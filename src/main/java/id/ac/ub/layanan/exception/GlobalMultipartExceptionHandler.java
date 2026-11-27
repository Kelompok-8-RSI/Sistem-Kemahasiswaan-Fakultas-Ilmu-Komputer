package id.ac.ub.layanan.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalMultipartExceptionHandler {

    private static final Pattern POLA_REVISI = Pattern.compile(".*/pengajuan/(\\d+)/revisi(?:/.*)?$");
    private static final Pattern POLA_LAMPIRAN = Pattern.compile(".*/pengajuan/(\\d+)/lampiran(?:/.*)?$");

    @ExceptionHandler({MaxUploadSizeExceededException.class, MultipartException.class})
    public String tanganiUkuranBerkasMelebihiBatas(MultipartException ex,
                                                   HttpServletRequest request,
                                                   RedirectAttributes redirect) {
        String uri = request.getRequestURI();
        Matcher matcherRevisi = POLA_REVISI.matcher(uri);
        if (matcherRevisi.matches()) {
            redirect.addFlashAttribute("pesanErrorLampiran",
                    "Ukuran file melebihi batas maksimal 5 MB. Unggah ulang dokumen revisi maksimal 5 MB.");
            return "redirect:/pengajuan/" + matcherRevisi.group(1) + "/revisi";
        }
        Matcher matcherLampiran = POLA_LAMPIRAN.matcher(uri);
        if (matcherLampiran.matches()) {
            redirect.addFlashAttribute("pesanErrorLampiran",
                    "Ukuran file melebihi batas maksimal 5 MB. Pilih file berukuran maksimal 5 MB.");
            return "redirect:/pengajuan/" + matcherLampiran.group(1) + "/tenggat";
        }
        redirect.addFlashAttribute("pesanError",
                "Ukuran berkas yang diunggah melebihi batas maksimal 5 MB.");
        return "redirect:/pengajuan";
    }
}
