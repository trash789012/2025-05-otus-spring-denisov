package ru.otus.hw.rest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/api/v1/genre")
    public List<GenreDto> getAllGenres() {
        List<GenreDto> genres = genreService.findAll();
        if (genres.isEmpty()) {
            throw new EntityNotFoundException("Genres not found!");
        }
        return genres;
    }

    @GetMapping("/api/v1/genre/{id}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable String id) {
        return genreService.findByIds(Set.of(id))
                .stream().findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/v1/genre")
    public ResponseEntity<GenreDto> createGenre(@RequestBody GenreDto genreDto) {
        var savedGenre = genreService.insert(genreDto);
        return ResponseEntity.ok().body(savedGenre);
    }

    @PutMapping("/api/v1/genre/{id}")
    public ResponseEntity<GenreDto> updateGenre(@PathVariable String id,
                                                @RequestBody GenreDto genreDto) {
        if (!id.equals(genreDto.id())) {
            throw new IllegalArgumentException("ID in path and body must match");
        }

        GenreDto updatedGenre = genreService.update(genreDto);
        return ResponseEntity.ok().body(updatedGenre);
    }

    @DeleteMapping("/api/v1/genre/{id}")
    public ResponseEntity<GenreDto> deleteGenre(@PathVariable String id) {
        genreService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
