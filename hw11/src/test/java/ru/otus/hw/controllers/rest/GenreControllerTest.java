package ru.otus.hw.controllers.rest;

//@WebMvcTest(GenreController.class)
//@Import({LocalValidatorFactoryBean.class})
public class GenreControllerTest {
//    @Autowired
//    private MockMvc mvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private GenreService genreService;
//
//    @Test
//    void shouldReturnAllGenres() throws Exception {
//        List<GenreDto> genres = List.of(
//                new GenreDto("1", "Genre1"),
//                new GenreDto("2", "Genre2")
//        );
//
//        when(genreService.findAll()).thenReturn(genres);
//
//        mvc.perform(get("/api/v1/genre"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(content().json(objectMapper.writeValueAsString(genres)));
//    }
//
//    @Test
//    void shouldReturnEmptyGenreList() throws Exception {
//        when(genreService.findAll()).thenReturn(List.of());
//
//        mvc.perform(get("/api/v1/genre"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void shouldReturnGenreById() throws Exception {
//        GenreDto genre = new GenreDto("1", "Genre1");
//        when(genreService.findByIds(Set.of("1"))).thenReturn(List.of(genre));
//
//        mvc.perform(get("/api/v1/genre/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id", is("1")))
//                .andExpect(jsonPath("$.name", is("Genre1")));
//    }
//
//    @Test
//    void shouldReturnNotFoundWhenGenreNotExist() throws Exception {
//        when(genreService.findByIds(Set.of("1"))).thenReturn(List.of());
//
//        mvc.perform(get("/api/v1/genre/1"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void shouldCreateGenre() throws Exception {
//        GenreDto genreToCreate = new GenreDto(null, "NewGenre");
//        GenreDto createdGenre = new GenreDto("1", "NewGenre");
//
//        when(genreService.insert(any(GenreDto.class))).thenReturn(createdGenre);
//
//        mvc.perform(post("/api/v1/genre")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(genreToCreate)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is("1")))
//                .andExpect(jsonPath("$.name", is("NewGenre")));
//    }
//
//    @Test
//    void shouldUpdateGenre() throws Exception {
//        GenreDto genreToUpdate = new GenreDto("1", "UpdatedGenre");
//        when(genreService.update(any(GenreDto.class))).thenReturn(genreToUpdate);
//
//        mvc.perform(put("/api/v1/genre/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(genreToUpdate)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is("1")))
//                .andExpect(jsonPath("$.name", is("UpdatedGenre")));
//    }
//
//    @Test
//    void shouldValidateWhenCreatingInvalidGenre() throws Exception {
//        GenreDto invalidDto = new GenreDto(null, null);
//
//        mvc.perform(post("/api/v1/genre")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$[0].defaultMessage").exists());
//    }
//
//    @Test
//    void shouldValidateWhenUpdatingInvalidGenre() throws Exception {
//        GenreDto invalidDto = new GenreDto("1", null);
//
//        mvc.perform(put("/api/v1/genre/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$[0].defaultMessage").exists());
//    }
//
//    @Test
//    void shouldReturnBadRequestWhenIdsMismatch() throws Exception {
//        GenreDto genreToUpdate = new GenreDto("2", "UpdatedGenre");
//
//        mvc.perform(put("/api/v1/genre/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(genreToUpdate)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void shouldDeleteGenre() throws Exception {
//        mvc.perform(delete("/api/v1/genre/1"))
//                .andExpect(status().isNoContent());
//
//        Mockito.verify(genreService).deleteById("1");
//    }

}
