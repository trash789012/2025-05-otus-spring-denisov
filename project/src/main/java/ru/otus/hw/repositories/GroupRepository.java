package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.domain.Group;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    @EntityGraph("group-members-graph")
    Optional<Group> findById(Long id);

    @EntityGraph("group-members-graph")
    Optional<Group> findByName(String name);
}
