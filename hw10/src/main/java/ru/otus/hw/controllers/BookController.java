package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final GenreService genreService;

    private final AuthorService authorService;

    private final CommentService commentService;

    private final BookConverter bookConverter;

    @GetMapping("/")
    public String listPage(Model model) {
        return "index";
    }

    @GetMapping({"/book/{id}", "/book/new"})
    public String editPage(@PathVariable(required = false) String id, Model model) {
        return "book";
    }
}
