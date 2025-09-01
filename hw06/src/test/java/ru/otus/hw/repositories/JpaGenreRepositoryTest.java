package ru.otus.hw.repositories;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaGenreRepository.class})
@DisplayName("Репозиторий JPA для работы с жанрами ")
public class JpaGenreRepositoryTest {

    public static final int EXPECTED_NUMBER_OF_GENRES = 6;

    public static final long NOT_EXISTS_GENRE_ID = 99L;

    @Autowired
    private JpaGenreRepository jpaGenreRepository;

    @DisplayName(" должен загружать список всех жанров")
    @Test
    void shouldFindAllGenres() {
        val genres = jpaGenreRepository.findAll();

        assertThat(genres).isNotNull().hasSize(EXPECTED_NUMBER_OF_GENRES)
                .allMatch(g -> g.getId() != 0)
                .allMatch(g -> !g.getName().isBlank());
    }

    @DisplayName(" должен загружать список жанров по id's")
    @Test
    void shouldFindAllGenresByIds() {
        val ids = Set.of(1L, 2L);

        val genres = jpaGenreRepository.findAllByIds(ids);

        assertThat(genres).isNotNull().hasSize(2)
                .allSatisfy(g -> {
                    assertThat(g.getId()).isIn(ids);
                    assertThat(!g.getName().isBlank());
                });
    }

    @DisplayName(" должен возвращать пустой список, если жанров нет")
    @Test
    void shouldReturnEmptyListWhenNotFound() {
        val ids = Set.of(NOT_EXISTS_GENRE_ID);

        val genres = jpaGenreRepository.findAllByIds(ids);

        assertThat(genres)
                .isNotNull()
                .isEmpty();
    }

}
