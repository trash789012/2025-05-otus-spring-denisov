package ru.otus.hw.dto;

import ru.otus.hw.domain.enums.SlotStatus;

import java.time.LocalDateTime;

public record SlotDto(Long id,
                      LocalDateTime startTime,
                      LocalDateTime endTime,
                      SlotStatus status,
                      Long bookedById) {
}
