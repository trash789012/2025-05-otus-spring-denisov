package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataMongoTest
@DisplayName("Интеграционный тест сервиса авторов ")
@Import({AuthorServiceImpl.class, AuthorConverter.class})
public class AuthorServiceImplTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    private AuthorService authorService;

    @Autowired
    AuthorServiceImplTest(AuthorService authorService) {
        this.authorService = authorService;
    }

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();
    }

    @Test
    @DisplayName("должен возвращать всех авторов")
    void shouldFindAllAuthors() {
        Author author1 = new Author("Author1");
        Author author2 = new Author("Author2");

        mongoTemplate.save(author1);
        mongoTemplate.save(author2);

        var authors = authorService.findAll();

        assertAll(
                () -> assertThat(authors).isNotNull(),
                () -> assertThat(authors).hasSize(2),
                () -> assertThat(authors.get(0).fullName())
                        .isEqualTo("Author1"),
                () -> assertThat(authors.get(1).fullName())
                        .isEqualTo("Author2"),
                () -> assertThat(authors.get(0))
                        .extracting(AuthorDto::id)
                        .isEqualTo(author1.getId()),
                () -> assertThat(authors.get(1))
                        .extracting(AuthorDto::id)
                        .isEqualTo(author2.getId())

        );
    }

    @Test
    @DisplayName("должен находить автора по id")
    void shouldFindAuthorById() {
        Author author1 = new Author("Author1");
        mongoTemplate.save(author1);

        Optional<AuthorDto> savedAuthor = authorService.findById(author1.getId());

        assertThat(savedAuthor).isPresent();
        assertThat(savedAuthor.get().id()).isEqualTo(author1.getId());
        assertThat(savedAuthor.get().fullName()).isEqualTo(author1.getFullName());
    }

    @Test
    @DisplayName("должен корректно реагировать на не существующий id")
    void shouldReturnEmptyForInvalidId() {
        String invalidId = "invalidId";

        var author = authorService.findById(invalidId);

        assertThat(author).isEmpty();
    }

    @Test
    @DisplayName("должен создавать автора")
    void shouldCreateNewAuthor() {
        var author = authorService.insert(new AuthorDto(null, "Author1"));

        var savedAuthor = mongoTemplate.findById(author.id(), Author.class);

        assertThat(savedAuthor).isNotNull();
        assertThat(savedAuthor.getFullName())
                .isEqualTo(author.fullName())
                .isEqualTo("Author1");
        assertThat(author.id()).isEqualTo(savedAuthor.getId());
    }

    @Test
    @DisplayName("должен обновлять автора")
    void shouldUpdateAuthor() {
        var author = mongoTemplate.save(new Author("Author1"));
        var updatedAuthor = authorService.update(new AuthorDto(author.getId(), "New Author"));
        var savedAuthor = mongoTemplate.findById(author.getId(), Author.class);

        assertThat(savedAuthor).isNotNull();
        assertThat(updatedAuthor).isNotNull();
        assertThat(updatedAuthor.fullName()).isEqualTo("New Author");
        assertThat(updatedAuthor.fullName()).isEqualTo(savedAuthor.getFullName());
        assertThat(updatedAuthor.id()).isEqualTo(savedAuthor.getId());
    }

    @Test
    @DisplayName("должен удалять автора")
    void shouldDeleteAuthor() {
        var author1 = mongoTemplate.save(new Author("Author1"));
        mongoTemplate.save(new Author("Author2"));

        authorService.deleteById(author1.getId());

        var allAuthors = mongoTemplate.findAll(Author.class);
        var deletedAuthor = mongoTemplate.findById(author1.getId(), Author.class);

        assertThat(allAuthors).hasSize(1);
        assertThat(deletedAuthor).isNull();
    }

}
