package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.domain.Slot;

public interface SlotRepository extends JpaRepository<Slot, Long> {
}
