package ru.otus.hw.controllers.security.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.controllers.rest.AuthorController;
import ru.otus.hw.controllers.rest.BookController;
import ru.otus.hw.controllers.rest.CommentController;
import ru.otus.hw.controllers.rest.GenreController;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.GlobalExceptionHandler;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.UserDetailService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        AuthorController.class,
        GenreController.class,
        CommentController.class,
        BookController.class,
})
@Import({
        AuthorService.class,
        GenreService.class,
        CommentService.class,
        BookService.class,
        BookConverter.class,
        AuthorConverter.class,
        GenreConverter.class,
        GlobalExceptionHandler.class,
        SecurityConfig.class,
        UserDetailService.class
})
public class AllControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private UserDetailService userDetailService;

    @Autowired
    private ObjectMapper objectMapper;

    static Stream<Endpoint> getEndpoints() {
        return Stream.of(
                new Endpoint(HttpMethod.GET, "/api/v1/author"),
                new Endpoint(HttpMethod.GET, "/api/v1/author/1"),
                new Endpoint(HttpMethod.GET, "/api/v1/genre"),
                new Endpoint(HttpMethod.GET, "/api/v1/genre/1"),
                new Endpoint(HttpMethod.GET, "/api/v1/book/1/comment"),
                new Endpoint(HttpMethod.GET, "/api/v1/book"),
                new Endpoint(HttpMethod.GET, "/api/v1/book/1")
        );
    }

    static Stream<Endpoint> deleteEndpoints() {
        return Stream.of(
                new Endpoint(HttpMethod.DELETE, "/api/v1/author/1"),
                new Endpoint(HttpMethod.DELETE, "/api/v1/genre/1"),
                new Endpoint(HttpMethod.DELETE, "/api/v1/book/1/comment/1"),
                new Endpoint(HttpMethod.DELETE, "/api/v1/book/1")
        );
    }

    static Stream<Endpoint> postEndpoints() {
        return Stream.of(
                new Endpoint(HttpMethod.POST, "/api/v1/author"),
                new Endpoint(HttpMethod.POST, "/api/v1/genre"),
                new Endpoint(HttpMethod.POST, "/api/v1/book/1/comment"),
                new Endpoint(HttpMethod.POST, "/api/v1/book")
        );
    }

    static Stream<Endpoint> putEndpoints() {
        return Stream.of(
                new Endpoint(HttpMethod.PUT, "/api/v1/author/1"),
                new Endpoint(HttpMethod.PUT, "/api/v1/genre/1"),
                new Endpoint(HttpMethod.PUT, "/api/v1/book/1")
        );
    }

    private record Endpoint(HttpMethod method, String uri) {
    }

    @BeforeEach
    void setUpData() {
        AuthorDto authorDto = new AuthorDto(1L, "Test Author");
        GenreDto genreDto = new GenreDto(1L, "Test Genre");
        CommentDto commentDto = new CommentDto(1L, "Comment", 1L);

        when(authorService.findAll()).thenReturn(List.of(authorDto));
        when(authorService.findById(anyLong())).thenReturn(Optional.of(authorDto));
        when(genreService.findAll()).thenReturn(List.of(genreDto));
        when(genreService.findByIds(any())).thenReturn(List.of(genreDto));
        when(commentService.findById(anyLong())).thenReturn(Optional.of(commentDto));

        BookDto bookDto = new BookDto(1L, "Book", authorDto, List.of(genreDto));
        when(bookService.findById(1L)).thenReturn(Optional.of(bookDto));
        when(bookService.findAll()).thenReturn(List.of(bookDto));
    }

    @ParameterizedTest
    @MethodSource({"getEndpoints", "postEndpoints", "deleteEndpoints", "putEndpoints"})
    void shouldAllAnyEndpointWithUnauthorizedUserReturnRedirect(Endpoint endpoint) throws Exception {
        mockMvc.perform(request(endpoint.method(), endpoint.uri()).with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @ParameterizedTest
    @MethodSource("getEndpoints")
    @WithMockUser(username = "user")
    void shouldAllGetEndpointWithAuthorizedUserReturnOk(Endpoint endpoint) throws Exception {
        mockMvc.perform(request(endpoint.method(), endpoint.uri()))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("deleteEndpoints")
    @WithMockUser(username = "user")
    void shouldAllDeleteEndpointWithAuthorizedUserReturnOk(Endpoint endpoint) throws Exception {
        mockMvc.perform(request(endpoint.method(), endpoint.uri()).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("user")
    void shouldCreateAuthorWithAuthorizedUserReturnOk() throws Exception {
        AuthorDto author = new AuthorDto(0L, "Test Author");
        AuthorDto insertedAuthor = new AuthorDto(1L, "Test Author");

        when(authorService.insert(author)).thenReturn(insertedAuthor);

        mockMvc.perform(request(HttpMethod.POST, "/api/v1/author")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertedAuthor)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user")
    void shouldUpdateAuthorWithAuthorizedUserReturnOk() throws Exception {
        AuthorDto authorDto = new AuthorDto(0L, "Test Author");
        AuthorDto updatedAuthorDto = new AuthorDto(1L, "Test Author");

        when(authorService.update(authorDto)).thenReturn(updatedAuthorDto);

        mockMvc.perform(request(HttpMethod.PUT, "/api/v1/author/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedAuthorDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    void shouldCreateGenreWithAuthorizedUserReturnOk() throws Exception {
        GenreDto genre = new GenreDto(0L, "Test Genre");
        GenreDto insertedGenre = new GenreDto(1L, "Test Genre");

        when(genreService.insert(genre)).thenReturn(insertedGenre);

        mockMvc.perform(request(HttpMethod.POST, "/api/v1/genre")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertedGenre)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("user")
    void shouldUpdateGenreWithAuthorizedUserReturnOk() throws Exception {
        GenreDto genre = new GenreDto(0L, "Test Genre");
        GenreDto updatedGenre = new GenreDto(1L, "Test Genre");

        when(genreService.update(genre)).thenReturn(updatedGenre);

        mockMvc.perform(request(HttpMethod.PUT, "/api/v1/genre/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedGenre)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("user")
    void shouldCreateCommentWithAuthorizedUserReturnOk() throws Exception {
        CommentDto comment = new CommentDto(0L, "Comment", 1L);
        CommentDto insertedComment = new CommentDto(1L, "Comment", 1L);

        when(commentService.insert(comment)).thenReturn(insertedComment);

        mockMvc.perform(request(HttpMethod.POST, "/api/v1/book/1/comment")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertedComment)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser()
    void shouldCreateBookWithAuthorizedUserReturnOk() throws Exception {
        AuthorDto authorDto = new AuthorDto(1L, "Test Author");
        GenreDto genreDto = new GenreDto(1L, "Test Genre");

        BookFormDto bookFormDto = new BookFormDto(0L, "Title", 1L, List.of(1L));
        BookDto insertedBook = new BookDto(1L, "Title", authorDto, List.of(genreDto));

        when(bookService.insert(bookFormDto)).thenReturn(insertedBook);

        mockMvc.perform(request(HttpMethod.POST, "/api/v1/book")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookFormDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("user")
    void shouldUpdateBookWithAuthorizedUserReturnOk() throws Exception {
        AuthorDto authorDto = new AuthorDto(1L, "Test Author");
        GenreDto genreDto = new GenreDto(1L, "Test Genre");

        BookFormDto bookFormDto = new BookFormDto(1L, "Title", 1L, List.of(1L));
        BookDto updatedBook = new BookDto(1L, "Title Updated", authorDto, List.of(genreDto));

        when(bookService.update(bookFormDto)).thenReturn(updatedBook);

        mockMvc.perform(request(HttpMethod.PUT, "/api/v1/book/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookFormDto)))
                .andExpect(status().isOk());
    }

}