package id.ac.ub.layanan.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    public static final ZoneId ZONA_WIB = ZoneId.of("Asia/Jakarta");

    @Bean
    public Clock clock() {
        return Clock.system(ZONA_WIB);
    }

    // NFR-SRS-02: password disimpan sebagai hash bcrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
