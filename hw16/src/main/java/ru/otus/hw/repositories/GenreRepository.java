package ru.otus.hw.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.Genre;

import javax.annotation.Nonnull;
import java.util.List;

@RepositoryRestResource(path = "genre")
public interface GenreRepository extends CrudRepository<Genre, Long> {
    @Override
    @Nonnull
    @RestResource(path = "All", rel = "All")
    List<Genre> findAll();

    @Override
    @RestResource(path = "bookId", rel = "bookId")
    List<Genre> findAllById(Iterable<Long> longs);
}
