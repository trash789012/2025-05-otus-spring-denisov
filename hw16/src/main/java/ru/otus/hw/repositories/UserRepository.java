package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.User;

import java.util.Optional;

@RepositoryRestResource(path = "user")
public interface UserRepository extends CrudRepository<User, Long> {
    @EntityGraph("user-roles-graph")
    @RestResource(path = "userName", rel = "userName")
    Optional<User> findByUsername(String name);
}
