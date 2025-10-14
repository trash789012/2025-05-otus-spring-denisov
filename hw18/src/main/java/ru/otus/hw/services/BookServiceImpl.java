package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookConverter bookConverter;

    @Override
    public Optional<BookDto> findById(String id) {
        return bookRepository.findById(id)
                .map(bookConverter::bookToBookDto);
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookConverter::bookToBookDto)
                .toList();
    }

    @Override
    @Transactional
    public BookDto insert(BookFormDto bookDto) {
        if (bookDto.authorId() == null) {
            throw new IllegalArgumentException("Book id must not be null");
        }
        return save(bookDto);
    }

    @Override
    @Transactional
    public BookDto update(BookFormDto bookDto) {
        return save(bookDto);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        bookRepository.deleteById(id);
    }

    private BookDto save(BookFormDto bookFormDto) {
        if (isEmpty(bookFormDto.genreIds())) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        Book book;
        book = prepareBook(bookFormDto, bookFormDto.genreIds());
        return bookConverter.bookToBookDto(bookRepository.save(book));
    }

    private Book prepareBook(BookFormDto bookDto, List<String> genresIds) {
        Book book;
        if (bookDto.id() == null || bookDto.id().isEmpty()) {
            book = new Book();
        } else {
            book = bookRepository.findById(bookDto.id()).orElseThrow(
                    () -> new EntityNotFoundException("Book with id %s not found".formatted(bookDto.id()))
            );
        }
        var author = authorRepository.findById(bookDto.authorId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Author with id %s not found".formatted(bookDto.authorId())));
        var genres = genreRepository.findAllById(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }
        book.setId(bookDto.id());
        book.setTitle(bookDto.title());
        book.setAuthor(author);
        book.setGenres(genres);
        return book;
    }
}
