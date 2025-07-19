package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookCondensedDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

@Component
@RequiredArgsConstructor
public class CommentConverter {

    private final BookCondensedConverter bookConverter;

    private final AuthorConverter authorConverter;

    public String commentDtoToString(CommentDto comment) {
        return "Id: %d, Text: %s, book: %s".formatted(
                comment.id(),
                comment.text(),
                bookConverter.bookCondensedDtoToString(comment.book()));
    }

    public CommentDto commentToDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        return new CommentDto(
                comment.getId(),
                comment.getText(),
                new BookCondensedDto(
                        comment.getBook().getId(),
                        comment.getBook().getTitle(),
                        authorConverter.authorToDto(comment.getBook().getAuthor())
                )
        );
    }

}
