package pl.com.foks.reporest;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RepositoriesNotFoundException extends NotFoundException {
    public RepositoriesNotFoundException(HttpStatus status, String message) {
        super(status, message);
    }
}
