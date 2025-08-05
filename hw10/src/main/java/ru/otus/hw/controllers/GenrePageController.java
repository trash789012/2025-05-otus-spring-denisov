package ru.otus.hw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GenrePageController {
    @GetMapping("/genres")
    public String listPage(Model model) {
        return "genres";
    }

    @GetMapping({"/genre/{id}", "/genre/new"})
    public String editPage(@PathVariable(required = false) String id, Model model) {
        return "genre";
    }
}
