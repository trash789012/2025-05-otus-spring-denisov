package ru.otus.hw.controllers.security.page;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.controllers.page.GenrePageController;
import ru.otus.hw.exceptions.GlobalExceptionHandler;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.UserDetailService;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenrePageController.class)
@Import({GenreService.class, GlobalExceptionHandler.class,
        SecurityConfig.class, UserDetailService.class})
public class GenrePageControllerSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private UserDetailService userDetailService;

    static Stream<String> endpoints() {
        return Stream.of(
                "/genres",
                "/genre/1",
                "/genre/new"
        );
    }

    @ParameterizedTest
    @MethodSource("endpoints")
    public void shouldRedirectPagesEndpointUnauthorized(String endpoint) throws Exception {
        mockMvc.perform(get(endpoint))
                .andExpect(status().is3xxRedirection());
    }

    @ParameterizedTest
    @MethodSource("endpoints")
    public void shouldOkPagesEndpointAuthorized(String endpoint) throws Exception {
        mockMvc.perform(get(endpoint).with(user("user")))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldOkPagesEndpointUnauthorized() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk());
    }

}
