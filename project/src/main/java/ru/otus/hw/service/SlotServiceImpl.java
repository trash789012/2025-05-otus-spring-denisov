package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.SlotConverter;
import ru.otus.hw.domain.Group;
import ru.otus.hw.domain.Slot;
import ru.otus.hw.domain.enums.SlotStatus;
import ru.otus.hw.dto.SlotDto;
import ru.otus.hw.dto.SlotFormDto;
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

    @Override
    @Transactional(readOnly = true)
    public SlotDto findById(Long id) {
        return slotRepository.findById(id)
                .map(slotConverter::toDto)
                .orElseThrow(() ->
                        new EntityNotFoundException("Slot with id %d not found".formatted(id))
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
    public List<SlotDto> findByBookedBy(Long bookedById) {
        return slotRepository.findAllByBookedBy_Id(bookedById).stream()
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
    public SlotDto insert(SlotFormDto slotDto) {
        validateBeforeSave(slotDto);
        var savedSlot = prepareSlot(slotDto);
        return slotConverter.toDto(savedSlot);
    }

    @Override
    @Transactional
    public SlotDto update(SlotFormDto slotDto) {
        if (slotDto.id() == null) {
            throw new IllegalArgumentException("Slot id is null");
        }
        validateBeforeSave(slotDto);
        var updatedSlot = prepareSlot(slotDto);
        return slotConverter.toDto(updatedSlot);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        slotRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Slot with id %d not found".formatted(id))
                );

        slotRepository.deleteById(id);
    }

    private Slot prepareSlot(SlotFormDto slotDto) {
        Slot slot;
        if (slotDto.id() != null && slotDto.id() != 0) {
            slot = slotRepository.findById(slotDto.id())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Slot with id %d not found".formatted(slotDto.id()))
                    );
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
                            new EntityNotFoundException("Group with id %d not found".formatted(slotDto.groupId()))
                    );

            slot.setBookedBy(bookedBy);
            slot.setStatus(SlotStatus.BOOKED);
        } else {
            slot.setStatus(SlotStatus.FREE);
            slot.setBookedBy(null);
        }

        return slotRepository.save(slot);
    }

    private void validateBeforeSave(SlotFormDto slotDto) {
        if (slotDto.startTime() == null || slotDto.endTime() == null) {
            throw new IllegalArgumentException("Start time and end time must not be null");
        }

        if (slotDto.startTime().isAfter(slotDto.endTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }
}
