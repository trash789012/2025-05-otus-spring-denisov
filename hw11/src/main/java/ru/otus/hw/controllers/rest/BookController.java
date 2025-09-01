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
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.exceptions.BadRequestException;
import ru.otus.hw.exceptions.NotFoundRequestException;
import ru.otus.hw.services.BookService;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    private final BookConverter bookConverter;

    @GetMapping("/api/v1/book")
    public Flux<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/api/v1/book/{id}")
    public Mono<ResponseEntity<BookFormDto>> getBookById(@PathVariable String id) {
        return bookService.findById(id)
                .flatMap(book -> {
                    BookFormDto dto = bookConverter.bookDtoToBookFormDto(book);
                    return Mono.just(ResponseEntity.ok(dto));
                })
                .switchIfEmpty(Mono.error(
                        new NotFoundRequestException("Book with id %s not found".formatted(id)))
                );
    }


    @PostMapping("/api/v1/book")
//    public Mono<ResponseEntity<?>> createBook(
//            @Valid @RequestBody BookFormDto bookDto,
//            BindingResult bindingResult) {
//
//        if (bindingResult.hasErrors()) {
//            return Mono.just(
//                    ResponseEntity.badRequest()
//                            .body(bindingResult.getAllErrors())
//            );
//        }
//
//        return bookService.insert(bookDto)
//                .map(savedBook -> ResponseEntity.ok().body(savedBook));
//    }

    public Mono<ResponseEntity<Object>> createBook(@Valid @RequestBody Mono<BookFormDto> bookDtoMono) {
        return bookDtoMono
                .flatMap(bookDto -> bookService.insert(bookDto)
                        .map(savedBook -> ResponseEntity.ok().<Object>body(savedBook))
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

    @PutMapping("/api/v1/book/{id}")
    public Mono<ResponseEntity<Object>> updateBook(@PathVariable String id,
                                                   @Valid @RequestBody Mono<BookFormDto> bookDtoMono) {
        return bookDtoMono
                .flatMap(bookDto -> {
                    if (!id.equals(bookDto.id())) {
                        return Mono.error(new BadRequestException("ID in path and body must match"));
                    }
                    return bookService.update(bookDto)
                            .map(updatedBook -> ResponseEntity.ok().<Object>body(updatedBook));
                })
                .onErrorResume(WebExchangeBindException.class, ex -> {
                    var errors = ex.getFieldErrors().stream()
                            .map(fieldError -> Optional.ofNullable(fieldError.getDefaultMessage())
                                    .orElse("Invalid value"))
                            .toList();
                    return Mono.just(ResponseEntity.badRequest().body(errors));
                });
    }


    @DeleteMapping("/api/v1/book/{id}")
    public Mono<ResponseEntity<Void>> deleteBook(@PathVariable String id) {
        return bookService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
