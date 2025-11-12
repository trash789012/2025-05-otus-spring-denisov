package ru.otus.hw.dto.group;

import java.util.List;

public record GroupFormDto(Long id,
                           String name,
                           String description,
                           List<Long> memberIds,
                           List<Long> slotIds) {
}
