package ru.otus.hw.dto.group;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record GroupFormDto(Long id,
                           @NotBlank(message = "Заполните название коллектива")
                           String name,
                           String description,
                           List<Long> memberIds,
                           List<Long> slotIds) {
}
