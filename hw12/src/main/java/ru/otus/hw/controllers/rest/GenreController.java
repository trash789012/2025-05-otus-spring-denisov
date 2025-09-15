package ru.otus.hw.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    public List<GenreDto> getAllGenres() {
        return genreService.findAll();
    }

    @GetMapping("/api/v1/genre/{id}")
    public GenreDto getGenreById(@PathVariable String id) {
        return genreService.findByIds(Set.of(id))
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundRequestException("Genre with id %s not found!".formatted(id)));
    }

    @PostMapping("/api/v1/genre")
    @ResponseStatus(HttpStatus.CREATED)
    public GenreDto createGenre(@Valid @RequestBody GenreDto genreDto) {
        return genreService.insert(genreDto);
    }

    @PutMapping("/api/v1/genre/{id}")
    public GenreDto updateGenre(@PathVariable String id,
                                @Valid @RequestBody GenreDto genreDto) {
        if (!id.equals(genreDto.id())) {
            throw new BadRequestException("ID in path and body must match");
        }

        return genreService.update(genreDto);
    }

    @DeleteMapping("/api/v1/genre/{id}")
    public void deleteGenre(@PathVariable String id) {
        genreService.deleteById(id);
    }
}
