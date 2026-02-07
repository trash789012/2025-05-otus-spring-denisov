package ru.otus.hw.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserWithRolesDto(Long id,
                               @NotBlank(message = "Логин не может быть пустым")
                               String name,
                               @NotBlank(message = "Заполните имя")
                               String firstName,
                               String lastName,
                               String shortDescription,
                               @NotNull(message = "Присвойте хотя бы одну роль")
                               @Size(min = 1, message = "Присвойте хотя бы одну роль")
                               List<String> roles) {
}
