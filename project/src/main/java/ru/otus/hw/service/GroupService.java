package ru.otus.hw.service;

import ru.otus.hw.dto.group.GroupDto;
import ru.otus.hw.dto.group.GroupFormDto;
import ru.otus.hw.dto.group.GroupInfoDto;
import ru.otus.hw.dto.group.GroupWithMembersAndSlotsDto;
import ru.otus.hw.dto.group.GroupWithMembersDto;
import ru.otus.hw.dto.user.UserInfoDto;

import java.util.List;

public interface GroupService {

    GroupDto findById(long id);

    List<GroupDto> findAll();

    List<GroupInfoDto> findAllWithoutNested();

    GroupWithMembersDto findGroupWithMembersById(Long id);

    GroupWithMembersAndSlotsDto findGroupWithMembersAndSlotsById(Long id);

    void deleteMemberFromGroup(Long memberId, Long groupId);

    List<UserInfoDto> findUsersForGroupBySearchTerm(Long groupId, String searchTerm);

    GroupWithMembersDto addMembersToGroup(List<Long> memberIds, Long groupId);

    GroupDto insert(GroupFormDto groupDto);

    GroupDto update(GroupFormDto groupDto);


    void deleteById(Long id);

}
