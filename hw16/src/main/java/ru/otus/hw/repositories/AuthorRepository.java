package ru.otus.hw.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.Author;

import javax.annotation.Nonnull;
import java.util.List;

@RepositoryRestResource(path = "author")
public interface AuthorRepository extends CrudRepository<Author, Long> {
    @Override
    @Nonnull
    @RestResource(path = "All", rel = "All")
    List<Author> findAll();
}
