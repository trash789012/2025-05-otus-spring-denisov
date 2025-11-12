package ru.otus.hw.dto.group;

import ru.otus.hw.dto.slot.SlotInfo;
import ru.otus.hw.dto.user.UserInfoDto;

import java.util.List;

public record GroupWithMembersAndSlotsDto(Long id,
                                          String name,
                                          String description,
                                          List<UserInfoDto> members,
                                          List<SlotInfo> slots) {
}
