package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.services.AuthorService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = {AuthorController.class})
class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private AuthorRepository authorRepository;

    @Test
    void testGetAllAuthors() throws Exception {
        List<AuthorDto> authors = List.of(
                new AuthorDto("1", "Author 1"),
                new AuthorDto("2", "Author 2")
        );

        given(authorService.findAll()).willReturn(authors);

        mvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors"))
                .andExpect(model().attribute("authors", hasSize(2)));

        verify(authorService, Mockito.times(1)).findAll();
    }

    @Test
    void shouldDeleteAuthor() throws Exception {
        mvc.perform(post("/authorDelete")
                        .param("authorId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authors"));

        verify(authorService).deleteById("1");
    }

    @Test
    void shouldReturnEditPageForExistingAuthor() throws Exception {
        AuthorDto author = new AuthorDto("1", "Existing Author");

        Mockito.when(authorService.findById("1")).thenReturn(Optional.of(author));

        MvcResult result = mvc.perform(get("/author/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("author"))
                .andReturn();

        AuthorDto modelAuthor = (AuthorDto) result.getModelAndView().getModel().get("author");
        assertThat(modelAuthor.id()).isEqualTo("1");
        assertThat(modelAuthor.fullName()).isEqualTo("Existing Author");
    }

    @Test
    void shouldReturnEditPageForNewAuthor() throws Exception {
        MvcResult result = mvc.perform(get("/author/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("author"))
                .andReturn();

        AuthorDto actualAuthor = (AuthorDto) Objects.requireNonNull(result.getModelAndView()).getModel().get("author");
        assertThat(actualAuthor.id()).isNull();
        assertThat(actualAuthor.fullName()).isNull();
    }

    @Test
    void shouldRedirectToCustomErrorWhenAuthorNotFound() throws Exception {
        String nonExistentId = "99";
        Mockito.when(authorService.findById(nonExistentId))
                .thenReturn(Optional.empty());

        mvc.perform(get("/author/{id}", nonExistentId))
                .andExpect(status().isOk())
                .andExpect(view().name("customError"))
                .andExpect(model().attributeExists("errorText"));
    }

    @Test
    void shouldCreateNewAuthor() throws Exception {
        AuthorDto newAuthor = new AuthorDto(null, "New Author");
        AuthorDto savedAuthor = new AuthorDto("1", "New Author");
        Mockito.when(authorService.insert(newAuthor)).thenReturn(savedAuthor);

        mvc.perform(post("/author")
                        .param("fullName", "New Author"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/author/1"));
    }

    @Test
    void shouldUpdateExistingAuthor() throws Exception {
        AuthorDto existingAuthor = new AuthorDto("1", "Updated Author");
        Mockito.when(authorService.update(existingAuthor)).thenReturn(existingAuthor);

        mvc.perform(post("/author")
                        .param("id", "1")
                        .param("fullName", "Updated Author"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/author/1"));
    }

}