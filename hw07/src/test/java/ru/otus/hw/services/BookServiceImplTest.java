package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Интеграционный тест сервиса книг")
@DataJpaTest
@Import({BookServiceImpl.class,
        BookConverter.class,
        AuthorConverter.class,
        GenreConverter.class,
        CommentConverter.class})
@Transactional(propagation = Propagation.NEVER)
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
        assertThat(book.id()).isGreaterThan(0).isEqualTo(FIRST_BOOK_ID);
        assertThat(book.title()).isNotBlank().isEqualTo("BookTitle_1");

        assertThat(book.author()).isNotNull();
        assertThat(book.author().fullName()).isNotBlank().isEqualTo("Author_1");

        assertThat(book.genres()).isNotEmpty().hasSize(2);
        assertThat(book.genres()).satisfies(genreDto -> {
            assertThat(genreDto.get(0).id()).isEqualTo(1L);
            assertThat(genreDto.get(0).name()).isNotBlank().isEqualTo("Genre_1");
            assertThat(genreDto.get(1).id()).isEqualTo(2L);
            assertThat(genreDto.get(1).name()).isNotBlank().isEqualTo("Genre_2");
        });
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
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    public void deleteBookByIdShouldNotThrowLazyException() {
        var optionalBook = bookService.findById(FIRST_BOOK_ID);

        assertThat(optionalBook).isPresent();

        bookService.deleteById(FIRST_BOOK_ID);

        assertThat(bookService.findById(FIRST_BOOK_ID)).isEmpty();
    }

    @Test
    @DisplayName("Должен создавать новую книгу")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    public void insertBookShouldNotThrowLazyException() {

        String title = "New Book Title";
        long authorId = 1L;
        Set<Long> genres = Set.of(1L, 2L);

        AuthorDto authorDto = new AuthorDto(authorId, null);
        List<GenreDto> genresDto = new ArrayList<>();

        genres.stream().forEach(genresId -> {
            genresDto.add(new GenreDto(genresId, null));
        });

        BookDto bookDto = new BookDto(0, title, authorDto, genresDto);

        var bookInserted = bookService.insert(bookDto);

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
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void updateBookShouldNotThrowLazyException() {

        String title = "New Book Title";
        long authorId = 3L;
        Set<Long> genres = Set.of(5L, 6L);

        AuthorDto authorDto = new AuthorDto(authorId, null);
        List<GenreDto> genresDto = new ArrayList<>();

        genres.stream().forEach(genresId -> {
            genresDto.add(new GenreDto(genresId, null));
        });

        BookDto bookDto = new BookDto(FIRST_BOOK_ID, title, authorDto, genresDto);

        var bookUpdated = bookService.update(bookDto);

        assertThat(bookUpdated.id()).isEqualTo(FIRST_BOOK_ID);
        assertThat(bookUpdated.title()).isEqualTo(title);
        assertThat(bookUpdated.author().id()).isEqualTo(authorId);
        assertThat(bookUpdated.genres()).hasSize(2)
                .extracting(GenreDto::id)
                .containsExactlyInAnyOrderElementsOf(genres);

    }

    @Test
    @DisplayName("Должен бросать исключение при создании книги с несуществующим автором")
    void insertBookShoulThrowWithNoExistsAuthor() {
        AuthorDto authorDto = new AuthorDto(99L, null);
        List<GenreDto> genresDto = List.of(
                new GenreDto(1L, null),
                new GenreDto(2L, null)
        );

        BookDto bookDto = new BookDto(0, "Title", authorDto, genresDto);

        assertThatThrownBy(() ->
                bookService.insert(bookDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Author with id 99 not found");

    }

    @Test
    @DisplayName("Должен бросать исключение при создании книги с несуществующим жанром")
    void insertBookShouldThrowWithNoExistsGenre() {

        AuthorDto authorDto = new AuthorDto(1L, null);
        List<GenreDto> genresDto = List.of(new GenreDto(99L, null));

        BookDto bookDto = new BookDto(FIRST_BOOK_ID, "Title New", authorDto, genresDto);

        assertThatThrownBy(() -> {
            bookService.insert(bookDto);
        })
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("One or all genres with ids [99] not found");
    }
}
