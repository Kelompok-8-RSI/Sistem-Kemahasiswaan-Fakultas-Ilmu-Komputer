package id.ac.ub.layanan.security;

import id.ac.ub.layanan.entity.AkunPengguna;
import id.ac.ub.layanan.repository.AkunPenggunaRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATTR_ID = "idPengguna";
    public static final String ATTR_TOKEN = "sessionToken";
    public static final String ATTR_AKUN = "akunAktif";

    private final AkunPenggunaRepository akunRepository;

    public AuthInterceptor(AkunPenggunaRepository akunRepository) {
        this.akunRepository = akunRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        Integer id = session == null ? null : (Integer) session.getAttribute(ATTR_ID);
        String token = session == null ? null : (String) session.getAttribute(ATTR_TOKEN);
        Optional<AkunPengguna> akun = token == null ? Optional.empty()
                : akunRepository.findBySessionToken(token).filter(a -> a.getIdPengguna().equals(id));

        if (akun.isEmpty()) {
            boolean sesiBerakhir = request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid();
            response.sendRedirect(request.getContextPath() + (sesiBerakhir ? "/login?sesi" : "/login"));
            return false;
        }

        if (handler instanceof HandlerMethod hm) {
            RequiredRole syarat = hm.getMethodAnnotation(RequiredRole.class);
            if (syarat == null) {
                syarat = hm.getBeanType().getAnnotation(RequiredRole.class);
            }
            if (syarat != null && !Arrays.asList(syarat.value()).contains(akun.get().getPeran())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Peran tidak memiliki akses");
                return false;
            }
        }
        request.setAttribute(ATTR_AKUN, akun.get());
        return true;
    }
}
