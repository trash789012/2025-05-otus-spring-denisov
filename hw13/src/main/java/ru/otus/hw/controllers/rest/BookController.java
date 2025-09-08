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
    public ResponseEntity<BookFormDto> getBookById(@PathVariable Long id) {
        return bookService.findById(id)
                .map(bookConverter::bookDtoToBookFormDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundRequestException("Book with id %s not found".formatted(id)));
    }

    @PostMapping("/api/v1/book")
    public ResponseEntity<?> createBook(
            @Valid @RequestBody BookFormDto bookDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors());
        }

        var savedBook = bookService.insert(bookDto);
        return ResponseEntity.ok(savedBook);
    }

    @PutMapping("/api/v1/book/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id,
                                        @Valid @RequestBody BookFormDto bookDto,
                                        BindingResult bindingResult) {
        if (!id.equals(bookDto.id())) {
            throw new BadRequestException("ID in path and body must match");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors());
        }

        BookDto savedBook = bookService.update(bookDto);
        return ResponseEntity.ok().body(savedBook);
    }

    @DeleteMapping("/api/v1/book/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
