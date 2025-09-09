package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorDto(long id,
                        @NotBlank(message = "Заполните имя автора")
                        String fullName) {
}
