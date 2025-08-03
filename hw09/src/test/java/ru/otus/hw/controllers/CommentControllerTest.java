package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.services.CommentService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private CommentRepository commentRepository;

    @Test
    void shouldDeleteCommentAndRedirectToBookPage() throws Exception {
        String bookId = "1";
        String commentId = "10";

        mvc.perform(post("/book/{bookId}/comments/{commentId}/deleteComment", bookId, commentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/" + bookId));

        Mockito.verify(commentService).deleteById(commentId);
        Mockito.verifyNoMoreInteractions(commentService);
    }

    @Test
    void shouldAddCommentAndRedirectToBookPage() throws Exception {
        String bookId = "1";
        CommentDto commentDto = new CommentDto(null, "New comment", bookId);

        mvc.perform(post("/book/{bookId}/addComment", bookId)
                        .param("text", "New comment")
                        .param("bookId", bookId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/" + bookId));

        Mockito.verify(commentService).insert(Mockito.argThat(comment ->
                comment.text().equals("New comment") &&
                        comment.bookId().equals(bookId)
        ));
        Mockito.verifyNoMoreInteractions(commentService);
    }

    @Test
    void shouldHandleEmptyCommentText() throws Exception {
        String bookId = "1";

        mvc.perform(post("/book/{bookId}/addComment", bookId)
                        .param("text", "")
                        .param("bookId", bookId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/" + bookId));

        Mockito.verify(commentService).insert(Mockito.any());
    }
}
