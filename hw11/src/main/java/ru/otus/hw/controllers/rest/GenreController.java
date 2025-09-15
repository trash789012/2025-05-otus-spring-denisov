package ru.otus.hw.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.BadRequestException;
import ru.otus.hw.exceptions.NotFoundRequestException;
import ru.otus.hw.services.GenreService;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/api/v1/genre")
    public Flux<GenreDto> getAllGenres() {
        return genreService.findAll();
    }

    @GetMapping("/api/v1/genre/{id}")
    public Mono<ResponseEntity<GenreDto>> getGenreById(@PathVariable String id) {
        return genreService.findByIds(Set.of(id))
                .next()
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new NotFoundRequestException(
                        "Genre with id %s not found!".formatted(id)
                )));
    }

    @PostMapping("/api/v1/genre")
    public Mono<ResponseEntity<Object>> createGenre(@Valid @RequestBody Mono<GenreDto> genreDtoMono) {
        return genreDtoMono
                .flatMap(genreDto -> genreService.insert(genreDto)
                        .map(savedGenre -> ResponseEntity.ok().<Object>body(savedGenre))
                )
                .onErrorResume(WebExchangeBindException.class, ex -> {
                    var errors = ex.getFieldErrors().stream()
                            .collect(Collectors.toMap(
                                    FieldError::getField,
                                    fieldError -> Optional.ofNullable(fieldError.getDefaultMessage())
                                            .orElse("Invalid value")
                            ));
                    return Mono.just(ResponseEntity.badRequest().body(errors));
                });
    }

    @PutMapping("/api/v1/genre/{id}")
    public Mono<ResponseEntity<Object>> updateGenre(@PathVariable String id,
                                                    @Valid @RequestBody Mono<GenreDto> genreDtoMono) {
        return genreDtoMono
                .flatMap(genreDto -> {
                    if (!id.equals(genreDto.id())) {
                        return Mono.error(new BadRequestException("Genre id %s mismatch".formatted(id)));
                    }
                    return genreService.update(genreDto)
                            .map(updatedGenre -> ResponseEntity.ok().<Object>body(updatedGenre));
                })
                .onErrorResume(WebExchangeBindException.class, ex -> {
                    var errors = ex.getFieldErrors().stream()
                            .map(fieldError -> Optional.ofNullable(fieldError.getDefaultMessage())
                                    .orElse("Invalid value"))
                            .toList();
                    return Mono.just(ResponseEntity.badRequest().body(errors));
                });
    }

    @DeleteMapping("/api/v1/genre/{id}")
    public Mono<ResponseEntity<Void>> deleteGenre(@PathVariable String id) {
        return genreService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
