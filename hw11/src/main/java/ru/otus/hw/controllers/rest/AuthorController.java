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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.BadRequestException;
import ru.otus.hw.exceptions.NotFoundRequestException;
import ru.otus.hw.services.AuthorService;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/api/v1/author")
    public Flux<AuthorDto> getAllAuthors() {
        return authorService.findAll();
    }

    @GetMapping("/api/v1/author/{id}")
    public Mono<ResponseEntity<AuthorDto>> getAuthorById(@PathVariable String id) {
        return authorService.findById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(
                        new NotFoundRequestException("Author with id %s not found!".formatted(id))
                ));
    }

    @PostMapping("/api/v1/author")
    public Mono<ResponseEntity<Object>> createAuthor(@Valid @RequestBody Mono<AuthorDto> authorDtoMono) {
        return authorDtoMono
                .flatMap(authorDto -> authorService.insert(authorDto)
                        .map(savedAuthor -> ResponseEntity.ok().<Object>body(savedAuthor))
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

    @PutMapping("/api/v1/author/{id}")
    public Mono<ResponseEntity<Object>> updateAuthor(@PathVariable String id,
                                                     @Valid @RequestBody Mono<AuthorDto> authorDtoMono) {
        return authorDtoMono
                .flatMap(authorDto -> {
                    if (!id.equals(authorDto.id())) {
                        return Mono.error(new BadRequestException("Author id %s mismatch".formatted(id)));
                    }
                    return authorService.update(authorDto)
                            .map(updatedAuthor -> ResponseEntity.ok().<Object>body(updatedAuthor));
                })
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


    @DeleteMapping("/api/v1/author/{id}")
    public Mono<ResponseEntity<Void>> deleteAuthorById(@PathVariable String id) {
        return authorService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
