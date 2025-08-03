package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    @Autowired
    private AuthorService authorService;

    @Test
    @DisplayName("должен вывести всех авторов")
    public void whenGetAllAuthors() throws Exception {
        List<AuthorDto> authors = List.of(
                new AuthorDto("1", "Lermontov"),
                new AuthorDto("2", "Pushkin")
        );

        when(authorService.findAll()).thenReturn(authors);

        mvc.perform(get("/authors"))
                .andExpect(status().isOk());
    }

}
