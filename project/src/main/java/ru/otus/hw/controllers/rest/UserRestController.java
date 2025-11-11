package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.UserDto;
import ru.otus.hw.service.CustomUserDetailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserRestController {

    private final CustomUserDetailService userDetailsService;

    @GetMapping("/self")
    public UserDto getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetailsService.findByUserName(userDetails.getUsername());
    }

}
