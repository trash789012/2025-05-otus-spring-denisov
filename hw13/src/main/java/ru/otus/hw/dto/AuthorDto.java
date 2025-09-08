package ru.otus.hw.dto;

import jakarta.validation.constraints.NotEmpty;

public record AuthorDto(long id,
                        @NotEmpty(message = "Заполните имя автора")
                        String fullName) {
}
