package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.domain.Slot;

import java.util.List;
import java.util.Optional;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    @EntityGraph("slot-group-graph")
    Optional<Slot> findById(Long id);

    List<Slot> findAllByBookedBy_Id(Long groupId);
}
