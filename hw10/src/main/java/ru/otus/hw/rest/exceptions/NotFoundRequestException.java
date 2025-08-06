package ru.otus.hw.rest.exceptions;

public class NotFoundRequestException extends RuntimeException {
    public NotFoundRequestException(String message) {
        super(message);
    }
}
