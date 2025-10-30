package ru.otus.hw.service;

import ru.otus.hw.dto.GroupDto;

import java.util.List;
import java.util.Optional;

public interface GroupService {

    Optional<GroupDto> findById(long id);

    List<GroupDto> findAll();

    GroupDto insert(GroupDto groupDto);

    GroupDto update(GroupDto groupDto);

    GroupDto delete(long id);

}
