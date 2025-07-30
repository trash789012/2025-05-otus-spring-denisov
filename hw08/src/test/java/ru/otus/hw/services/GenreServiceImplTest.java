package ru.otus.hw.services;

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
@Import({GenreServiceImpl.class, GenreConverter.class})
public class GenreServiceImplTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    private final GenreService genreService;

    @Autowired
    GenreServiceImplTest(GenreService genreService) {
        this.genreService = genreService;
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

}
