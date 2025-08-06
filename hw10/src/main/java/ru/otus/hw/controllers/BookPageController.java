package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class BookPageController {

    @GetMapping("/")
    public String listPage(Model model) {
        return "index";
    }

    @GetMapping({"/book/{id}", "/book/new"})
    public String editPage(@PathVariable(required = false) String id, Model model) {
        return "book";
    }
}
