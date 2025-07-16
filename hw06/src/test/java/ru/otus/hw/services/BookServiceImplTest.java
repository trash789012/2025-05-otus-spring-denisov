package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.converters.*;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Интеграционный тест сервиса книг")
@DataJpaTest
@Import({BookServiceImpl.class,
        JpaAuthorRepository.class,
        JpaGenreRepository.class,
        JpaBookRepository.class,
        JpaCommentRepository.class,
        BookConverter.class,
        AuthorConverter.class,
        GenreConverter.class,
        CommentConverter.class,
        BookCondensedConverter.class})
public class BookServiceImplTest {

    public static final long FIRST_BOOK_ID = 1L;

    private final BookService bookService;

    @Autowired
    public BookServiceImplTest(BookService bookService) {
        this.bookService = bookService;
    }

    @Test
    @DisplayName("Должен находить книгу по id и всю информацию")
    public void findBookByIdShouldNotThrowLazyException() {
        var optionalBook = bookService.findById(FIRST_BOOK_ID);
        assertThat(optionalBook).isPresent();

        var book = optionalBook.get();

        //обращаемся ко всем элементам
        assertThat(book.id()).isGreaterThan(0);
        assertThat(book.title()).isNotBlank();

        assertThat(book.author()).isNotNull();
        assertThat(book.author().fullName()).isNotBlank();

        assertThat(book.genres()).isNotEmpty();
        assertThat(book.genres()).allSatisfy(genreDto -> {
            assertThat(genreDto.id()).isGreaterThan(0);
            assertThat(genreDto.name()).isNotBlank();
        });

        assertThat(book.comments()).isNotNull();
    }

    @Test
    @DisplayName("Должен находить список всех книг и всю информацию")
    public void findAllBookShouldNotThrowLazyException() {
        var books = bookService.findAll();

        assertThat(books).isNotNull();
        assertThat(books).allSatisfy(bookDto -> {
            //книга
            assertThat(bookDto.id()).isGreaterThan(0);
            assertThat(bookDto.title()).isNotBlank();

            //автор
            assertThat(bookDto.author()).isNotNull();
            assertThat(bookDto.author().fullName()).isNotBlank();

            //жанры
            assertThat(bookDto.genres()).isNotEmpty()
                    .allSatisfy(genreDto -> {
                        assertThat(genreDto.id()).isGreaterThan(0);
                        assertThat(genreDto.name()).isNotBlank();
                    });
        });
    }

    @Test
    @DisplayName("Должен удалять книгу по id")
    public void deleteBookByIdShouldNotThrowLazyException() {
        var optionalBook = bookService.findById(FIRST_BOOK_ID);

        assertThat(optionalBook).isPresent();

        bookService.deleteById(FIRST_BOOK_ID);

        assertThat(bookService.findById(FIRST_BOOK_ID)).isEmpty();
    }

    @Test
    @DisplayName("Должен создавать новую книгу")
    public void insertBookShouldNotThrowLazyException() {

        String title = "New Book Title";
        long authorId = 1L;
        Set<Long> genres = Set.of(1L, 2L);

        var bookInserted = bookService.insert(title, authorId, genres);

        assertThat(bookInserted).isNotNull();
        assertThat(bookInserted.id()).isGreaterThan(0);
        assertThat(bookInserted.title()).isEqualTo(title);
        assertThat(bookInserted.author()).isNotNull();
        assertThat(bookInserted.genres()).hasSize(2)
                .extracting(GenreDto::id)
                .containsExactlyInAnyOrderElementsOf(genres);
    }

    @Test
    @DisplayName("Должен обновлять книгу")
    void updateBookShouldNotThrowLazyException() {

        String title = "New Book Title";
        long authorId = 3L;
        Set<Long> genres = Set.of(5L, 6L);

        var bookUpdated = bookService.update(FIRST_BOOK_ID, title, authorId, genres);

        assertThat(bookUpdated.id()).isEqualTo(FIRST_BOOK_ID);
        assertThat(bookUpdated.title()).isEqualTo(title);
        assertThat(bookUpdated.author().id()).isEqualTo(authorId);
        assertThat(bookUpdated.genres()).hasSize(2)
                .extracting(GenreDto::id)
                .containsExactlyInAnyOrderElementsOf(genres);

    }
}
