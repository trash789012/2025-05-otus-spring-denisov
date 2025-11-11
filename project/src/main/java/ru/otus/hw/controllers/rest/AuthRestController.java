package ru.otus.hw.controllers.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.AuthRequestDto;
import ru.otus.hw.dto.AuthResponseDto;
import ru.otus.hw.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Аутентификация")
public class AuthRestController {

    private final AuthService authService;

    @Operation(summary = "Создать новый токен")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto request) {
        var token = authService.generateToken(request.username(), request.password());

        return ResponseEntity.ok(new AuthResponseDto(token));
    }
}
