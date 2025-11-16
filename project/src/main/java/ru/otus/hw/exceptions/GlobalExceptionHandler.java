package ru.otus.hw.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.otus.hw.dto.auth.AuthErrorResponseDto;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        return handleError(ex, request, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex,
                                                       WebRequest request) {
        Map<String, Object> detail = getMessageDetail(ex,
                (ServletWebRequest) request, HttpStatus.UNAUTHORIZED, ex.getMessage()
        );
        var body = new AuthErrorResponseDto(
                ex.getMessage(),
                ex.getMessage(),
                false,
                false,
                true,
                detail
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Object> handleInvalidCredentials(InvalidCredentialsException ex,
                                                           WebRequest request) {
        Map<String, Object> detail = getMessageDetail(ex,
                (ServletWebRequest) request, HttpStatus.UNAUTHORIZED, ex.getMessage()
        );
        var body = new AuthErrorResponseDto(
                ex.getMessage(),
                ex.getMessage(),
                false,
                false,
                true,
                detail
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(io.jsonwebtoken.ExpiredJwtException.class)
    public ResponseEntity<Object> handleExpiredJwt(ExpiredJwtException ex, WebRequest request) {
        Map<String, Object> detail = getMessageDetail(ex, (ServletWebRequest) request, HttpStatus.UNAUTHORIZED, ex.getMessage());
        var body = new AuthErrorResponseDto(
                ex.getMessage(),
                ex.getMessage(),
                true,
                false,
                false,
                detail
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Object> handleJwtException(JwtException ex, WebRequest request) {
        Map<String, Object> detail = getMessageDetail(ex, (ServletWebRequest) request, HttpStatus.UNAUTHORIZED, ex.getMessage());
        var body = new AuthErrorResponseDto(
                ex.getMessage(),
                ex.getMessage(),
                false,
                true,
                false,
                detail
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handeNotFoundException(
            EntityNotFoundException ex,
            WebRequest request) {

        return handleError(ex, request, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NotFoundRequestException.class)
    public ResponseEntity<Object> handleBadRequest(
            NotFoundRequestException ex,
            WebRequest request) {

        return handleError(ex, request, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(
            BadRequestException ex,
            WebRequest request) {

        return handleError(ex, request, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request) {

        ServletWebRequest servletRequest = (ServletWebRequest) request;

        if (shouldReturnJsonResponse(servletRequest)) {
            Map<String, Object> body = Map.of(
                    "error", "ACCESS_DENIED",
                    "status", HttpStatus.FORBIDDEN.value(),
                    "redirect", "/access-denied",
                    "message", ex.getMessage()
            );
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body);
        }

        try {
            Objects.requireNonNull(servletRequest.getResponse())
                    .sendRedirect(servletRequest.getRequest().getContextPath() + "/access-denied");
        } catch (IOException e) {
            log.error("Redirect failed", e);
        }
        return null;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllException(
            Exception ex,
            WebRequest request) {

        logError(ex, request);
        return handleError(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        return ResponseEntity.badRequest()
                .body(ex.getBindingResult().getAllErrors());
    }

    private void logError(Exception ex, WebRequest request) {
        String errorData = """
                    Class: %s
                    Message: %s
                    StackTrace: %s
                """.formatted(ex.getClass().getName(), ex.getMessage(), ex.getStackTrace());
        String requestData = """
                    URL: %s
                    Headers: %s
                """.formatted(
                request.getDescription(false),
                request.getHeaderNames());

        log.error(errorData, requestData);
    }

    private ResponseEntity<Object> handleError(
            Exception ex,
            WebRequest request,
            HttpStatus status,
            String message) {

        if (shouldReturnJsonResponse((ServletWebRequest) request)) {
            Map<String, Object> body = getMessageDetail(ex, (ServletWebRequest) request, status, message);

            return ResponseEntity
                    .status(status)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body);
        } else {
            ModelAndView modelAndView = new ModelAndView("customError");
            modelAndView.addObject("errorText", message);
            return ResponseEntity
                    .status(status)
                    .contentType(MediaType.TEXT_HTML)
                    .body(modelAndView);
        }
    }

    private static Map<String, Object> getMessageDetail(Exception ex, ServletWebRequest request, HttpStatus status, String message) {
        Map<String, Object> body = Map.of(
                "error", message,
                "status", status.value(),
                "uri", request.getRequest().getRequestURI(),
                "exception", ex.getClass().getSimpleName()
        );
        return body;
    }

    private boolean shouldReturnJsonResponse(ServletWebRequest request) {
        String acceptHeader = request.getRequest().getHeader("Accept");
        String uri = request.getRequest().getRequestURI();

        return (acceptHeader != null && acceptHeader.contains("application/json")) ||
                uri.startsWith("/api/");
    }
}
