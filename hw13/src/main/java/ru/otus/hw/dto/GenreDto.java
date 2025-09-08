package ru.otus.hw.dto;

import jakarta.validation.constraints.NotEmpty;

public record GenreDto(long id,
                       @NotEmpty(message = "заполните наименование жанра")
                       String name) {
}
