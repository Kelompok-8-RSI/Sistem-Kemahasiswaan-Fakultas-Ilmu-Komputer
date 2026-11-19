package id.ac.ub.layanan.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// membatasi akses controller atau handler berdasarkan peran: Mahasiswa, Staf Kemahasiswaan, Pejabat
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredRole {
    String[] value();
}
