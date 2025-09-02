package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookConverter bookConverter;
    private final CommentRepository commentRepository;

    @Override
    public Mono<BookDto> findById(String id) {
        return bookRepository.findById(id)
                .map(bookConverter::bookToBookDto);
    }

    @Override
    public Flux<BookDto> findAll() {
        return bookRepository.findAll()
                .map(bookConverter::bookToBookDto);
    }

    @Override
    public Mono<BookDto> insert(BookFormDto bookDto) {
        if (bookDto.authorId() == null) {
            return Mono.error(new IllegalArgumentException("Author id must not be null"));
        }
        return save(bookDto);
    }

    @Override
    public Mono<BookDto> update(BookFormDto bookDto) {
        return save(bookDto);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.when(
                commentRepository.deleteAllByBookId(id),
                bookRepository.deleteById(id)
        ).then();
    }

    private Mono<BookDto> save(BookFormDto bookFormDto) {
        if (isEmpty(bookFormDto.genreIds())) {
            return Mono.error(new IllegalArgumentException("Genres ids must not be null"));
        }

        return prepareBook(bookFormDto, bookFormDto.genreIds())
                .flatMap(bookRepository::save)
                .map(bookConverter::bookToBookDto);
    }

    private Mono<Book> prepareBook(BookFormDto bookDto, List<String> genreIds) {
        return getBook(bookDto)
                .zipWith(getAuthor(bookDto))
                .zipWith(getGenres(genreIds))
                .flatMap(tuple -> assembleBook(tuple.getT1().getT1(), tuple.getT1().getT2(), tuple.getT2(), bookDto));
    }

    private Mono<Book> getBook(BookFormDto bookDto) {
        if (bookDto.id() == null || bookDto.id().isEmpty()) {
            return Mono.just(new Book());
        }
        return bookRepository.findById(bookDto.id())
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Book with id %s not found".formatted(bookDto.id()))
                ));
    }

    private Mono<ru.otus.hw.models.Author> getAuthor(BookFormDto bookDto) {
        return authorRepository.findById(bookDto.authorId())
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Author with id %s not found".formatted(bookDto.authorId()))
                ));
    }

    private Mono<List<ru.otus.hw.models.Genre>> getGenres(List<String> genreIds) {
        return genreRepository.findAllById(genreIds)
                .collectList()
                .flatMap(genres -> {
                    if (isEmpty(genres) || genreIds.size() != genres.size()) {
                        return Mono.error(new EntityNotFoundException(
                                "One or all genres with ids %s not found".formatted(genreIds)
                        ));
                    }
                    return Mono.just(genres);
                });
    }

    private Mono<Book> assembleBook(Book book, ru.otus.hw.models.Author author,
                                    List<ru.otus.hw.models.Genre> genres, BookFormDto bookDto) {
        book.setId(bookDto.id());
        book.setTitle(bookDto.title());
        book.setAuthor(author);
        book.setGenres(genres);
        return Mono.just(book);
    }

}
