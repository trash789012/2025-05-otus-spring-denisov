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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.BadRequestException;
import ru.otus.hw.exceptions.NotFoundRequestException;
import ru.otus.hw.services.AuthorService;

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
    public Mono<ResponseEntity<?>> createAuthor(@Valid @RequestBody AuthorDto authorDto,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Mono.just(
                    ResponseEntity.badRequest()
                            .body(bindingResult.getAllErrors())
            );
        }

        return authorService.insert(authorDto)
                .map(savedAuthor -> ResponseEntity.ok().body(savedAuthor));
    }

    @PutMapping("/api/v1/author/{id}")
    public Mono<ResponseEntity<?>> updateAuthor(@PathVariable String id,
                                                @Valid @RequestBody AuthorDto authorDto,
                                                BindingResult bindingResult) {
        if (!id.equals(authorDto.id())) {
            return Mono.error(new BadRequestException("Author id %s mismatch".formatted(id)));
        }

        if (bindingResult.hasErrors()) {
            return Mono.just(
                    ResponseEntity.badRequest()
                            .body(bindingResult.getAllErrors())
            );
        }

        return authorService.update(authorDto)
                .map(updatedAuthor -> ResponseEntity.ok().body(updatedAuthor));
    }

    @DeleteMapping("/api/v1/author/{id}")
    public Mono<ResponseEntity<Void>> deleteAuthorById(@PathVariable String id) {
        return authorService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
