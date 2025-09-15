package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;

public record GenreDto(long id,
                       @NotBlank(message = "заполните наименование жанра")
                       String name) {
}
