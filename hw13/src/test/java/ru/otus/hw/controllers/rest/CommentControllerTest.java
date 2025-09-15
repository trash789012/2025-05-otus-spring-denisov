package ru.otus.hw.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.GlobalExceptionHandler;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class})
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
                new CommentDto(1L, "Comment 1", 1L),
                new CommentDto(2L, "Comment 2", 1L)
        );

        when(commentService.findByBookId(1L)).thenReturn(comments);

        mvc.perform(get("/api/v1/book/1/comment"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(comments)));
    }

    @Test
    void shouldReturnEmptyListWhenNoCommentsForBook() throws Exception {
        when(commentService.findByBookId(1)).thenReturn(List.of());

        mvc.perform(get("/api/v1/book/1/comment"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void shouldCreateCommentForBook() throws Exception {
        CommentDto commentToCreate = new CommentDto(0L, "New Comment", 1L);
        CommentDto createdComment = new CommentDto(1L, "New Comment", 1L);

        when(commentService.insert(any(CommentDto.class))).thenReturn(createdComment);

        mvc.perform(post("/api/v1/book/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentToCreate)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldDeleteComment() throws Exception {
        mvc.perform(delete("/api/v1/book/1/comment/1"))
                .andExpect(status().isOk());

        Mockito.verify(commentService).deleteById(1);
    }

    @Test
    void shouldValidateCommentTextNotEmpty() throws Exception {
        CommentDto emptyComment = new CommentDto(0L, "", 1L);

        mvc.perform(post("/api/v1/book/book1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyComment)))
                .andExpect(status().isBadRequest());
    }
}
