package ru.otus.hw.exceptions;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Order(Ordered.HIGHEST_PRECEDENCE)
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

    private static Mono<ResponseEntity<ErrorResponse>> handleError(HttpStatus status,
                                                                   String internalStatus) {
        ErrorResponse error = new ErrorResponse(status.value(), internalStatus);
        return Mono.just(ResponseEntity.status(status).body(error));
    }

    public record ErrorResponse(int status, String message) {
    }

}
