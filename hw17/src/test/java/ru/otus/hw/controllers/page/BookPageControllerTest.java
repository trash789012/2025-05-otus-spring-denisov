package ru.otus.hw.controllers.page;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BookPageController.class)
@Import({BookConverter.class, AuthorConverter.class, GenreConverter.class, CommentConverter.class})
public class BookPageControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("возвращать главную страницу со списком книг")
    void listPageShouldReturnIndexView() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("возвращать страницу создания новой книги")
    void editPageNewBook_ShouldReturnBookView() throws Exception {
        mvc.perform(get("/book/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("book"));
    }

    @Test
    @DisplayName("возвращать страницу редактирования существующей книги")
    void editPageWithExistingId_ShouldReturnBookView() throws Exception {
        mvc.perform(get("/book/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("book"));
    }
}
