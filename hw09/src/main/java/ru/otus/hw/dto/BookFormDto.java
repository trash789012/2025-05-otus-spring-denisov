package ru.otus.hw.dto;

import java.util.List;

public record BookFormDto(String id, String title, String authorId, List<String> genreIds) {
}
