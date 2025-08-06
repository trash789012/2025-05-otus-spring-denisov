package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(GenrePageController.class)
public class GenrePageControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void listPageShouldReturnGenresView() throws Exception {
        mvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(view().name("genres"));
    }

    @Test
    void editPageWithIdShouldReturnGenreView() throws Exception {
        mvc.perform(get("/genre/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("genre"));
    }

    @Test
    void editPageNewGenreShouldReturnGenreView() throws Exception {
        mvc.perform(get("/genre/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("genre"));
    }
}
