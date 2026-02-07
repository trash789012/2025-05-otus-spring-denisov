package ru.otus.hw.dto.user;

import ru.otus.hw.dto.group.GroupWithMembersDto;

import java.util.List;

public record UserWithRolesAndGroupsDto(Long id,
                                        String name,
                                        String firstName,
                                        String lastName,
                                        String shortDescription,
                                        List<GroupWithMembersDto> groups,
                                        List<String> roles) {
}
