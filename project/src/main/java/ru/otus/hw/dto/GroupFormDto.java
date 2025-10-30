package ru.otus.hw.dto;

import java.util.List;

public record GroupFormDto(Long id,
                           String name,
                           String description,
                           List<Long> memberIds,
                           List<Long> slotIds) {
}
