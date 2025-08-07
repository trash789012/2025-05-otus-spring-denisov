package ru.otus.hw.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
@Import({LocalValidatorFactoryBean.class})
class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorService authorService;

    @Test
    void shouldReturnAllAuthors() throws Exception {
        List<AuthorDto> authors = List.of(
                new AuthorDto("1", "Author1"),
                new AuthorDto("2", "Author2")
        );

        when(authorService.findAll()).thenReturn(authors);

        mvc.perform(get("/api/v1/author"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].fullName", is("Author1")))
                .andExpect(jsonPath("$[1].id", is("2")))
                .andExpect(jsonPath("$[1].fullName", is("Author2")));
    }

    @Test
    void shouldReturnEmptyAuthorsList() throws Exception {
        when(authorService.findAll()).thenReturn(List.of());

        mvc.perform(get("/api/v1/author"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnAuthorById() throws Exception {
        AuthorDto author = new AuthorDto("1", "Author1");
        when(authorService.findById("1")).thenReturn(Optional.of(author));

        mvc.perform(get("/api/v1/author/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.fullName", is("Author1")));
    }

    @Test
    void shouldReturnNotFoundWhenAuthorNotExist() throws Exception {
        when(authorService.findById("100")).thenReturn(Optional.empty());

        mvc.perform(get("/api/v1/author/100"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateAuthor() throws Exception {
        AuthorDto authorToCreate = new AuthorDto(null, "NewAuthor");
        AuthorDto createdAuthor = new AuthorDto("1", "NewAuthor");

        when(authorService.insert(any(AuthorDto.class))).thenReturn(createdAuthor);

        mvc.perform(post("/api/v1/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.fullName", is("NewAuthor")));
    }

    @Test
    void shouldValidateWhenCreatingInvalidAuthor() throws Exception {
        AuthorDto invalidDto = new AuthorDto(null, null);

        mvc.perform(post("/api/v1/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].defaultMessage").exists());
    }

    @Test
    void shouldValidateWhenUpdatingInvalidAuthor() throws Exception {
        AuthorDto invalidDto = new AuthorDto("1", null);

        mvc.perform(put("/api/v1/author/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].defaultMessage").exists());
    }

    @Test
    void shouldUpdateAuthor() throws Exception {
        AuthorDto authorToUpdate = new AuthorDto("1", "UpdatedAuthor");
        when(authorService.update(any(AuthorDto.class))).thenReturn(authorToUpdate);

        mvc.perform(put("/api/v1/author/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.fullName", is("UpdatedAuthor")));
    }

    @Test
    void shouldReturnBadRequestWhenIdsMismatch() throws Exception {
        AuthorDto authorToUpdate = new AuthorDto("2", "UpdatedAuthor");

        mvc.perform(put("/api/v1/author/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorToUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteAuthor() throws Exception {
        mvc.perform(delete("/api/v1/author/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(authorService).deleteById("1");
    }
}
