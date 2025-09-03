package ru.otus.hw.controllers.security.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.controllers.rest.AuthorController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.GlobalExceptionHandler;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.UserDetailService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
@Import({AuthorService.class, GlobalExceptionHandler.class,
        SecurityConfig.class, UserDetailService.class})
public class AuthorControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private UserDetailService userDetailService;

    static Stream<String> getEndpoints() {
        return Stream.of(
                "/api/v1/author",
                "/api/v1/author/1"
        );
    }

    @ParameterizedTest
    @MethodSource("getEndpoints")
    void getAllAuthors_Unauthorized_ShouldRedirectToLogin(String endpoint) throws Exception {
        mockMvc.perform(get(endpoint))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user")
    void getAllAuthors_AuthorizedUser_ShouldReturnOk() throws Exception {
        when(authorService.findAll()).thenReturn(List.of(new AuthorDto("1", "Test Author")));

        mockMvc.perform(get("/api/v1/author"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "user")
    void getAuthorById_AuthorizedUser_ShouldReturnOk() throws Exception {
        when(authorService.findById("1")).thenReturn(Optional.of(new AuthorDto("1", "Test Author")));

        mockMvc.perform(get("/api/v1/author/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }


}
