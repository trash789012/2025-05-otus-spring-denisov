package ru.otus.hw.rest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/api/v1/book/{id}/comment")
    public List<CommentDto> getCommentsByBookId(@PathVariable("id") String bookId) {
        return commentService.findByBookId(bookId);
    }

    @PostMapping("/api/v1/book/{id}/comment")
    public ResponseEntity<CommentDto> createComment(@PathVariable("id") String bookId, @RequestBody CommentDto commentDto) {
        var savedComment = commentService.insert(commentDto);
        return ResponseEntity.ok().body(savedComment);
    }

    @DeleteMapping("/api/v1/book/{bookId}/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("bookId") String bookId, @PathVariable("commentId") String commentId) {
        commentService.deleteById(commentId);
        return ResponseEntity.noContent().build();
    }
}
