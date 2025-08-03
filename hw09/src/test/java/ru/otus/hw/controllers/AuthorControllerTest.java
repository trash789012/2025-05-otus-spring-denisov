package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;

@WebMvcTest(AuthorController.class)
//@Import(TestMongoConfig.class)
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    @Autowired
    private AuthorService authorService;

    @MockitoBean
    @Autowired
    private AuthorRepository authorRepository;


    @Test
    public void whenGetAllAuthors() throws Exception {
        // Здесь будет тест
    }
}