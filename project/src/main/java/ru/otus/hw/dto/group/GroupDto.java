package ru.otus.hw.dto.group;

import ru.otus.hw.dto.slot.SlotDto;
import ru.otus.hw.dto.user.UserDto;

import java.util.List;

public record GroupDto(Long id,
                       String name,
                       String description,
                       List<UserDto> members,
                       List<SlotDto> slots) {
}
