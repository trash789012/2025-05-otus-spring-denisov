package ru.otus.hw.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.services.AuthorService;
@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    @Autowired
    private AuthorService authorService;

}
