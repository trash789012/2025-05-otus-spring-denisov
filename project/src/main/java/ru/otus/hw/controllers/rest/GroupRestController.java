package ru.otus.hw.controllers.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.converters.GroupConverter;
import ru.otus.hw.dto.group.GroupDto;
import ru.otus.hw.dto.group.GroupFormDto;
import ru.otus.hw.dto.group.GroupInfoDto;
import ru.otus.hw.dto.group.GroupWithMembersDto;
import ru.otus.hw.exceptions.BadRequestException;
import ru.otus.hw.service.GroupService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
@Tag(name = "Groups", description = "Операции с музыкальными группами")
public class GroupRestController {

    private final GroupService groupService;

    private final GroupConverter groupConverter;

    @Operation(summary = "Получить все группы")
    @GetMapping
    public List<GroupInfoDto> getAllGroups() {
        return groupService.findAllWithoutNested();
    }

    @Operation(summary = "Получить группу по ID")
    @GetMapping("/{id}")
    public GroupFormDto getGroupById(@PathVariable Long id) {
        var group = groupService.findById(id);
        return groupConverter.toDto(group);
    }

    @Operation(summary = "Получить группу с участниками по ID")
    @GetMapping("/{id}/members")
    public GroupWithMembersDto getGroupWithMembers(@PathVariable Long id) {
        return groupService.findGroupWithMembersById(id);
    }

    @Operation(summary = "Создать новую группу")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupDto createGroup(@RequestBody GroupFormDto groupFormDto) {
        return groupService.insert(groupFormDto);
    }

    @Operation(summary = "Обновить существующую группу")
    @PutMapping("/{id}")
    public GroupDto updateGroup(@PathVariable Long id,
                                @RequestBody GroupFormDto groupDto) {
        if (!id.equals(groupDto.id())) {
            throw new BadRequestException("Id in path and body must match");
        }

        return groupService.update(groupDto);
    }

    @Operation(summary = "Удалить группу")
    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable Long id) {
        groupService.deleteById(id);
    }

}
