package ru.otus.hw.exceptions;

public class NotFoundRequestException extends RuntimeException {
    public NotFoundRequestException(String message) {
        super(message);
    }
}
