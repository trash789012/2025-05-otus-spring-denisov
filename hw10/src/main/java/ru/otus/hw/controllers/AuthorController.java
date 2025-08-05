package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

@Primary
@Controller
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/authors")
    public String listPage(Model model) {
        return "authors";
    }

    @GetMapping({"/author/{id}", "/author/new"})
    public String editPage(@PathVariable(required = false) String id, Model model) {
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
