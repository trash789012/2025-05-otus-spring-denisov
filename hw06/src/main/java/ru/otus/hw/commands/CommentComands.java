package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentComands {

    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @ShellMethod(key = "cbid", value = "Find comment by id")
    public String findCommentById(long id) {
        return commentService.findById(id)
                .map(commentConverter::commentDtoToString)
                .orElse("Comment with id %d not found".formatted(id));
    }

    @ShellMethod(key = "cbbid", value = "Find comments by book id")
    public String findCommentsByBookId(long bookId) {
        return commentService.findByBookId(bookId)
                .stream()
                .map(commentConverter::commentDtoToString)
                .collect(Collectors.joining("\n"));
    }

}
