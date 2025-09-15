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
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.exceptions.BadRequestException;
import ru.otus.hw.exceptions.NotFoundRequestException;
import ru.otus.hw.services.BookService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    private final BookConverter bookConverter;

    @GetMapping("/api/v1/book")
    public List<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/api/v1/book/{id}")
    public BookFormDto getBookById(@PathVariable String id) {
        return bookService.findById(id)
                .map(bookConverter::bookDtoToBookFormDto)
                .orElseThrow(() -> new NotFoundRequestException("Book with id %s not found".formatted(id)));
    }

    @PostMapping("/api/v1/book")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(@Valid @RequestBody BookFormDto bookDto) {
        return bookService.insert(bookDto);
    }

    @PutMapping("/api/v1/book/{id}")
    public BookDto updateBook(@PathVariable String id,
                                        @Valid @RequestBody BookFormDto bookDto) {
        if (!id.equals(bookDto.id())) {
            throw new BadRequestException("ID in path and body must match");
        }

        return bookService.update(bookDto);
    }

    @DeleteMapping("/api/v1/book/{id}")
    public void deleteBook(@PathVariable String id) {
        bookService.deleteById(id);
    }
}
