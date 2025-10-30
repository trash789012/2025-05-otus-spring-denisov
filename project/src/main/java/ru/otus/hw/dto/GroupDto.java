package ru.otus.hw.dto;

import java.util.List;

public record GroupDto(Long id,
                       String name,
                       String description,
                       List<UserDto> members,
                       List<SlotDto> slots) {
}
