package ru.otus.hw.dto;

import jakarta.validation.constraints.NotEmpty;

public record GenreDto(String id,
                       @NotEmpty(message = "заполните наименование жанра")
                       String name) {
}
