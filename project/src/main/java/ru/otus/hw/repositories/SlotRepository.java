package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.domain.Slot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    @EntityGraph("slot-group-graph")
    Optional<Slot> findById(Long id);

    @EntityGraph("slot-group-graph")
    List<Slot> findAllByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
            SELECT s
            FROM Slot s
            WHERE 
                (:excludeId IS NULL OR s.id <> :excludeId)
                AND s.startTime < :end
                AND s.endTime > :start
            """)
    List<Slot> findOverlappingSlots(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("excludeId") Long excludeId
    );
}
