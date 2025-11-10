package ru.otus.hw.service;

import ru.otus.hw.dto.SlotDto;
import ru.otus.hw.dto.SlotFormDto;

import java.time.LocalDateTime;
import java.util.List;

public interface SlotService {
    SlotDto findById(Long id);

    List<SlotDto> findAll();

    List<SlotDto> findByBookedBy(Long bookedById);

    List<SlotDto> findByPeriod(LocalDateTime start, LocalDateTime end);

    SlotDto insert(SlotFormDto slotDto);

    SlotDto update(SlotFormDto slotDto);

    void delete(Long id);
}
