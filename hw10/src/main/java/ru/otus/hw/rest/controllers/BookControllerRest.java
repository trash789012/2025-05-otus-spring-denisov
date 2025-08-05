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
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookControllerRest {
    private final BookService bookService;

    private final BookConverter bookConverter;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentService commentService;

    @GetMapping("/api/v1/book")
    public List<BookDto> getAllBooks() {
        List<BookDto> books = bookService.findAll();
        if (books.isEmpty()) {
            throw new EntityNotFoundException("Book not found!");
        }

        return books;
    }

    @GetMapping("/api/v1/book/{id}")
    public ResponseEntity<BookFormDto> getBookById(@PathVariable String id) {
        return bookService.findById(id)
                .map(bookConverter::bookDtoToBookFormDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/v1/book")
    public ResponseEntity<BookDto> createBook(@RequestBody BookFormDto bookFormDto) {
        var savedBook = bookService.insert(bookFormDto);
        return ResponseEntity.ok().body(savedBook);
    }

    @PutMapping("/api/v1/book/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable String id, @RequestBody BookFormDto bookFormDto) {
        if (!id.equals(bookFormDto.id())) {
            throw new IllegalArgumentException("ID in path and body must match");
        }

        BookDto updatedBook = bookService.update(bookFormDto);
        return ResponseEntity.ok().body(updatedBook);
    }

    @DeleteMapping("/api/v1/book/{id}")
    public ResponseEntity<BookDto> deleteBook(@PathVariable String id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
