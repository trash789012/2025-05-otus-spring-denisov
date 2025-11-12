package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.GroupConverter;
import ru.otus.hw.converters.UserConverter;
import ru.otus.hw.domain.Group;
import ru.otus.hw.domain.Slot;
import ru.otus.hw.domain.User;
import ru.otus.hw.dto.group.GroupDto;
import ru.otus.hw.dto.group.GroupFormDto;
import ru.otus.hw.dto.group.GroupInfoDto;
import ru.otus.hw.dto.group.GroupWithMembersAndSlotsDto;
import ru.otus.hw.dto.group.GroupWithMembersDto;
import ru.otus.hw.dto.user.UserInfoDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.GroupRepository;
import ru.otus.hw.repositories.SlotRepository;
import ru.otus.hw.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    private final UserConverter userConverter;

    private final SlotRepository slotRepository;

    private final GroupConverter groupConverter;

    private record groupNestedObjects(List<User> members, List<Slot> slots) {

    }

    @Override
    @Transactional(readOnly = true)
    public GroupDto findById(long id) {
        return groupRepository.findById(id)
                .map(groupConverter::toDto)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Group with id %d not found".formatted(id)
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupDto> findAll() {
        return groupRepository.findAll().stream()
                .map(groupConverter::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupInfoDto> findAllWithoutNested() {
        return groupRepository.findAll().stream()
                .map(groupConverter::toWithoutNestedDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GroupWithMembersDto findGroupWithMembersById(Long id) {
        var group = groupRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Group with id %d not found".formatted(id))
                );
        return groupConverter.toWithMembersDto(group);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupWithMembersAndSlotsDto findGroupWithMembersAndSlotsById(Long id) {
        var group = groupRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Group with id %d not found".formatted(id))
                );
        return groupConverter.toWithMembersAndSlotsDto(group);

    }

    @Override
    @Transactional
    public void deleteMemberFromGroup(Long memberId, Long groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("Group id is null");
        }
        if (memberId == null) {
            throw new IllegalArgumentException("Member id is null");
        }

        var group = groupRepository.findById(groupId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Group with id %d not found".formatted(groupId))
                );
        var member = userRepository.findById(memberId)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with id %d not found".formatted(memberId))
                );

        group.getMembers().remove(member);
        groupRepository.save(group);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserInfoDto> findUsersForGroupBySearchTerm(Long groupId, String searchTerm) {
        List<Long> existingMemberIds = new ArrayList<>();
        var group = groupRepository.findById(groupId);
        if (group.isPresent()) {
            existingMemberIds = group.get().getMembers().stream()
                    .map(User::getId)
                    .toList();
        }
        return userRepository.findBySearchTermAndIdNotIn(searchTerm, existingMemberIds)
                .stream()
                .map(userConverter::toInfoDto)
                .toList();
    }

    @Override
    @Transactional
    public GroupDto insert(GroupFormDto groupDto) {
        validateBeforeSave(groupDto);
        var savedGroup = prepareGroup(groupDto);
        return groupConverter.toDto(savedGroup);
    }

    @Override
    @Transactional
    public GroupDto update(GroupFormDto groupDto) {
        if (groupDto.id() == null) {
            throw new IllegalArgumentException("Group id is null");
        }
        validateBeforeSave(groupDto);
        var updatedGroup = prepareGroup(groupDto);
        return groupConverter.toDto(updatedGroup);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        groupRepository.deleteById(id);
    }

    private Group prepareGroup(GroupFormDto groupDto) {
        Group group;
        if (groupDto.id() != null) {
            group = groupRepository.findById(groupDto.id())
                    .orElseThrow(() ->
                            new EntityNotFoundException(
                                    "Group with id %d not found".formatted(groupDto.id())
                            )
                    );
        } else {
            group = new Group();
        }

        groupNestedObjects nestedObjs = prepareNestedObjects(groupDto);
        if (nestedObjs.members() != null) {
            group.setMembers(nestedObjs.members());
        }
        if (nestedObjs.slots() != null) {
            group.setSlots(nestedObjs.slots());
        }

        group.setName(groupDto.name());
        group.setDescription(groupDto.description());

        return groupRepository.save(group);
    }

    private groupNestedObjects prepareNestedObjects(GroupFormDto groupDto) {
        List<User> members = null;
        if (groupDto.memberIds() != null) {
            members = userRepository.findAllById(groupDto.memberIds());
            if (members.size() != groupDto.memberIds().size()) {
                throw new EntityNotFoundException(
                        "One or all members with ids %s not found".formatted(groupDto.memberIds())
                );
            }
        }

        List<Slot> slots = null;
        if (groupDto.slotIds() != null) {
            slots = slotRepository.findAllById(groupDto.slotIds());
            if (slots.size() != groupDto.slotIds().size()) {
                throw new EntityNotFoundException(
                        "One or all slots with ids %s not found".formatted(groupDto.slotIds())
                );
            }
        }
        return new groupNestedObjects(members, slots);
    }

    private void validateBeforeSave(GroupFormDto groupDto) {
        if (groupDto.name().isEmpty()) {
            throw new IllegalArgumentException("Group name is empty");
        }
    }
}