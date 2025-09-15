package ru.otus.hw.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(GenreController.class)
@Import({LocalValidatorFactoryBean.class})
public class GenreControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GenreService genreService;

    @Test
    void shouldReturnAllGenres() throws Exception {
        List<GenreDto> genres = List.of(
                new GenreDto("1", "Genre1"),
                new GenreDto("2", "Genre2")
        );

        Mockito.when(genreService.findAll()).thenReturn(Flux.fromIterable(genres));

        webClient.get().uri("/api/v1/genre")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(objectMapper.writeValueAsString(genres));
    }

    @Test
    void shouldReturnEmptyGenreList() throws Exception {
        Mockito.when(genreService.findAll()).thenReturn(Flux.empty());

        webClient.get().uri("/api/v1/genre")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class).hasSize(0);
    }

    @Test
    void shouldReturnGenreById() throws Exception {
        GenreDto genre = new GenreDto("1", "Genre1");
        Mockito.when(genreService.findByIds(Set.of("1"))).thenReturn(Flux.just(genre));

        webClient.get()
                .uri("/api/v1/genre/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(objectMapper.writeValueAsString(genre));
    }

    @Test
    void shouldReturnNotFoundWhenGenreNotExist() throws Exception {
        Mockito.when(genreService.findByIds(Set.of("1"))).thenReturn(Flux.empty());

        webClient.get().uri("/api/v1/genre/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldCreateGenre() throws Exception {
        GenreDto genreToCreate = new GenreDto(null, "NewGenre");
        GenreDto createdGenre = new GenreDto("1", "NewGenre");

        Mockito.when(genreService.insert(any(GenreDto.class))).thenReturn(Mono.just(createdGenre));

        webClient.post()
                .uri("/api/v1/genre")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(genreToCreate)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json(objectMapper.writeValueAsString(createdGenre));
    }

    @Test
    void shouldUpdateGenre() throws Exception {
        GenreDto genreToUpdate = new GenreDto("1", "UpdatedGenre");
        Mockito.when(genreService.update(any(GenreDto.class))).thenReturn(Mono.just(genreToUpdate));

        webClient.put()
                .uri("/api/v1/genre/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(genreToUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json(objectMapper.writeValueAsString(genreToUpdate));
    }

    @Test
    void shouldValidateWhenCreatingInvalidGenre() throws Exception {
        GenreDto invalidDto = new GenreDto(null, null);

        webClient.post()
                .uri("/api/v1/genre")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(Map.class)
                .value(errors -> {
                    assert !errors.isEmpty();

                    errors.forEach((field, message) -> {
                        assert message != null && !message.toString().isBlank();
                    });
                });
    }

    @Test
    void shouldValidateWhenUpdatingInvalidGenre() throws Exception {
        GenreDto invalidDto = new GenreDto("1", null);

        webClient.put()
                .uri("/api/v1/genre/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(response -> {
                    String body = new String(Objects.requireNonNull(response.getResponseBodyContent()));
                    System.out.println("Validation response: " + body);

                    assert !body.isBlank();
                });
    }

    @Test
    void shouldReturnBadRequestWhenIdsMismatch() throws Exception {
        GenreDto genreToUpdate = new GenreDto("2", "UpdatedGenre");

        webClient.put()
                .uri("/api/v1/genre/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(genreToUpdate)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldDeleteGenre() throws Exception {
        Mockito.when(genreService.deleteById("1")).thenReturn(Mono.empty());

        webClient.delete()
                .uri("/api/v1/genre/1")
                .exchange()
                .expectStatus().isNoContent();

        Mockito.verify(genreService).deleteById("1");
    }

}
