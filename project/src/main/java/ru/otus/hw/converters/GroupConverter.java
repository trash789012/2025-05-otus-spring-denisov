package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Group;
import ru.otus.hw.dto.GroupDto;

@Component
@RequiredArgsConstructor
public class GroupConverter {

    private final SlotConverter slotConverter;

    private final UserConverter userConverter;

    public GroupDto toDto(Group group) {

        if (group == null) {
            return null;
        }

        return new GroupDto(
                group.getId(),
                group.getName(),
                group.getDescription(),
                (group.getMembers()) != null ?
                        group.getMembers().stream()
                                .map(userConverter::toDto)
                                .toList() : null,
                (group.getSlots()) != null ?
                        group.getSlots().stream()
                                .map(slotConverter::toDto)
                                .toList() : null
        );
    }
}
