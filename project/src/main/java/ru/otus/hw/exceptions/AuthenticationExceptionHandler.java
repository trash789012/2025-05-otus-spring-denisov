package ru.otus.hw.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.auth.AuthErrorResponseDto;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        AuthErrorResponseDto errorResponse = new AuthErrorResponseDto(
                "AUTH_ERROR",
                authException.getMessage(),
                true,
                false,
                false,
                Map.of(
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "path", request.getServletPath()
                )
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
