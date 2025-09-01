package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Genre;

import javax.annotation.Nonnull;
import java.util.List;

public interface GenreRepository extends ReactiveMongoRepository<Genre, String> {

    @Nonnull
    @Override
    Flux<Genre> findAll();

    Flux<Genre> findAllById(@Nonnull List<String> ids);

}
