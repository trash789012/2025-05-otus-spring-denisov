package ru.otus.hw.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BookFormDto(String id,
                          @NotEmpty(message = "заполните название книги")
                          String title,
                          @NotEmpty(message = "выберите автора из списка")
                          String authorId,
                          @NotEmpty(message = "выберите хотя бы один жанр")
                          List<String> genreIds) {
}
