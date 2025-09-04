package ru.otus.hw.controllers.page;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = {AuthorPageController.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthorPageControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void listPageShouldReturnAuthorsView() throws Exception {
        mvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors"));
    }

    @Test
    void editPageWithIdShouldReturnAuthorView() throws Exception {
        mvc.perform(get("/author/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("author"));
    }

    @Test
    void editPageNewAuthorShouldReturnAuthorView() throws Exception {
        mvc.perform(get("/author/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("author"));
    }
}