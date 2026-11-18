package id.ac.ub.layanan;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LayananKemahasiswaanApplication {

    public static void main(String[] args) {
        // NFR-SRS-07: satu zona waktu (WIB, GMT+7) untuk seluruh perhitungan tanggal dan tenggat
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
        SpringApplication.run(LayananKemahasiswaanApplication.class, args);
    }
}
