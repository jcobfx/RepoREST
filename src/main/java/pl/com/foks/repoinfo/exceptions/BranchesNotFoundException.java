package pl.com.foks.repoinfo.exceptions;

import org.springframework.http.HttpStatus;

public class BranchesNotFoundException extends NotFoundException {
    public BranchesNotFoundException(HttpStatus status, String message) {
        super(status, message);
    }
}
