package pl.com.foks.repoinfo.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(HttpStatus status, String message) {
        super(status, message);
    }
}
