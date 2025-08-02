package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.mongo.listener.BookCascadeDeleteMongoListener;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataMongoTest
@DisplayName("Интеграционный тест сервиса книг ")
@Import({BookServiceImpl.class,
        BookConverter.class,
        AuthorConverter.class,
        GenreConverter.class,
        BookCascadeDeleteMongoListener.class})
public class BookServiceImplTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    private final BookService bookService;

    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    @Autowired
    BookServiceImplTest(BookService bookService, AuthorConverter authorConverter,
                        GenreConverter genreConverter) {
        this.bookService = bookService;
        this.authorConverter = authorConverter;
        this.genreConverter = genreConverter;
    }

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();
    }

    @Test
    @DisplayName("должен создавать новую книгу")
    void shouldCreateNewBook() {
        Author author = new Author("Author");
        mongoTemplate.save(author);

        Genre genre1 = new Genre("Genre1");
        Genre genre2 = new Genre("Genre2");

        mongoTemplate.save(genre1);
        mongoTemplate.save(genre2);

        Book book = new Book("Book");
        book.setAuthor(author);
        book.setGenres(List.of(genre1, genre2));

        BookFormDto bookDto = new BookFormDto(
                null,
                book.getTitle(),
                book.getAuthor().getId(),
                book.getGenres().stream().map(Genre::getId).collect(Collectors.toList())
        );

        var insertedBook = bookService.insert(bookDto);

        var savedBook = mongoTemplate.findById(insertedBook.id(), Book.class);

        assertAll(
                () -> assertThat(insertedBook.title()).isEqualTo("Book"),
                () -> assertThat(insertedBook.author().id()).isEqualTo(author.getId()),
                () -> assertThat(insertedBook.genres())
                        .isNotNull()
                        .hasSize(2),
                () -> assertThat(savedBook).isNotNull(),
                () -> assertThat(savedBook.getAuthor().getId()).isEqualTo(author.getId()),
                () -> assertThat(savedBook.getGenres())
                        .extracting(Genre::getId)
                        .containsExactlyInAnyOrder(genre1.getId(), genre2.getId())
        );
    }

    @Test
    @DisplayName("должен обновлять существующую книгу")
    void shouldUpdateExistingBook() {
        Author author = new Author("Author");
        mongoTemplate.save(author);

        Author author2 = new Author("New Author");
        mongoTemplate.save(author2);

        Genre genre1 = new Genre("Genre1");
        mongoTemplate.save(genre1);

        Genre genre2 = new Genre("New Genre");
        mongoTemplate.save(genre2);

        Book book = new Book("Book1");
        book.setAuthor(author);
        book.setGenres(List.of(genre1));
        mongoTemplate.save(book);

        BookFormDto bookDto = new BookFormDto(
                book.getId(),
                "New Title",
                author2.getId(),
                List.of(genre2.getId())
        );

        var updatedBook = bookService.update(bookDto);

        var savedBook = mongoTemplate.findById(book.getId(), Book.class);

        assertAll(
                () -> assertThat(updatedBook.title()).isEqualTo("New Title"),
                () -> assertThat(updatedBook.author().id()).isEqualTo(author2.getId()),
                () -> assertThat(updatedBook.genres())
                        .isNotNull()
                        .hasSize(1)
                        .extracting(GenreDto::name)
                        .containsExactly("New Genre"),
                () -> assertThat(savedBook).isNotNull().extracting(Book::getTitle)
                        .isEqualTo("New Title"),
                () -> {
                    assert (savedBook != null ? savedBook.getAuthor() : null) != null;
                    assertThat(savedBook.getAuthor().getId()).isEqualTo(author2.getId());
                },
                () -> {
                    assert (savedBook != null ? savedBook.getGenres() : null) != null;
                    assertThat(updatedBook.genres())
                            .hasSize(1)
                            .extracting(GenreDto::id)
                            .containsExactly(genre2.getId());
                }
        );
    }

    @Test
    @DisplayName("должен находить книгу по ее id")
    public void shouldFindBookById() {
        Author author = new Author("Author");
        mongoTemplate.save(author);

        Genre genre1 = new Genre("Genre1");
        Genre genre2 = new Genre("Genre2");

        mongoTemplate.save(genre1);
        mongoTemplate.save(genre2);

        Book book = new Book("Book");
        book.setAuthor(author);
        book.setGenres(List.of(genre1, genre2));
        mongoTemplate.save(book);

        Optional<BookDto> savedBook = bookService.findById(book.getId());

        assertThat(savedBook).isPresent();
        assertAll(
                () -> assertThat(savedBook.get())
                        .extracting(BookDto::title, dto -> dto.author().fullName())
                        .containsExactlyInAnyOrder("Book", "Author"),
                () -> assertThat(savedBook.get().genres())
                        .isNotNull()
                        .hasSize(2)
                        .extracting(GenreDto::name)
                        .containsExactlyInAnyOrder("Genre1", "Genre2")
        );
    }

    @Test
    @DisplayName("должен возвращать все книги")
    public void shouldFindAllBooks() {
        Author author = new Author("Author");
        mongoTemplate.save(author);

        Genre genre1 = new Genre("Genre1");
        Genre genre2 = new Genre("Genre2");

        mongoTemplate.save(genre1);
        mongoTemplate.save(genre2);

        Book book1 = new Book("Book1");
        book1.setAuthor(author);
        book1.setGenres(List.of(genre1, genre2));
        mongoTemplate.save(book1);

        Book book2 = new Book("Book2");
        book2.setAuthor(author);
        book2.setGenres(List.of(genre1, genre2));
        mongoTemplate.save(book2);

        var allBooks = bookService.findAll();

        assertAll(
                () -> assertThat(allBooks).hasSize(2),
                () -> assertThat(allBooks)
                        .extracting(BookDto::title)
                        .containsExactlyInAnyOrder("Book1", "Book2"),
                () -> assertThat(allBooks)
                        .extracting(dto -> dto.author().fullName())
                        .containsOnly("Author"),
                () -> assertThat(allBooks)
                        .flatExtracting(BookDto::genres)
                        .isNotNull()
                        .hasSize(4)
                        .extracting(GenreDto::name)
                        .containsOnly("Genre1", "Genre2")

        );
    }

    @Test
    @DisplayName("должен удалять книгу и каскадом комментарии")
    void shouldDeleteExistingBook() {
        Author author = new Author("Author");
        mongoTemplate.save(author);

        Book book = new Book("Book");
        book.setAuthor(author);
        book.setGenres(List.of());
        mongoTemplate.save(book);

        Comment comment1 = new Comment("Comment1");
        comment1.setBook(book);
        Comment comment2 = new Comment("Comment2");
        comment2.setBook(book);
        mongoTemplate.save(comment1);
        mongoTemplate.save(comment2);

        bookService.deleteById(book.getId());

        var notExistsBook = mongoTemplate.findById(book.getId(), Book.class);
        var notExistsComment1 = mongoTemplate.findById(comment1.getId(), Comment.class);
        var notExistsComment2 = mongoTemplate.findById(comment2.getId(), Comment.class);

        assertThat(notExistsBook).isNull();
        assertThat(notExistsComment1).isNull();
        assertThat(notExistsComment2).isNull();
    }
}
