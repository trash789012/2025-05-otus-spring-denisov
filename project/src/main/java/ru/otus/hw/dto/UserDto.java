package ru.otus.hw.dto;

import java.util.List;

public record UserDto(Long id,
                      String name,
                      String firstName,
                      String lastName,
                      String shortDescription,
                      List<GroupFormDto> groups) {
}
