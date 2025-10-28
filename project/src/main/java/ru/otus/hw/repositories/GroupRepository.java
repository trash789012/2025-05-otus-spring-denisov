package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.domain.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
