package ru.otus.hw.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.BadRequestException;
import ru.otus.hw.services.CommentService;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/api/v1/book/{id}/comment")
    public Flux<CommentDto> getCommentsByBookId(@PathVariable("id") String bookId) {
        return commentService.findByBookId(bookId);
    }

    @PostMapping("/api/v1/book/{id}/comment")
    public Mono<ResponseEntity<Object>> createComment(@PathVariable("id") String bookId,
                                                      @Valid @RequestBody Mono<CommentDto> commentDtoMono) {
        if (bookId == null || bookId.isBlank()) {
            return Mono.error(new BadRequestException("Book id is null or empty"));
        }

        return commentDtoMono
                .flatMap(commentDto -> commentService.insert(commentDto)
//                        .switchIfEmpty(Mono.error(new BadRequestException("Failed to save comment")))
                        .map(savedComment -> ResponseEntity.ok().<Object>body(savedComment))
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

    @DeleteMapping("/api/v1/book/{bookId}/comment/{commentId}")
    public Mono<ResponseEntity<Void>> deleteComment(@PathVariable("bookId") String bookId,
                                                    @PathVariable("commentId") String commentId) {
        return commentService.deleteById(commentId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
