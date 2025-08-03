package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BookController.class)
@Import({BookConverter.class, AuthorConverter.class, GenreConverter.class, CommentConverter.class})
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BookConverter bookConverter;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private AuthorRepository authorRepository;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private CommentRepository commentRepository;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private GenreRepository genreRepository;

    @Test
    void shouldReturnListPage() throws Exception {
        List<BookDto> books = List.of(
                new BookDto("1", "Book 1", null, null),
                new BookDto("2", "Book 2", null, null)
        );
        when(bookService.findAll()).thenReturn(books);

        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("books", books));
    }

    @Test
    void shouldReturnEditPageForExistingBook() throws Exception {
        String bookId = "1";
        BookDto bookDto = new BookDto(bookId, "Existing Book", null, null);
        BookFormDto bookFormDto = new BookFormDto(bookId, "Existing Book", null, null);

        when(bookService.findById(bookId)).thenReturn(Optional.of(bookDto));
        when(authorService.findAll()).thenReturn(List.of());
        when(genreService.findAll()).thenReturn(List.of());
        when(commentService.findByBookId(bookId)).thenReturn(List.of());

        mvc.perform(get("/book/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("book"))
                .andExpect(model().attribute("book", bookFormDto))
                .andExpect(model().attributeExists("allAuthors"))
                .andExpect(model().attributeExists("allGenres"))
                .andExpect(model().attributeExists("bookComments"));
    }

    @Test
    void shouldReturnEditPageForNewBook() throws Exception {
        when(authorService.findAll()).thenReturn(List.of());
        when(genreService.findAll()).thenReturn(List.of());

        mvc.perform(get("/book/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("book"))
                .andExpect(model().attribute("book",
                        new BookFormDto(null, null, null, null)))
                .andExpect(model().attributeExists("allAuthors"))
                .andExpect(model().attributeExists("allGenres"));
    }

    @Test
    void shouldRedirectToErrorPageWhenBookNotFound() throws Exception {
        String nonExistentId = "99";
        when(bookService.findById(nonExistentId)).thenReturn(Optional.empty());

        mvc.perform(get("/book/{id}", nonExistentId))
                .andExpect(status().isOk())
                .andExpect(view().name("customError"))
                .andExpect(model().attributeExists("errorText"));
    }

    @Test
    void shouldCreateNewBook() throws Exception {
        BookFormDto newBook = new BookFormDto(null, "New Book", null, null);
        BookDto savedBook = new BookDto("1", "New Book", null, null);

        when(bookService.insert(newBook)).thenReturn(savedBook);

        mvc.perform(post("/book")
                        .param("title", "New Book"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/1"));
    }

    @Test
    void shouldUpdateExistingBook() throws Exception {
        BookFormDto existingBook = new BookFormDto("1", "Updated Book", null, null);
        BookDto updatedBook = new BookDto("1", "Updated Book", null, null);

        when(bookService.update(existingBook)).thenReturn(updatedBook);

        mvc.perform(post("/book")
                        .param("id", "1")
                        .param("title", "Updated Book"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/1"));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        String bookId = "1";

        mvc.perform(post("/bookDelete")
                        .param("bookId", bookId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).deleteById(bookId);
    }
}
