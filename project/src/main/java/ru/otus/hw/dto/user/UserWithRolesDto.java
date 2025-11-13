package ru.otus.hw.dto.user;

import ru.otus.hw.domain.enums.UserRole;

import java.util.List;

public record UserWithRolesDto(Long id,
                               String name,
                               String firstName,
                               String lastName,
                               String shortDescription,
                               List<String> roles) {
}
