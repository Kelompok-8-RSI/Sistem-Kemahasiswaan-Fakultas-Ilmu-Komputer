package id.ac.ub.layanan.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DataTidakDitemukanException extends RuntimeException {
    public DataTidakDitemukanException(String pesan) {
        super(pesan);
    }
}
