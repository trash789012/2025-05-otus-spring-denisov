package ru.otus.hw.dto.slot;

import ru.otus.hw.domain.enums.SlotStatus;
import ru.otus.hw.dto.group.GroupInfoDto;

import java.time.LocalDateTime;

public record SlotDto(Long id,
                      LocalDateTime startTime,
                      LocalDateTime endTime,
                      SlotStatus status,
                      GroupInfoDto group) {
}
