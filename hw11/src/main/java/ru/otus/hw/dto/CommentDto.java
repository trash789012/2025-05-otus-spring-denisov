package ru.otus.hw.dto;

import jakarta.validation.constraints.NotEmpty;

public record CommentDto(String id,
                         @NotEmpty(message = "заполните текст комментария")
                         String text,
                         String bookId) {
}
