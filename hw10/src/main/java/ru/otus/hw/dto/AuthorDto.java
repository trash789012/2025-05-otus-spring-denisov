package ru.otus.hw.dto;

import jakarta.validation.constraints.NotEmpty;

public record AuthorDto(String id,
                        @NotEmpty(message = "Заполните имя автора")
                        String fullName) {
}
