package ru.otus.hw.controllers.page;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = {ErrorPageController.class})
@AutoConfigureMockMvc(addFilters = false)
public class ErrorControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void listPageShouldReturnErrorView() throws Exception {
        mvc.perform(get("/access-denied"))
                .andExpect(status().isOk())
                .andExpect(view().name("access-denied"));
    }
}

