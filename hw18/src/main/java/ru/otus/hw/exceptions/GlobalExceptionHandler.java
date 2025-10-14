package ru.otus.hw.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllException(
            Exception ex,
            WebRequest request) {

        logError(ex, request);
        return handleError(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
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
            Map<String, Object> body = Map.of(
                    "error", message,
                    "status", status.value(),
                    "uri", ((ServletWebRequest) request).getRequest().getRequestURI(),
                    "exception", ex.getClass().getSimpleName()
            );

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

    private boolean shouldReturnJsonResponse(ServletWebRequest request) {
        String acceptHeader = request.getRequest().getHeader("Accept");
        String uri = request.getRequest().getRequestURI();

        return (acceptHeader != null && acceptHeader.contains("application/json")) ||
                uri.startsWith("/api/");
    }

}
