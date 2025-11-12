package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Slot;
import ru.otus.hw.dto.group.GroupInfoDto;
import ru.otus.hw.dto.slot.SlotDto;
import ru.otus.hw.dto.slot.SlotInfo;

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
                ((slot.getBookedBy()) != null ? new GroupInfoDto(
                        slot.getBookedBy().getId(),
                        slot.getBookedBy().getName(),
                        slot.getBookedBy().getDescription()
                ) : null)
        );
    }

    public SlotInfo toInfoDto(Slot slot) {
        if (slot == null) {
            return null;
        }

        return new SlotInfo(
                slot.getId(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getStatus()
        );
    }
}
