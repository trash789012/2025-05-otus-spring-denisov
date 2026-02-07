package ru.otus.hw.dto.user;

import ru.otus.hw.dto.group.GroupFormDto;

import java.util.List;

public record UserDto(Long id,
                      String name,
                      String firstName,
                      String lastName,
                      String shortDescription,
                      List<GroupFormDto> groups) {
}
