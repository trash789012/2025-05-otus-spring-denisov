package ru.otus.hw.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BookFormDto(long id,
                          @NotEmpty(message = "заполните название книги")
                          String title,
                          @NotEmpty(message = "выберите автора из списка")
                          long authorId,
                          @NotEmpty(message = "выберите хотя бы один жанр")
                          List<Long> genreIds) {
}
