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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.rest.exceptions.NotFoundRequestException;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/api/v1/author")
    public List<AuthorDto> getAllAuthors() {
        List<AuthorDto> authors = authorService.findAll();
        if (authors.isEmpty()) {
            throw new NotFoundRequestException("Authors not found!");
        }
        return authors;
    }

    @GetMapping("/api/v1/author/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(@PathVariable String id) {
        return authorService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundRequestException("Author with id %s not found!".formatted(id)));
    }

    @PostMapping("/api/v1/author")
    public ResponseEntity<AuthorDto> createAuthor(@RequestBody AuthorDto authorDto) {
        var savedAuthor = authorService.insert(authorDto);
        return ResponseEntity.ok().body(savedAuthor);
    }

    @PutMapping("/api/v1/author/{id}")
    public ResponseEntity<AuthorDto> updateAuthor(@PathVariable String id,
                                                  @RequestBody AuthorDto authorDto) {
        if (!id.equals(authorDto.id())) {
            return ResponseEntity.badRequest().build();
        }

        AuthorDto updatedAuthor = authorService.update(authorDto);
        return ResponseEntity.ok().body(updatedAuthor);
    }

    @DeleteMapping("/api/v1/author/{id}")
    public ResponseEntity<Void> deleteAuthorById(@PathVariable String id) {
        authorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
