package ru.otus.hw.controllers.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.slot.SlotDto;
import ru.otus.hw.dto.slot.SlotFormDto;
import ru.otus.hw.exceptions.BadRequestException;
import ru.otus.hw.service.SlotService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/slot")
@Tag(name = "Slots", description = "Операции с временными слотами репетиций")
public class SlotRestController {

    private final SlotService slotService;

    @Operation(summary = "Получить все слоты")
    @GetMapping
    public List<SlotDto> getAllSlots(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        if (start != null && end != null) {
            return slotService.findByPeriod(start, end);
        }

        return slotService.findAll();
    }

    @Operation(summary = "Получить слот по ID")
    @GetMapping("/{id}")
    public SlotDto getSlotById(@PathVariable Long id) {
        return slotService.findById(id);
    }

    @Operation(summary = "Создать новый слот")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SlotDto createSlot(@RequestBody @Valid SlotFormDto slotDto) {
        return slotService.insert(slotDto);
    }

    @Operation(summary = "Обновить существующий слот")
    @PutMapping("/{id}")
    public SlotDto updateSlot(@PathVariable Long id,
                              @RequestBody @Valid SlotFormDto slotDto) {
        if (!id.equals(slotDto.id())) {
            throw new BadRequestException("Id in path and body must match");
        }

        return slotService.update(slotDto);
    }

    @Operation(summary = "Удалить слот")
    @DeleteMapping("/{id}")
    public void deleteSlot(@PathVariable Long id) {
        slotService.delete(id);
    }
}
