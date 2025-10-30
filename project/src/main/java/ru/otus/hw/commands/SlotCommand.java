package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.domain.Slot;
import ru.otus.hw.domain.enums.SlotStatus;
import ru.otus.hw.repositories.GroupRepository;
import ru.otus.hw.repositories.SlotRepository;

import java.time.LocalDateTime;
import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class SlotCommand {

    private final SlotRepository slotRepository;

    private final GroupRepository groupRepository;

    @Transactional(readOnly = true) //todo: убрать
    @ShellMethod(key = "list-slots", value = "Вывести все слоты")
    public String listSlots() {
        List<Slot> slots = slotRepository.findAll();
        if (slots.isEmpty()) {
            return "Слотов нет";
        }
        StringBuilder sb = new StringBuilder("=== SLOTS ===\n");
        slots.forEach(s -> sb.append(s.getId())
                .append(". ").append(s.getStartTime())
                .append(" - ").append(s.getEndTime())
                .append(" [status=").append(s.getStatus())
                .append(" group=").append(s.getBookedBy() != null ? s.getBookedBy().getName() : "—")
                .append("]\n"));

        return sb.toString();
    }

    @ShellMethod(key = "create-slot", value = "Создать свободный слот (пример: 2025-10-23T18:00)")
    public String createSlot(@ShellOption String start, @ShellOption String end) {
        LocalDateTime st = LocalDateTime.parse(start);
        LocalDateTime en = LocalDateTime.parse(end);
        Slot slot = Slot.builder()
                .startTime(st)
                .endTime(en)
                .status(SlotStatus.FREE)
                .build();
        slotRepository.save(slot);
        return "✅ Слот создан: " + start + " - " + end;
    }

    @ShellMethod(key = "book-slot", value = "Забронировать слот")
    public String bookSlot(@ShellOption long slotId, @ShellOption long groupId) {
        var slot = slotRepository.findById(slotId).orElse(null);
        var group = groupRepository.findById(groupId).orElse(null);

        if (slot == null || group == null) {
            return "❌ Слот или группа не найдены";
        }

        if (slot.getStatus() == SlotStatus.BOOKED) {
            return "❌ Слот уже занят";
        }

        slot.setStatus(SlotStatus.BOOKED);
        slot.setBookedBy(group);
        slotRepository.save(slot);

        return "✅ Слот #" + slotId + " забронирован группой " + group.getName();
    }

}
