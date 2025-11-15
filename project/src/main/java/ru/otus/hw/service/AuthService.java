package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.security.jwt.JwtTokenProvider;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.exceptions.InvalidCredentialsException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;

    private final JwtTokenProvider jwtTokenProvider;

    public String generateToken(String username, String password) {
        Authentication authentication;
        try {
            authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return jwtTokenProvider.generateToken(authentication);

    }
}