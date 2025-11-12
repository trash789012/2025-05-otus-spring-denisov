package ru.otus.hw.service;

import ru.otus.hw.dto.GroupDto;
import ru.otus.hw.dto.GroupFormDto;
import ru.otus.hw.dto.GroupWithoutNestedDto;

import java.util.List;

public interface GroupService {

    GroupDto findById(long id);

    List<GroupDto> findAll();

    List<GroupWithoutNestedDto> findAllWithoutNested();

    GroupDto insert(GroupFormDto groupDto);

    GroupDto update(GroupFormDto groupDto);

    void deleteById(long id);

}
