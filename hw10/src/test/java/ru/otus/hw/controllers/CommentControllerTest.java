package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.rest.controllers.CommentController;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {
    @Autowired
    private MockMvc mvc;

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

        when(commentService.findByBookId("1")).thenReturn(comments);

        String URI = "/api/v1/book/1/comment";
        var status = mvc.perform(get(URI)).andReturn();

        mvc.perform(get("/api/v1/book/1/comment"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].text", is("Comment 1")))
                .andExpect(jsonPath("$[0].bookId", is("book1")))
                .andExpect(jsonPath("$[1].id", is("2")))
                .andExpect(jsonPath("$[1].text", is("Comment 2")))
                .andExpect(jsonPath("$[1].bookId", is("book1")));
    }

    @Test
    void shouldReturnEmptyListWhenNoCommentsForBook() throws Exception {
        when(commentService.findByBookId("book1")).thenReturn(List.of());

        mvc.perform(get("/api/v1/book/book1/comment"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void shouldCreateCommentForBook() throws Exception {
        CommentDto commentToCreate = new CommentDto(null, "New Comment", "book1");
        CommentDto createdComment = new CommentDto("1", "New Comment", "book1");

        when(commentService.insert(any(CommentDto.class))).thenReturn(createdComment);

        mvc.perform(post("/api/v1/book/book1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.text", is("New Comment")))
                .andExpect(jsonPath("$.bookId", is("book1")));
    }

    @Test
    void shouldDeleteComment() throws Exception {
        mvc.perform(delete("/api/v1/book/book1/comment/comment1"))
                .andExpect(status().isNoContent());

        Mockito.verify(commentService).deleteById("comment1");
    }

    @Test
    void shouldValidateCommentTextNotEmpty() throws Exception {
        CommentDto emptyComment = new CommentDto(null, "", "book1");

        mvc.perform(post("/api/v1/book/book1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyComment)))
                .andExpect(status().isBadRequest());
    }
}
