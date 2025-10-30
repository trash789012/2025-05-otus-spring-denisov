package ru.otus.hw.service;

import ru.otus.hw.dto.SlotDto;

import java.util.List;
import java.util.Optional;

public interface SlotService {
    SlotDto findById(Long id);

    List<SlotDto> findAll();

    SlotDto insert(SlotDto slotDto);

    SlotDto update(SlotDto slotDto);

    void delete(Long id);
}
