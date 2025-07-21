package pl.com.foks.reporest;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(HttpStatus status, String message) {
        super(status, message);
    }
}
