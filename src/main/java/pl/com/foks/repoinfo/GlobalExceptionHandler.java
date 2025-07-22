package pl.com.foks.repoinfo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.com.foks.repoinfo.exceptions.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ExceptionResponse(int status, String message) {}

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(404).body(new ExceptionResponse(ex.getStatus().value(), ex.getMessage()));
    }
}
