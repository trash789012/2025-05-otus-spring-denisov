package ru.otus.hw.repositories;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Репозиторий JPA для работы с книгами ")
public class JpaBookRepositoryTest {

    public static final long FIRST_BOOK_ID = 1L;

    public static final long SECONDARY_AUTHOR_ID = 2L;

    public static final long LAST_GENRE_ID = 6L;

    @Autowired
    private BookRepository jpaBookRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName(" должен корректно находить книгу по id с полной информацией")
    void shouldCorrectFindExpectedBookById() {
        val expectedBook = em.find(Book.class, FIRST_BOOK_ID);
        int genresSize = expectedBook.getGenres().size();
        int commentsSize = expectedBook.getComments().size();
        em.detach(expectedBook);

        val actualBook = jpaBookRepository.findById(FIRST_BOOK_ID);

        assertThat(actualBook).isPresent().get()
                .satisfies(book -> {
                    assertThat(book.getId()).isEqualTo(expectedBook.getId());
                    assertThat(book.getTitle()).isEqualTo(expectedBook.getTitle());

                    assertThat(book.getAuthor())
                            .isNotNull()
                            .extracting(Author::getId, Author::getFullName)
                            .containsExactly(
                                    expectedBook.getAuthor().getId(),
                                    expectedBook.getAuthor().getFullName()
                            );

                    assertThat(book.getGenres())
                            .isNotNull()
                            .hasSize(genresSize);

                    assertThat(book.getComments())
                            .isNotNull()
                            .hasSize(commentsSize);
                });
    }

    @Test
    @DisplayName(" должен корректно обновлять книгу")
    void shouldCorrectUpdateExistsBook() {
        Author newAuthor = em.find(Author.class, SECONDARY_AUTHOR_ID);
        Genre newGenre = em.find(Genre.class, LAST_GENRE_ID);

        Book persistentBook = em.find(Book.class, FIRST_BOOK_ID);

        val genres = persistentBook.getGenres();
        persistentBook.setTitle("New Title");
        persistentBook.setAuthor(newAuthor);
        persistentBook.getGenres().add(newGenre);
        int genreSize = persistentBook.getGenres().size();
        em.detach(persistentBook);

        jpaBookRepository.save(persistentBook);
        em.flush();
        em.clear();

        val actualBook = em.find(Book.class, FIRST_BOOK_ID);
        assertThat(actualBook).isNotNull()
                .satisfies(book -> {
                   assertThat(book.getId()).isEqualTo(persistentBook.getId());
                   assertThat(book.getTitle()).isEqualTo("New Title");
                   assertThat(book.getAuthor().getId()).isNotNull()
                           .isEqualTo(newAuthor.getId());
                   assertThat(book.getGenres())
                           .isNotNull()
                           .hasSize(genreSize);
                });
    }

    @Test
    @DisplayName(" должен корректно добавлять новую книгу")
    void shouldCorrectInsertNewBook() {
        Author author = em.find(Author.class, SECONDARY_AUTHOR_ID);
        Genre genre = em.find(Genre.class, LAST_GENRE_ID);
        Book transientBook = new Book(0, "Inserted Book", author, List.of(genre), List.of());

        Book savedBook = jpaBookRepository.save(transientBook);
        em.flush();
        em.clear();

        Book actualBook = em.find(Book.class, savedBook.getId());

        assertThat(actualBook).isNotNull()
                .satisfies(book -> {
                    assertThat(book.getTitle()).contains("Inserted Book");

                    //автора проверим
                    assertThat(book.getAuthor().getId() == author.getId());
                    assertThat(book.getAuthor().getFullName()).isEqualTo(author.getFullName());

                    //жанры
                    assertThat(book.getGenres())
                            .hasSize(1)
                            .first()
                            .extracting(Genre::getId, Genre::getName)
                            .containsExactly(
                                    genre.getId(),
                                    genre.getName()
                            );

                });
    }

    @Test
    @DisplayName(" должен корректно удалять книгу по id")
    void shouldCorrectDeleteExistingBook() {
        val book = em.find(Book.class, FIRST_BOOK_ID);
        assertThat(book).isNotNull();
        em.detach(book);

        jpaBookRepository.deleteById(FIRST_BOOK_ID);
        em.flush();
        em.clear();

        val deletedBook = em.find(Book.class, FIRST_BOOK_ID);
        assertThat(deletedBook).isNull();
    }
}
