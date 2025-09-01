package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final GenreConverter genreConverter;

    @Override
    public Flux<GenreDto> findAll() {
        return genreRepository.findAll()
                .map(genreConverter::genreToDto);
    }

    @Override
    public Flux<GenreDto> findByIds(Set<String> ids) {
        return genreRepository.findAllById(ids)
                .map(genreConverter::genreToDto);
    }

    @Override
    @Transactional
    public Mono<GenreDto> insert(GenreDto genreDto) {
        return save(genreDto);
    }

    @Override
    @Transactional
    public Mono<GenreDto> update(GenreDto genreDto) {
        return save(genreDto);
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(String id) {
        return genreRepository.deleteById(id);
    }

    private Mono<GenreDto> save(GenreDto genreDto) {
        if (genreDto.name() == null || genreDto.name().isBlank()) {
            return Mono.error(new IllegalArgumentException("Genre name is empty"));
        }

        Mono<Genre> genreMono;
        if (genreDto.id() == null) {
            Genre genre = new Genre();
            genre.setName(genreDto.name());
            genreMono = Mono.just(genre);
        } else {
            genreMono = genreRepository.findById(genreDto.id())
                    .switchIfEmpty(Mono.error(
                            new EntityNotFoundException("Genre with id %s not found".formatted(genreDto.id()))
                    ))
                    .map(existing -> {
                        existing.setName(genreDto.name());
                        return existing;
                    });
        }

        return genreMono
                .flatMap(genreRepository::save)
                .map(genreConverter::genreToDto);
    }
}
