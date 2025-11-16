package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.SlotConverter;
import ru.otus.hw.domain.Group;
import ru.otus.hw.domain.Slot;
import ru.otus.hw.domain.enums.SlotStatus;
import ru.otus.hw.dto.slot.SlotDto;
import ru.otus.hw.dto.slot.SlotFormDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.GroupRepository;
import ru.otus.hw.repositories.SlotRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SlotServiceImpl implements SlotService {

    private final SlotRepository slotRepository;

    private final GroupRepository groupRepository;

    private final SlotConverter slotConverter;

    private final AclService aclService;

    @Override
    @Transactional(readOnly = true)
    public SlotDto findById(Long id) {
        return slotRepository.findById(id)
                .map(slotConverter::toDto)
                .orElseThrow(() ->
                        new EntityNotFoundException("Слот с ID %d не найден".formatted(id))
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotDto> findAll() {
        return slotRepository.findAll().stream()
                .map(slotConverter::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotDto> findByPeriod(LocalDateTime start, LocalDateTime end) {
        return slotRepository.findAllByStartTimeBetween(start, end).stream()
                .map(slotConverter::toDto)
                .toList();
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROOT') or @groupSecurityMatcher.isMember(#slotDto.groupId())")
    public SlotDto insert(SlotFormDto slotDto) {
        validateBeforeSave(slotDto);
        var savedSlot = prepareSlot(slotDto);
        return slotConverter.toDto(savedSlot);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROOT') or @groupSecurityMatcher.isMemberBoth(#slotDto.id(), #slotDto.groupId())")
    public SlotDto update(SlotFormDto slotDto) {
        if (slotDto.id() == null) {
            throw new IllegalArgumentException("ID слота is null");
        }
        validateBeforeSave(slotDto);
        var updatedSlot = prepareSlot(slotDto);
        return slotConverter.toDto(updatedSlot);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROOT') or @groupSecurityMatcher.isMember(#id)")
    public void delete(Long id) {
        if (!slotRepository.existsById(id)) {
            throw new EntityNotFoundException("Слот с %d не найден".formatted(id));
        }
        slotRepository.deleteById(id);
    }

    private Slot prepareSlot(SlotFormDto slotDto) {
        Slot slot;
        var isCreate = true;
        if (slotDto.id() != null && slotDto.id() != 0) {
            slot = slotRepository.findById(slotDto.id())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Слот с id %d не найден".formatted(slotDto.id()))
                    );
            isCreate = false;
        } else {
            slot = Slot.builder().
                    status(SlotStatus.FREE).
                    build();
        }

        slot.setStartTime(slotDto.startTime());
        slot.setEndTime(slotDto.endTime());

        Group bookedBy;
        if (slotDto.groupId() != null) {
            bookedBy = groupRepository.findById(slotDto.groupId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Группа с ID %d не найдена".formatted(slotDto.groupId()))
                    );

            slot.setBookedBy(bookedBy);
            slot.setStatus(SlotStatus.BOOKED);
        } else {
            slot.setStatus(SlotStatus.FREE);
            slot.setBookedBy(null);
        }

        var savedSlot = slotRepository.save(slot);
        if (isCreate) {
            aclService.createSlotPermissions(savedSlot, BasePermission.WRITE);
            aclService.createAdminPermission(savedSlot);
            aclService.createRootPermission(savedSlot);

            aclService.flushAclCache();
        }

        return savedSlot;
    }

    private void validateBeforeSave(SlotFormDto slotDto) {
        if (slotDto.startTime() == null || slotDto.endTime() == null) {
            throw new IllegalArgumentException("Время начала и время окончания не должны быть пустыми");
        }

        if (slotDto.startTime().isEqual(slotDto.endTime())) {
            throw new IllegalArgumentException("Продолжительность не может быть равна нулю");
        }

        if (slotDto.startTime().isAfter(slotDto.endTime())) {
            throw new IllegalArgumentException("Время начала не может быть больше времени окончания");
        }

        //Проверка пересечения
        var overlapping = slotRepository.findOverlappingSlots(
                slotDto.startTime(),
                slotDto.endTime(),
                slotDto.id()
        );

        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Слот пересекается с другим существующим слотом");
        }
    }
}
