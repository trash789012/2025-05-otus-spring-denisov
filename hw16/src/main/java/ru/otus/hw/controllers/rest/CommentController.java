package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.BadRequestException;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/api/v1/book/{id}/comment")
    public List<CommentDto> getCommentsByBookId(@PathVariable("id") Long bookId) {
        return commentService.findByBookId(bookId);
    }

    @PostMapping("/api/v1/book/{id}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable("id") Long bookId,
                                    @RequestBody CommentDto commentDto) {
        if (bookId == null || commentDto == null) {
            throw new BadRequestException("Id is null or empty");
        }
        if (commentDto.text() == null || commentDto.text().isEmpty()) {
            throw new BadRequestException("Text is null or empty");
        }
        return commentService.insert(commentDto);
    }

    @DeleteMapping("/api/v1/book/{bookId}/comment/{commentId}")
    public void deleteComment(@PathVariable("bookId") Long bookId,
                              @PathVariable("commentId") Long commentId) {
        commentService.deleteById(commentId);
    }
}
