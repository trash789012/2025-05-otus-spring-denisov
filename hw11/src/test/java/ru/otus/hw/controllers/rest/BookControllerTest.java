package ru.otus.hw.controllers.rest;

@WebMvcTest(BookController.class)
@Import({BookConverter.class, AuthorConverter.class, GenreConverter.class,
        LocalValidatorFactoryBean.class})
public class BookControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private BookConverter bookConverter;

    @Test
    void shouldGetAllBooks() throws Exception {

        BookDto book1 = new BookDto(
                "1",
                "Book Title 1",
                new AuthorDto("1", "Author 1"),
                List.of(new GenreDto("1", "Genre 1"))
        );

        BookDto book2 = new BookDto(
                "2",
                "Book Title 2",
                new AuthorDto("2", "Author 2"),
                List.of(new GenreDto("2", "Genre 2"))
        );

        List<BookDto> books = List.of(book1, book2);

        given(bookService.findAll()).willReturn(books);

        mvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(books)))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldGetBookById() throws Exception {
        BookDto bookDto = new BookDto(
                "1",
                "Book Title",
                new AuthorDto("1", "Author"),
                List.of(new GenreDto("1", "Genre"))
        );

        given(bookService.findById("1")).willReturn(java.util.Optional.of(bookDto));

        mvc.perform(get("/api/v1/book/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Book Title"))
                .andExpect(jsonPath("$.authorId").value("1"))
                .andExpect(jsonPath("$.genreIds[0]").value("1"));
    }

    @Test
    void shouldReturnNotFoundWhenBookNotExist() throws Exception {
        given(bookService.findById("1")).willReturn(java.util.Optional.empty());

        mvc.perform(get("/api/v1/book/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateBook() throws Exception {
        BookDto bookDto = new BookDto(
                null,
                "New Book",
                new AuthorDto("1", "author1"),
                List.of(new GenreDto("1", "genre1"))
        );
        BookFormDto requestDto = bookConverter.bookDtoToBookFormDto(bookDto);
        BookDto responseDto = new BookDto(
                "1",
                "New Book",
                new AuthorDto("1", "author1"),
                List.of(new GenreDto("1", "genre1"))
        );

        given(bookService.insert(any(BookFormDto.class))).willReturn(responseDto);

        mvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void shouldValidateWhenCreatingInvalidBook() throws Exception {
        BookFormDto invalidDto = new BookFormDto(null, "", null, null);

        mvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].defaultMessage").exists());
    }

    @Test
    void shouldValidateWhenUpdatingInvalidBook() throws Exception {
        BookFormDto invalidDto = new BookFormDto("1", "", null, null);

        mvc.perform(put("/api/v1/book/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].defaultMessage").exists());
    }

    @Test
    void shouldUpdateBook() throws Exception {
        BookDto bookDto = new BookDto(
                "1",
                "New Book",
                new AuthorDto("1", "author1"),
                List.of(new GenreDto("1", "genre1"))
        );
        BookFormDto requestDto = bookConverter.bookDtoToBookFormDto(bookDto);
        BookDto responseDto = new BookDto(
                "1",
                "Updated Book",
                new AuthorDto("1", "author1"),
                List.of(new GenreDto("1", "genre1"))
        );
        given(bookService.update(any(BookFormDto.class))).willReturn(responseDto);

        mvc.perform(put("/api/v1/book/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void shouldReturnBadRequestWhenIdsMismatch() throws Exception {
        BookFormDto requestDto = new BookFormDto("2", "Book Title", "author1", List.of("genre1"));

        mvc.perform(put("/api/v1/book/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteBook() throws Exception {
        mvc.perform(delete("/api/v1/book/1"))
                .andExpect(status().isNoContent());

        verify(bookService).deleteById("1");
    }
}
