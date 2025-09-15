package ru.otus.hw.controllers.security.page;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.controllers.page.ErrorPageController;
import ru.otus.hw.exceptions.GlobalExceptionHandler;
import ru.otus.hw.services.UserDetailService;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ErrorPageController.class)
@Import({GlobalExceptionHandler.class,
        SecurityConfig.class, UserDetailService.class})
public class ErrorPageControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailService userDetailService;

    static Stream<String> endpoints() {
        return Stream.of(
                "/access-denied"
        );
    }

    @ParameterizedTest
    @MethodSource("endpoints")
    public void shouldNotRedirectPagesEndpointUnauthorized(String endpoint) throws Exception {
        mockMvc.perform(get(endpoint))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("endpoints")
    public void shouldOkPagesEndpointAuthorized(String endpoint) throws Exception {
        mockMvc.perform(get(endpoint).with(user("user")))
                .andExpect(status().isOk());
    }

}
