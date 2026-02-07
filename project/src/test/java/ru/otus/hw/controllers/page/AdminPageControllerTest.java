package ru.otus.hw.controllers.page;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = AdminPageController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        ru.otus.hw.config.security.jwt.JwtAuthenticationFilter.class,
                        ru.otus.hw.config.security.jwt.JwtTokenProvider.class,
                        ru.otus.hw.config.security.SecurityConfig.class
                })
        })
@AutoConfigureMockMvc(addFilters = false)
public class AdminPageControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("должен возвращать страницу панели администратора")
    void shouldRedirectToProfilePage() throws Exception {
        mvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));
    }
}
