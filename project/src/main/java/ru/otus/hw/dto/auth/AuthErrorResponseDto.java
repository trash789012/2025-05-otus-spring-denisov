package ru.otus.hw.dto.auth;

import java.util.Map;

public record AuthErrorResponseDto(String error,
                                   String message,
                                   boolean expired,
                                   boolean invalidToken,
                                   boolean invalidCredentials,
                                   Map<String, Object> details) {
}
