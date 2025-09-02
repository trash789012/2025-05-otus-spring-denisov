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
import ru.otus.hw.services.AuthorService;

import java.util.List;
import java.util.Map;

@WebFluxTest(AuthorController.class)
@Import({LocalValidatorFactoryBean.class})
class AuthorControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorService authorService;

    @Test
    void shouldReturnAllAuthors() throws Exception {
        List<AuthorDto> authors = List.of(
                new AuthorDto("1", "Author1"),
                new AuthorDto("2", "Author2")
        );

        Mockito.when(authorService.findAll()).thenReturn(Flux.fromIterable(authors));

        webClient.get().uri("/api/v1/author")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(objectMapper.writeValueAsString(authors));
    }

    @Test
    void shouldReturnEmptyAuthorsList() throws Exception {
        Mockito.when(authorService.findAll()).thenReturn(Flux.empty());

        webClient.get().uri("/api/v1/author")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class).hasSize(0);
    }

    @Test
    void shouldReturnAuthorById() throws Exception {
        AuthorDto author = new AuthorDto("1", "Author1");
        Mockito.when(authorService.findById("1")).thenReturn(Mono.just(author));

        webClient.get().uri("/api/v1/author/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(objectMapper.writeValueAsString(author));
    }

    @Test
    void shouldReturnNotFoundWhenAuthorNotExist() throws Exception {
        Mockito.when(authorService.findById("100")).thenReturn(Mono.empty());

        webClient.get().uri("/api/v1/author/100")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldCreateAuthor() throws Exception {
        AuthorDto authorToCreate = new AuthorDto(null, "NewAuthor");
        AuthorDto createdAuthor = new AuthorDto("1", "NewAuthor");

        Mockito.when(authorService.insert(Mockito.any(AuthorDto.class))).thenReturn(Mono.just(createdAuthor));

        webClient.post().uri("/api/v1/author")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authorToCreate)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json(objectMapper.writeValueAsString(createdAuthor));
    }

    @Test
    void shouldValidateWhenCreatingInvalidAuthor() throws Exception {
        AuthorDto invalidDto = new AuthorDto(null, null);

        webClient.post().uri("/api/v1/author")
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
    void shouldValidateWhenUpdatingInvalidAuthor() {
        AuthorDto invalidDto = new AuthorDto("1", null);

        webClient.put().uri("/api/v1/author/1")
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
    void shouldUpdateAuthor() throws Exception {
        AuthorDto authorToUpdate = new AuthorDto("1", "UpdatedAuthor");
        Mockito.when(authorService.update(Mockito.any(AuthorDto.class))).thenReturn(Mono.just(authorToUpdate));

        webClient.put().uri("/api/v1/author/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authorToUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json(objectMapper.writeValueAsString(authorToUpdate));
    }

    @Test
    void shouldReturnBadRequestWhenIdsMismatch() {
        AuthorDto authorToUpdate = new AuthorDto("2", "UpdatedAuthor");

        webClient.put().uri("/api/v1/author/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authorToUpdate)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldDeleteAuthor() throws Exception {
        Mockito.when(authorService.deleteById("1")).thenReturn(Mono.empty());

        webClient.delete().uri("/api/v1/author/1")
                .exchange()
                .expectStatus().isNoContent();

        Mockito.verify(authorService).deleteById("1");
    }
}
