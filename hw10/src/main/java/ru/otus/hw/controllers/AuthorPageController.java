package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class AuthorPageController {
    @GetMapping("/authors")
    public String listPage(Model model) {
        return "authors";
    }

    @GetMapping({"/author/{id}", "/author/new"})
    public String editPage(@PathVariable(required = false) String id, Model model) {
        return "author";
    }
}
