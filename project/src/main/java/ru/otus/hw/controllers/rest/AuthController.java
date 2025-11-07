package ru.otus.hw.controllers.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.config.security.jwt.JwtTokenProvider;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Аутентификация")
public class AuthController {

    private final AuthenticationManager authManager;

    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "Создать новый токен")
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        return jwtTokenProvider.generateToken(authentication);
    }
}
