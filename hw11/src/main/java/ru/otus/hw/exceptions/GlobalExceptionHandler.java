package ru.otus.hw.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@Component
public class GlobalExceptionHandler implements WebExceptionHandler {

    @Nonnull
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        String message;

        if (ex instanceof EntityNotFoundException || ex instanceof NotFoundRequestException) {
            status = HttpStatus.NOT_FOUND;
            message = ex.getMessage();
        } else if (ex instanceof BadRequestException) {
            status = HttpStatus.BAD_REQUEST;
            message = ex.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Internal Server Error";
            logError(ex, exchange);
        }

        return renderError(exchange, ex, status, message);
    }

    private void logError(Throwable ex, ServerWebExchange exchange) {
        String errorData = """
                Class: %s
                Message: %s
                """.formatted(ex.getClass().getName(), ex.getMessage());
        String requestData = """
                URL: %s
                Headers: %s
                """.formatted(exchange.getRequest().getURI(), exchange.getRequest().getHeaders());

        log.error(errorData, requestData, ex);
    }

    private Mono<Void> renderError(ServerWebExchange exchange, Throwable ex, HttpStatus status, String message) {
        if (shouldReturnJsonResponse(exchange)) {
            Map<String, Object> body = Map.of(
                    "error", message,
                    "status", status.value(),
                    "uri", exchange.getRequest().getURI().toString(),
                    "exception", ex.getClass().getSimpleName()
            );
            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse()
                            .bufferFactory()
                            .wrap(body.toString().getBytes()))
            );
        } else {
            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_HTML);
            String html = "<html><body><h1>Error: " + message + "</h1></body></html>";
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(html.getBytes()))
            );
        }
    }

    private boolean shouldReturnJsonResponse(ServerWebExchange exchange) {
        String acceptHeader = exchange.getRequest().getHeaders().getFirst("Accept");
        String path = exchange.getRequest().getURI().getPath();
        return (acceptHeader != null && acceptHeader.contains("application/json")) || path.startsWith("/api/");
    }
}
