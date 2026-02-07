package ru.otus.hw.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserFormInfoDto(Long id,
                              @NotBlank(message = "Логин не может быть пустым")
                              String name,
                              @NotBlank(message = "Заполните имя пользователя")
                              String firstName,
                              String lastName,
                              String shortDescription) {
}
