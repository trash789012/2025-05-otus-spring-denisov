package ru.otus.hw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandlerEntity {
    @ExceptionHandler(NotFoundRequestException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFoundException(NotFoundRequestException ex) {
        return handleError(HttpStatus.NOT_FOUND, "Entity not found");
    }

    @ExceptionHandler(BadRequestException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBadRequestException(BadRequestException ex) {
        return handleError(HttpStatus.BAD_REQUEST, "Bad request");
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {
        return handleError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private static Mono<ResponseEntity<ErrorResponse>> handleError(HttpStatus internalServerError, String Internal_server_error) {
        ErrorResponse error = new ErrorResponse(internalServerError.value(), Internal_server_error);
        return Mono.just(ResponseEntity.status(internalServerError).body(error));
    }

    public record ErrorResponse(int status, String message) {
    }

}
