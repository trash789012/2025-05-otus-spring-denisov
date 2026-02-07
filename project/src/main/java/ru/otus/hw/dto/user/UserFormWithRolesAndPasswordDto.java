package ru.otus.hw.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserFormWithRolesAndPasswordDto(Long id,
                                              @NotBlank(message = "Логин не может быть пустым")
                                              String name,
                                              @NotNull(message = "Пароль обязательно нужно заполнить")
                                              @NotBlank(message = "Пароль обязательно нужно заполнить")
                                              String password,
                                              @NotBlank(message = "Заполните имя пользователя")
                                              String firstName,
                                              String lastName,
                                              String shortDescription,
                                              @NotNull(message = "Нужно присвоить хотя бы одну роль")
                                              @Size(min = 1, message = "Нужно присвоить хотя бы одну роль")
                                              List<String> roles) {
}
