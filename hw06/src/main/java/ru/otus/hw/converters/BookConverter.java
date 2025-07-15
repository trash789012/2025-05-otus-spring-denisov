package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    private final CommentConverter commentConverter;

    public String bookDtoToString(BookDto book) {
        var genresString = book.genres().stream()
                .map(genreConverter::genreDtoToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));

        var commentString = book.comments().stream()
                .map(commentConverter::commentDtoToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));

        return "Id: %d, title: %s, author: {%s}, genres: [%s], comments: [%s]".formatted(
                book.id(),
                book.title(),
                authorConverter.authorDtoToString(book.author()),
                genresString,
                commentString);
    }

    public BookDto bookToBookDto(Book book) {
        if (book == null) {
            return null;
        }

        return new BookDto(
                book.getId(),
                book.getTitle(),
                authorConverter.authorToDto(book.getAuthor()),
                book.getGenres().stream()
                        .map(genreConverter::genreToDto)
                        .toList(),
                book.getComments().stream()
                        .map(commentConverter::commentToDto)
                        .toList()
        );
    }
}
