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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.BadRequestException;
import ru.otus.hw.exceptions.NotFoundRequestException;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/api/v1/author")
    public List<AuthorDto> getAllAuthors() {
        return authorService.findAll();
    }

    @GetMapping("/api/v1/author/{id}")
    public AuthorDto getAuthorById(@PathVariable String id) {
        return authorService.findById(id)
                .orElseThrow(() -> new NotFoundRequestException("Author with id %s not found!".formatted(id)));
    }

    @PostMapping("/api/v1/author")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDto createAuthor(@Valid @RequestBody AuthorDto authorDto) {
        return authorService.insert(authorDto);
    }

    @PutMapping("/api/v1/author/{id}")
    public AuthorDto updateAuthor(@PathVariable String id,
                                           @Valid @RequestBody AuthorDto authorDto) {
        if (!id.equals(authorDto.id())) {
            throw new BadRequestException("Author id %s mismatch".formatted(id));
        }

        return authorService.update(authorDto);
    }

    @DeleteMapping("/api/v1/author/{id}")
    public void deleteAuthorById(@PathVariable String id) {
        authorService.deleteById(id);
    }
}
