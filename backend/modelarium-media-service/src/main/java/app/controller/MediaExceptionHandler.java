package app.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.FileNotFoundException;

@Log4j2
@RestControllerAdvice
public class MediaExceptionHandler {
    @ExceptionHandler(FileNotFoundException.class)
    public Mono<ResponseEntity<String>> handleNotFound(FileNotFoundException ex) {
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ex.getMessage())
        );
    }

    @ExceptionHandler(S3Exception.class)
    public Mono<ResponseEntity<String>> handleS3Exception(S3Exception ex) {
        HttpStatus status = switch (ex.statusCode()) {
            case 403 -> HttpStatus.FORBIDDEN;
            case 404 -> HttpStatus.NOT_FOUND;
            case 500 -> HttpStatus.BAD_GATEWAY;
            default -> HttpStatus.SERVICE_UNAVAILABLE;
        };
        log.error("Error S3: status = {}, message = {}", status, ex.getMessage());
        return Mono.just(
                ResponseEntity
                        .status(status)
                        .body("Error S3: " + ex.getMessage())
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public Mono<ResponseEntity<String>> handleDatabaseException(DataAccessException ex) {
        log.error("Database error: {}", ex.getMessage());
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Database error: " + ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<String>> handleGeneric(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage());
        return Mono.just
                (ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ex.getMessage())
                );
    }
}
