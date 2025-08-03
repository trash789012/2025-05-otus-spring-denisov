package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@DisplayName("Интеграционный тест сервиса жанров ")
@Import({GenreServiceImpl.class, GenreConverter.class, TestMongoConfig.class})
public class GenreServiceImplTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    private final GenreService genreService;

    @Autowired
    GenreServiceImplTest(GenreService genreService) {
        this.genreService = genreService;
    }

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();
    }

    @Test
    @DisplayName("должен возвращать все жанры")
    public void shouldFindAllGenres() {
        Genre genre1 = new Genre("Lyrics");
        Genre genre2 = new Genre("Science");

        mongoTemplate.save(genre1);
        mongoTemplate.save(genre2);

        var savedGenres = genreService.findAll();

        assertThat(savedGenres).isNotNull().hasSize(2);
        assertThat(savedGenres)
                .extracting(GenreDto::id)
                .containsExactlyInAnyOrder(genre1.getId(), genre2.getId());
        assertThat(savedGenres)
                .extracting(GenreDto::name)
                .containsExactlyInAnyOrder(genre1.getName(), genre2.getName());
    }

    @Test
    @DisplayName("должен возвращать список жанров по ids")
    public void shouldFindGenreByIds() {
        Genre genre1 = new Genre("Lyrics");
        Genre genre2 = new Genre("Science");
        mongoTemplate.save(genre1);
        mongoTemplate.save(genre2);

        var savedGenres = genreService.findByIds(Set.of(
                genre1.getId(),
                genre2.getId()
        ));

        assertThat(savedGenres).isNotNull().hasSize(2);
        assertThat(savedGenres)
                .extracting(GenreDto::id)
                .containsExactlyInAnyOrder(genre1.getId(), genre2.getId());
        assertThat(savedGenres)
                .extracting(GenreDto::name)
                .containsExactlyInAnyOrder(genre1.getName(), genre2.getName());
    }

    @Test
    @DisplayName("должен корректно реагировать на несуществующий id")
    public void shouldReturnEmptyForInvalidId() {
        String id = "invalidId";

        var genre = genreService.findByIds(Set.of(id));

        assertThat(genre).isEmpty();
    }

    @Test
    @DisplayName("должен возвращать только те жанры, которые существуют")
    public void shouldFindOnlyExistsGenres() {
        Genre genre1 = new Genre("Lyrics");
        mongoTemplate.save(genre1);

        var savedGenres = genreService.findByIds(Set.of(genre1.getId(), "invalidId"));

        assertThat(savedGenres).isNotNull().hasSize(1);
        assertThat(savedGenres.get(0))
                .extracting(GenreDto::id, GenreDto::name)
                .containsExactlyInAnyOrder(genre1.getId(), genre1.getName());
    }

    @Test
    @DisplayName("должен создавать жанр")
    public void shouldCreateNewGenre() {
        var genre = genreService.insert(new GenreDto(null, "Lyrics"));

        var savedGenre = mongoTemplate.findById(genre.id(), Genre.class);

        assertThat(savedGenre).isNotNull();
        assertThat(savedGenre.getName())
                .isEqualTo(genre.name())
                .isEqualTo("Lyrics");
        assertThat(savedGenre.getId()).isEqualTo(genre.id());
    }

    @Test
    @DisplayName("должен обновлять жанр")
    public void shouldUpdateGenre() {
        var genre = mongoTemplate.save(new Genre("Lyrics"));
        var updatedGenre = genreService.update(new GenreDto(genre.getId(), "New Genre"));
        var savedGenre = mongoTemplate.findById(genre.getId(), Genre.class);

        assertThat(savedGenre).isNotNull();
        assertThat(updatedGenre).isNotNull();
        assertThat(updatedGenre.name()).isEqualTo("New Genre");
        assertThat(savedGenre.getName()).isEqualTo(updatedGenre.name());
        assertThat(savedGenre.getId()).isEqualTo(updatedGenre.id());
    }

    @Test
    @DisplayName("должен удалять жанр")
    public void shouldDeleteGenre() {
        var genre1 = mongoTemplate.save(new Genre("Lyrics"));
        mongoTemplate.save(new Genre("Other"));

        genreService.deleteById(genre1.getId());

        var allGenres = mongoTemplate.findAll(Genre.class);
        var deletedGenre = mongoTemplate.findById(genre1.getId(), Genre.class);

        assertThat(allGenres).hasSize(1);
        assertThat(deletedGenre).isNull();
    }

}
