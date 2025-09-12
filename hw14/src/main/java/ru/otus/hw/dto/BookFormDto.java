package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BookFormDto(long id,
                          @NotBlank(message = "заполните название книги")
                          String title,
                          @NotNull(message = "выберите автора из списка")
                          long authorId,
                          @NotNull(message = "выберите хотя бы один жанр")
                          @Size(min = 1, message = "выберите хотя бы один жанр")
                          List<Long> genreIds) {
}
