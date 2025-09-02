package ru.otus.hw.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@WebFluxTest(CommentController.class)
public class CommentControllerTest {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @Test
    void shouldGetCommentsByBookId() throws Exception {
        List<CommentDto> comments = List.of(
                new CommentDto("1", "Comment 1", "book1"),
                new CommentDto("2", "Comment 2", "book1")
        );

        given(commentService.findByBookId("1")).willReturn(Flux.fromIterable(comments));

        webClient.get().uri("/api/v1/book/1/comment")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(objectMapper.writeValueAsString(comments));
    }

    @Test
    void shouldReturnEmptyListWhenNoCommentsForBook() throws Exception {
        given(commentService.findByBookId("book1")).willReturn(Flux.empty());

        webClient.get().uri("/api/v1/book/book1/comment")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CommentDto.class).hasSize(0);
    }

    @Test
    void shouldCreateCommentForBook() throws Exception {
        CommentDto commentToCreate = new CommentDto(null, "New Comment", "book1");
        CommentDto createdComment = new CommentDto("1", "New Comment", "book1");

        given(commentService.insert(any(CommentDto.class))).willReturn(Mono.just(createdComment));

        webClient.post().uri("/api/v1/book/book1/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(commentToCreate)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json(objectMapper.writeValueAsString(createdComment));
    }

    @Test
    void shouldDeleteComment() throws Exception {
        given(commentService.deleteById("comment1")).willReturn(Mono.empty());

        webClient.delete().uri("/api/v1/book/book1/comment/comment1")
                .exchange()
                .expectStatus().isNoContent();

        verify(commentService).deleteById("comment1");
    }

    @Test
    void shouldValidateCommentTextNotEmpty() throws Exception {
        CommentDto emptyComment = new CommentDto(null, null, "book1");

        webClient.post().uri("/api/v1/book/book1/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(emptyComment)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .json(objectMapper.writeValueAsString(emptyComment));
    }
}
