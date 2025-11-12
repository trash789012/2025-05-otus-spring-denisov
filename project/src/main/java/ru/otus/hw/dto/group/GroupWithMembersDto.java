package ru.otus.hw.dto.group;

import ru.otus.hw.dto.user.UserInfoDto;

import java.util.List;

public record GroupWithMembersDto(Long id,
                                  String name,
                                  String description,
                                  List<UserInfoDto> members) {
}
