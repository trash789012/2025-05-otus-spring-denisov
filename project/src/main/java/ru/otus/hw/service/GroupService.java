package ru.otus.hw.service;

import ru.otus.hw.dto.group.GroupDto;
import ru.otus.hw.dto.group.GroupFormDto;
import ru.otus.hw.dto.group.GroupInfoDto;
import ru.otus.hw.dto.group.GroupWithMembersDto;

import java.util.List;

public interface GroupService {

    GroupDto findById(long id);

    List<GroupDto> findAll();

    List<GroupInfoDto> findAllWithoutNested();

    GroupWithMembersDto findGroupWithMembersById(Long id);

    GroupDto insert(GroupFormDto groupDto);

    GroupDto update(GroupFormDto groupDto);

    void deleteById(long id);

}
