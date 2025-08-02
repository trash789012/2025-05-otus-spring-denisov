package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/authors")
    public String listPage(Model model) {
        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        return "authors";
    }

    @GetMapping({"/author/{id}", "/author/new"})
    public String editPage(@PathVariable(required = false) String id, Model model) {
        AuthorDto author = (id != null)
                ? authorService.findById(id)
                .orElseThrow(EntityNotFoundException::new)
                : new AuthorDto(null, null);

        model.addAttribute("author", author);

        return "author";
    }

    @PostMapping("/author")
    public String saveAuthor(@ModelAttribute("author") AuthorDto authorDto) {
        AuthorDto savedAuthor = (authorDto.id() != null)
                ? authorService.update(authorDto)
                : authorService.insert(authorDto);

        return "redirect:/author/" + savedAuthor.id();
    }

    @PostMapping("/authorDelete")
    public String deleteAuthor(@RequestParam("authorId") String id) {
        authorService.deleteById(id);
        return "redirect:/authors";
    }

}
