package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentComands {

    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @ShellMethod(key = "cbid", value = "Find comment by id")
    public String findCommentById(String id) {
        return commentService.findById(id)
                .map(commentConverter::commentDtoToString)
                .orElse("Comment with id %s not found".formatted(id));
    }

    @ShellMethod(key = "cbbid", value = "Find comments by book id")
    public String findCommentsByBookId(String bookId) {
        return commentService.findByBookId(bookId)
                .stream()
                .map(commentConverter::commentDtoToString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod(key = "ic", value = "Insert comment")
    public String insertComment(String text, String bookId) {
        var comment = commentService.insert(new CommentDto(null, text, bookId));
        return commentConverter.commentDtoToString(comment);
    }

    @ShellMethod(key = "uc", value = "Update comment")
    public String updateComment(String id, String text) {
        var comment = commentService.update(new CommentDto(id, text, null));
        return commentConverter.commentDtoToString(comment);
    }

    @ShellMethod(key = "dc", value = "Delete comment")
    public void deleteComment(String id) {
        commentService.deleteById(id);
    }

}
