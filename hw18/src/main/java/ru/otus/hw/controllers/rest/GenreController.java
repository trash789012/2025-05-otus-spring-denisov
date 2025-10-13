package ru.otus.hw.controllers.rest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.BadRequestException;
import ru.otus.hw.exceptions.NotFoundRequestException;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/api/v1/genre")
    @CircuitBreaker(name = "getAllGenres")
    @RateLimiter(name = "genreService")
    public List<GenreDto> getAllGenres() {
        return genreService.findAll();
    }

    @GetMapping("/api/v1/genre/{id}")
    @CircuitBreaker(name = "getGenreById")
    @RateLimiter(name = "genreService")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable String id) {
        return genreService.findByIds(Set.of(id))
                .stream().findFirst()
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundRequestException("Genre with id %s not found!".formatted(id)));
    }

    @PostMapping("/api/v1/genre")
    @CircuitBreaker(name = "createGenre")
    @RateLimiter(name = "genreService")
    public ResponseEntity<?> createGenre(@Valid @RequestBody GenreDto genreDto,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors());
        }

        var savedGenre = genreService.insert(genreDto);
        return ResponseEntity.ok().body(savedGenre);
    }

    @PutMapping("/api/v1/genre/{id}")
    @CircuitBreaker(name = "updateGenre")
    @RateLimiter(name = "genreService")
    public ResponseEntity<?> updateGenre(@PathVariable String id,
                                                @Valid @RequestBody GenreDto genreDto,
                                                BindingResult bindingResult) {
        if (!id.equals(genreDto.id())) {
            throw new BadRequestException("ID in path and body must match");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors());
        }

        GenreDto updatedGenre = genreService.update(genreDto);
        return ResponseEntity.ok().body(updatedGenre);
    }

    @DeleteMapping("/api/v1/genre/{id}")
    @CircuitBreaker(name = "deleteGenre")
    @RateLimiter(name = "genreService")
    public ResponseEntity<GenreDto> deleteGenre(@PathVariable String id) {
        genreService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
