package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import ru.otus.hw.models.Book;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends CrudRepository<Book, Long> {
    @EntityGraph(attributePaths = {"author", "genres"})
    Optional<Book> findById(long id);

    @Override
    @Nonnull
    @EntityGraph(attributePaths = {"author"})
    List<Book> findAll();
}