package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.h2.Book;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    public String bookDtoToString(BookDto book) {
        var genresString = book.genres().stream()
                .map(genreConverter::genreDtoToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));

        return "Id: %s, title: %s, author: {%s}, genres: [%s]".formatted(
                book.id(),
                book.title(),
                authorConverter.authorDtoToString(book.author()),
                genresString
        );
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
                        .toList()
        );
    }

    public BookFormDto bookDtoToBookFormDto(BookDto book) {
        if (book == null) {
            return null;
        }

        return new BookFormDto(
                book.id(),
                book.title(),
                (book.author() != null) ? book.author().id() : null,
                (book.genres() != null) ?
                        book.genres().stream()
                                .map(GenreDto::id)
                                .collect(Collectors.toList()) : null
        );
    }
}
