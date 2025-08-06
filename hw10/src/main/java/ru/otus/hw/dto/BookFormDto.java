package ru.otus.hw.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BookFormDto(String id,
                          String title,
                          String authorId,
                          @NotEmpty(message = "выберите хотя бы один жанр")
                          List<String> genreIds) {
}
