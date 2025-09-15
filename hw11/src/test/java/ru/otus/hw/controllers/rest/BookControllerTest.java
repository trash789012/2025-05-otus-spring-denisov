package ru.otus.hw.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@WebFluxTest(BookController.class)
@Import({BookConverter.class, AuthorConverter.class, GenreConverter.class,
        LocalValidatorFactoryBean.class})
public class BookControllerTest {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private BookConverter bookConverter;

    @Test
    void shouldGetAllBooks() throws Exception {
        BookDto book1 = new BookDto("1", "Book Title 1", new AuthorDto("1", "Author 1"),
                List.of(new GenreDto("1", "Genre 1")));
        BookDto book2 = new BookDto("2", "Book Title 2", new AuthorDto("2", "Author 2"),
                List.of(new GenreDto("2", "Genre 2")));
        List<BookDto> books = List.of(book1, book2);

        given(bookService.findAll()).willReturn(Flux.fromIterable(books));

        webClient.get().uri("/api/v1/book")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(objectMapper.writeValueAsString(books));
    }

    @Test
    void shouldGetBookById() throws Exception {
        Author author = new Author();
        author.setId("1");
        author.setFullName("Author");

        List<Genre> genres = List.of(
                new Genre("1", "Genre 1"),
                new Genre("2", "Genre 2")
        );

        Book newBook = new Book();
        newBook.setId("1");
        newBook.setTitle("Book Title");
        newBook.setAuthor(author);
        newBook.setGenres(genres);
        BookDto bookDto = bookConverter.bookToBookDto(newBook);

        given(bookService.findById("1")).willReturn(Mono.just(bookDto));

        webClient.get().uri("/api/v1/book/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(objectMapper.writeValueAsString(bookConverter.bookDtoToBookFormDto(bookDto)));
    }

    @Test
    void shouldReturnNotFoundWhenBookNotExist() throws Exception {
        given(bookService.findById("1")).willReturn(Mono.empty());

        webClient.get().uri("/api/v1/book/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldCreateBook() throws Exception {
        BookDto bookDto = new BookDto(null, "New Book", new AuthorDto("1", "author1"),
                List.of(new GenreDto("1", "genre1")));
        BookFormDto requestDto = bookConverter.bookDtoToBookFormDto(bookDto);
        BookDto responseDto = new BookDto("1", "New Book", new AuthorDto("1", "author1"),
                List.of(new GenreDto("1", "genre1")));

        given(bookService.insert(any(BookFormDto.class))).willReturn(Mono.just(responseDto));

        webClient.post().uri("/api/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json(objectMapper.writeValueAsString(responseDto));
    }

    @Test
    void shouldValidateWhenCreatingInvalidBook() throws Exception {
        BookFormDto invalidDto = new BookFormDto(null, "", null, null);

        webClient.post().uri("/api/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(Map.class)
                .value(errors -> {
                    assert !errors.isEmpty();

                    errors.forEach((field, message) -> {
                        assert message != null && !message.toString().isBlank();
                    });
                });
    }

    @Test
    void shouldValidateWhenUpdatingInvalidBook() throws Exception {
        BookFormDto invalidDto = new BookFormDto("1", "", null, null);

        webClient.put().uri("/api/v1/book/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(response -> {
                    String body = new String(Objects.requireNonNull(response.getResponseBodyContent()));
                    System.out.println("Validation response: " + body);

                    assert !body.isBlank();
                });
    }

    @Test
    void shouldUpdateBook() throws Exception {
        BookDto bookDto = new BookDto("1", "New Book", new AuthorDto("1", "author1"),
                List.of(new GenreDto("1", "genre1")));
        BookFormDto requestDto = bookConverter.bookDtoToBookFormDto(bookDto);
        BookDto responseDto = new BookDto("1", "Updated Book", new AuthorDto("1", "author1"),
                List.of(new GenreDto("1", "genre1")));

        given(bookService.update(any(BookFormDto.class))).willReturn(Mono.just(responseDto));

        webClient.put().uri("/api/v1/book/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json(objectMapper.writeValueAsString(responseDto));
    }

    @Test
    void shouldReturnBadRequestWhenIdsMismatch() throws Exception {
        BookFormDto requestDto = new BookFormDto("2", "Book Title", "author1", List.of("genre1"));

        webClient.put().uri("/api/v1/book/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldDeleteBook() throws Exception {
        given(bookService.deleteById("1")).willReturn(Mono.empty());

        webClient.delete().uri("/api/v1/book/1")
                .exchange()
                .expectStatus().isNoContent();

        verify(bookService).deleteById("1");
    }
}
