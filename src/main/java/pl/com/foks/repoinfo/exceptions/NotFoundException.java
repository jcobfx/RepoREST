package pl.com.foks.repoinfo.exceptions;

import org.springframework.http.HttpStatus;

public abstract class NotFoundException extends RuntimeException {
    private final HttpStatus status;

    public NotFoundException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
