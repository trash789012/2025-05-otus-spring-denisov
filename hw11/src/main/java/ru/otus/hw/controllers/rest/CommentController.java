package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.BadRequestException;
import ru.otus.hw.services.CommentService;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/api/v1/book/{id}/comment")
    public Flux<CommentDto> getCommentsByBookId(@PathVariable("id") String bookId) {
        return commentService.findByBookId(bookId);
    }

    @PostMapping("/api/v1/book/{id}/comment")
    public Mono<ResponseEntity<CommentDto>> createComment(@PathVariable("id") String bookId,
                                                          @RequestBody CommentDto commentDto) {
        if (bookId == null || commentDto == null) {
            return Mono.error(new BadRequestException("Id is null or empty"));
        }
        if (commentDto.text() == null || commentDto.text().isEmpty()) {
            return Mono.error(new BadRequestException("Comment text is null or empty"));
        }

        return commentService.insert(commentDto)
                .map(savedComment -> ResponseEntity.ok().body(savedComment));
    }

    @DeleteMapping("/api/v1/book/{bookId}/comment/{commentId}")
    public Mono<ResponseEntity<Void>> deleteComment(@PathVariable("bookId") String bookId,
                                                    @PathVariable("commentId") String commentId) {
        return commentService.deleteById(commentId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
