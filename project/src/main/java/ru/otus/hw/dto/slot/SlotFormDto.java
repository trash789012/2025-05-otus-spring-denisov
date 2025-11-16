package ru.otus.hw.dto.slot;

import jakarta.validation.constraints.NotNull;
import ru.otus.hw.domain.enums.SlotStatus;

import java.time.LocalDateTime;

public record SlotFormDto(Long id,
                          @NotNull(message = "Время начала не может быть пустым")
                          LocalDateTime startTime,
                          @NotNull(message = "Время окончания может быть пустым")
                          LocalDateTime endTime,
                          SlotStatus status,
                          Long groupId) {
}
