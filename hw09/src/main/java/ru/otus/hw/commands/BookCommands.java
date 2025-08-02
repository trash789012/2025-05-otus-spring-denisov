package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class BookCommands {

    private final BookService bookService;

    private final BookConverter bookConverter;

    @ShellMethod(value = "Find all books", key = "ab")
    public String findAllBooks() {
        return bookService.findAll().stream()
                .map(bookConverter::bookDtoToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find book by id", key = "bbid")
    public String findBookById(long id) {
        return bookService.findById(String.valueOf(id))
                .map(bookConverter::bookDtoToString)
                .orElse("Book with id %d not found".formatted(id));
    }

    // bins newBook 1 1,6
    @ShellMethod(value = "Insert book", key = "bins")
    public String insertBook(String title, String authorId, List<String> genresIds) {

        List<GenreDto> genresDto = new ArrayList<>();
        genresIds.forEach(genresId -> genresDto.add(new GenreDto(genresId, null)));

        BookFormDto bookDto = new BookFormDto(
                null,
                title,
                authorId,
                genresIds
        );

        var savedBook = bookService.insert(bookDto);
        return bookConverter.bookDtoToString(savedBook);
    }

    // bupd 4 editedBook 3 2,5
    @ShellMethod(value = "Update book", key = "bupd")
    public String updateBook(String id, String title, String authorId, List<String> genresIds) {
        BookFormDto bookDto = new BookFormDto(id, title, authorId, genresIds);

        var savedBook = bookService.update(bookDto);
        return bookConverter.bookDtoToString(savedBook);
    }

    // bdel 4
    @ShellMethod(value = "Delete book by id", key = "bdel")
    public void deleteBook(String id) {
        bookService.deleteById(id);
    }
}
