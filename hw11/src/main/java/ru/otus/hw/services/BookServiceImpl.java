package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
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
    @Transactional
    public Mono<BookDto> insert(BookFormDto bookDto) {
        if (bookDto.authorId() == null) {
            return Mono.error(new IllegalArgumentException("Author id must not be null"));
        }
        return save(bookDto);
    }

    @Override
    @Transactional
    public Mono<BookDto> update(BookFormDto bookDto) {
        return save(bookDto);
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(String id) {
        return bookRepository.deleteById(id);
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
        Mono<Book> bookMono;
        if (bookDto.id() == null || bookDto.id().isEmpty()) {
            bookMono = Mono.just(new Book());
        } else {
            bookMono = bookRepository.findById(bookDto.id())
                    .switchIfEmpty(Mono.error(
                            new EntityNotFoundException("Book with id %s not found".formatted(bookDto.id()))
                    ));
        }

        Mono<?> authorMono = authorRepository.findById(bookDto.authorId())
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Author with id %s not found".formatted(bookDto.authorId()))
                ));

        Flux<?> genresFlux = genreRepository.findAllById(genreIds);

        return Mono.zip(bookMono, authorMono, genresFlux.collectList())
                .flatMap(tuple -> {
                    Book book = tuple.getT1();
                    var author = tuple.getT2();
                    var genres = tuple.getT3();

                    if (isEmpty(genres) || genreIds.size() != genres.size()) {
                        return Mono.error(new EntityNotFoundException(
                                "One or all genres with ids %s not found".formatted(genreIds)
                        ));
                    }

                    book.setId(bookDto.id());
                    book.setTitle(bookDto.title());
                    book.setAuthor((ru.otus.hw.models.Author) author);
                    book.setGenres((List<ru.otus.hw.models.Genre>) genres);

                    return Mono.just(book);
                });
    }
}
