package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
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

    private final BookConverter bookConverter;

    private final AclServiceWrapperService aclService;

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
    @Transactional
    public BookDto insert(BookFormDto bookDto) {
        if (bookDto.authorId() == 0) {
            throw new IllegalArgumentException("Book id must not be null");
        }
        return save(bookDto);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasPermission(#bookDto.id(), 'ru.otus.hw.models.Book', 'WRITE')")
    public BookDto update(BookFormDto bookDto) {
        return save(bookDto);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasPermission(#id, 'ru.otus.hw.models.Book', 'DELETE')")
    public void deleteById(@P("id") long id) {
        bookRepository.deleteById(id);
    }

    private BookDto save(BookFormDto bookFormDto) {
        boolean isCreate = bookFormDto.id() == 0;

        if (isEmpty(bookFormDto.genreIds())) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        Book book;
        book = prepareBook(bookFormDto, bookFormDto.genreIds());

        var savedBook = bookRepository.save(book);
        if (isCreate) {
            aclService.createPermission(savedBook, BasePermission.WRITE);
            aclService.createPermission(savedBook, BasePermission.DELETE);
            aclService.createAdminPermission(savedBook);
        }
        return bookConverter.bookToBookDto(savedBook);
    }

    private Book prepareBook(BookFormDto bookDto, List<Long> genresIds) {
        Book book;
        if (bookDto.id() == 0) {
            book = new Book();
        } else {
            book = bookRepository.findById(bookDto.id()).orElseThrow(
                    () -> new EntityNotFoundException("Book with id %s not found".formatted(bookDto.id()))
            );
        }
        var author = authorRepository.findById(bookDto.authorId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Author with id %s not found".formatted(bookDto.authorId())));
        List<Genre> genres = genreRepository.findAllById(genresIds);
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
