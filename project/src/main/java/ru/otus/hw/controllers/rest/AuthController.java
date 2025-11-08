package ru.otus.hw.controllers.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Аутентификация")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Создать новый токен")
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        return authService.generateToken(username, password);
    }
}
