package ru.otus.hw.repositories.h2;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.h2.Genre;

import javax.annotation.Nonnull;
import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Override
    @Nonnull
    List<Genre> findAll();

    @Override
    @Nonnull
    List<Genre> findAllById(Iterable<Long> longs);
}
