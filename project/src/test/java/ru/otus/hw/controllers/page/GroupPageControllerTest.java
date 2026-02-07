package ru.otus.hw.controllers.page;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = GroupPageController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        ru.otus.hw.config.security.jwt.JwtAuthenticationFilter.class,
                        ru.otus.hw.config.security.jwt.JwtTokenProvider.class,
                        ru.otus.hw.config.security.SecurityConfig.class
                })
        })
@AutoConfigureMockMvc(addFilters = false)
public class GroupPageControllerTest {
    @Autowired
    private MockMvc mvc;

    static Stream<String> providePaths() {
        return Stream.of(
                "/profile/group/1",
                "/admin/group/1",
                "/admin/group/new"
        );
    }

    @ParameterizedTest
    @MethodSource("providePaths")
    @DisplayName("должен возвращать страницу c группой")
    void shouldRedirectToGroupPage(String path) throws Exception {
        mvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(view().name("group"));
    }
}
