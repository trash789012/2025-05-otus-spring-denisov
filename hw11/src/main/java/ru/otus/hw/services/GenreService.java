package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.GenreDto;

import java.util.Set;

public interface GenreService {
    Flux<GenreDto> findAll();

    Flux<GenreDto> findByIds(Set<String> ids);

    Mono<GenreDto> insert(GenreDto genreDto);

    Mono<GenreDto> update(GenreDto genreDto);

    Mono<Void> deleteById(String id);
}
