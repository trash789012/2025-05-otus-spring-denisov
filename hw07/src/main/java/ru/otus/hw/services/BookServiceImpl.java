package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final BookConverter bookConverter;

    @Override
    @Transactional(readOnly = true)
    public Optional<BookDto> findById(long id) {
        return bookRepository.findById(id)
                .map(bookConverter::bookToBookDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookConverter::bookToBookDto)
                .toList();
    }

    @Override
    public BookDto insert(BookDto bookDto) {
        return save(bookDto);
    }

    @Override
    @Transactional
    public BookDto update(BookDto bookDto) {
        return save(bookDto);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    private BookDto save(BookDto bookDto) {
        List<Long> genresIds = bookDto.genres().stream().map(GenreDto::id).toList();
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }
        Book book;
        if (bookDto.id() == 0) {
            book = new Book();
        } else {
            book = bookRepository.findById(bookDto.id()).orElseThrow(
                    () -> new EntityNotFoundException("Book with id %d not found".formatted(bookDto.id()))
            );
        }
        var author = authorRepository.findById(bookDto.author().id()).orElseThrow(() ->
                        new EntityNotFoundException("Author with id %d not found".formatted(bookDto.author().id())));
        var genres = genreRepository.findAllById(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }
        book.setId(bookDto.id());
        book.setTitle(bookDto.title());
        book.setAuthor(author);
        book.setGenres(genres);
        return bookConverter.bookToBookDto(bookRepository.save(book));
    }
}
