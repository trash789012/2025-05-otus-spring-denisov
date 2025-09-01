package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;

public interface AuthorService {
    Flux<AuthorDto> findAll();

    Mono<AuthorDto> findById(String id);

    Mono<AuthorDto> insert(AuthorDto authorDto);

    Mono<AuthorDto> update(AuthorDto authorDto);

    Mono<Void> deleteById(String id);
}
