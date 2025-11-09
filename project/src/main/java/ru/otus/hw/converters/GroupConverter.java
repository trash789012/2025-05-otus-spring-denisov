package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Group;
import ru.otus.hw.dto.GroupDto;
import ru.otus.hw.dto.GroupFormDto;
import ru.otus.hw.dto.GroupSlotsDto;
import ru.otus.hw.dto.SlotDto;
import ru.otus.hw.dto.UserDto;

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

    public GroupFormDto toDto(GroupDto group) {
        if (group == null) {
            return null;
        }

        return new GroupFormDto(
                group.id(),
                group.name(),
                group.description(),
                (group.members() != null) ?
                        group.members().stream()
                                .map(UserDto::id)
                                .toList() : null,
                (group.slots() != null) ?
                        group.slots().stream()
                                .map(SlotDto::id)
                                .toList() : null
        );
    }
}
