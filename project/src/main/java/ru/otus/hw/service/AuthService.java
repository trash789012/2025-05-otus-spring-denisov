package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.security.jwt.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;

    private final JwtTokenProvider jwtTokenProvider;

    public String generateToken(String username, String password) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        return jwtTokenProvider.generateToken(authentication);
    }

}
