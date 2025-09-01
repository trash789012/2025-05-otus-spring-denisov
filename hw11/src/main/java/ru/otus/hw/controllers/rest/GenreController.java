package ru.otus.hw.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.BadRequestException;
import ru.otus.hw.exceptions.NotFoundRequestException;
import ru.otus.hw.services.GenreService;

import java.util.Set;

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
    public Mono<ResponseEntity<?>> createGenre(@Valid @RequestBody GenreDto genreDto,
                                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return Mono.just(
                    ResponseEntity.badRequest()
                            .body(bindingResult.getAllErrors())
            );
        }

        return genreService.insert(genreDto)
                .map(savedGenre -> ResponseEntity.ok().body(savedGenre));
    }

    @PutMapping("/api/v1/genre/{id}")
    public Mono<ResponseEntity<?>> updateGenre(@PathVariable String id,
                                               @Valid @RequestBody GenreDto genreDto,
                                               BindingResult bindingResult) {

        if (!id.equals(genreDto.id())) {
            return Mono.error(new BadRequestException("ID in path and body must match"));
        }

        if (bindingResult.hasErrors()) {
            return Mono.just(
                    ResponseEntity.badRequest()
                            .body(bindingResult.getAllErrors())
            );
        }

        return genreService.update(genreDto)
                .map(updatedGenre -> ResponseEntity.ok().body(updatedGenre));
    }

    @DeleteMapping("/api/v1/genre/{id}")
    public Mono<ResponseEntity<Void>> deleteGenre(@PathVariable String id) {
        return genreService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
