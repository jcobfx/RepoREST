package pl.com.foks.repoinfo.exceptions;

import org.springframework.http.HttpStatus;

public class RepositoriesNotFoundException extends NotFoundException {
    public RepositoriesNotFoundException(HttpStatus status, String message) {
        super(status, message);
    }
}
