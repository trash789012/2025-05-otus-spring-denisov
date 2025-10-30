package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Slot;
import ru.otus.hw.dto.SlotDto;

@Component
@RequiredArgsConstructor
public class SlotConverter {

    public SlotDto toDto(Slot slot) {
        if (slot == null) {
            return null;
        }

        return new SlotDto(
                slot.getId(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getStatus(),
                ((slot.getBookedBy()) != null ? slot.getBookedBy().getId() : null)
        );
    }
}
