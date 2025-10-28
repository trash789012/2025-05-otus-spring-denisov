package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph("user-roles-graph")
    Optional<User> findByName(String username);
}
