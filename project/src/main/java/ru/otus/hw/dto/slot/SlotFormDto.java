package ru.otus.hw.dto.slot;

import ru.otus.hw.domain.enums.SlotStatus;

import java.time.LocalDateTime;

public record SlotFormDto(Long id,
                          LocalDateTime startTime,
                          LocalDateTime endTime,
                          SlotStatus status,
                          Long groupId) {
}
