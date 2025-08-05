package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(GenrePageController.class)
public class GenreControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private GenreRepository genreRepository;

    @Test
    void shouldReturnListPage() throws Exception {
        List<GenreDto> expectedGenres = List.of(
                new GenreDto("1", "Genre 1"),
                new GenreDto("2", "Genre 2")
        );

        when(genreService.findAll()).thenReturn(expectedGenres);

        MvcResult result = mvc.perform(get("/genres"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("genres"))
                .andReturn();

        List<GenreDto> actualGenres = (List<GenreDto>) result.getModelAndView().getModel().get("genres");
        assertThat(actualGenres)
                .hasSize(2)
                .containsExactlyElementsOf(expectedGenres);
    }

    @Test
    void shouldReturnEditPageForExistingGenre() throws Exception {
        GenreDto expectedGenre = new GenreDto("1", "Existing Genre");

        when(genreService.findByIds(Set.of("1")))
                .thenReturn(List.of(expectedGenre));

        MvcResult result = mvc.perform(get("/genre/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("genre"))
                .andReturn();

        GenreDto actualGenre = (GenreDto) result.getModelAndView().getModel().get("genre");
        assertThat(actualGenre.id()).isEqualTo(expectedGenre.id());
        assertThat(actualGenre.name()).isEqualTo(expectedGenre.name());
    }

    @Test
    void shouldReturnEditPageForNewGenre() throws Exception {
        MvcResult result = mvc.perform(get("/genre/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("genre"))
                .andReturn();

        GenreDto actualGenre = (GenreDto) result.getModelAndView().getModel().get("genre");
        assertThat(actualGenre.id()).isNull();
        assertThat(actualGenre.name()).isNull();
    }

    @Test
    void shouldRedirectToCustomErrorWhenGenreNotFound() throws Exception {
        String nonExistentId = "99";
        when(genreService.findByIds(Set.of(nonExistentId)))
                .thenReturn(List.of());

        mvc.perform(get("/genre/{id}", nonExistentId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("customError"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorText"));
    }

    @Test
    void shouldCreateNewGenre() throws Exception {
        GenreDto newGenre = new GenreDto(null, "New Genre");
        GenreDto savedGenre = new GenreDto("1", "New Genre");
        when(genreService.insert(newGenre)).thenReturn(savedGenre);

        mvc.perform(post("/genre")
                        .param("name", "New Genre"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/genre/1"));
    }

    @Test
    void shouldUpdateExistingGenre() throws Exception {
        GenreDto existingGenre = new GenreDto("1", "Updated Genre");
        when(genreService.update(existingGenre)).thenReturn(existingGenre);

        mvc.perform(post("/genre")
                        .param("id", "1")
                        .param("name", "Updated Genre"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/genre/1"));
    }

    @Test
    void shouldDeleteGenre() throws Exception {
        mvc.perform(post("/genreDelete")
                        .param("genreId", "1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/genres"));

        verify(genreService).deleteById("1");
    }
}
