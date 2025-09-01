package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;

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
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "index";
    }

    @GetMapping({"/book/{id}", "/book/new"})
    public String editPage(@PathVariable(required = false) String id, Model model) {
        BookDto book = (id != null)
                ? bookService.findById(id)
                .orElseThrow(EntityNotFoundException::new)
                : new BookDto(null, null, null, null);

        model.addAttribute(
                "book",
                bookConverter.bookDtoToBookFormDto(book));

        model.addAttribute("allAuthors", authorService.findAll());
        model.addAttribute("allGenres", genreService.findAll());

        if (id != null) {
            model.addAttribute("bookComments", commentService.findByBookId(id));
        }

        return "book";
    }

    @PostMapping("/book")
    public String saveBook(@ModelAttribute("book") BookFormDto bookDto) {
        BookDto savedBook = (bookDto.id() != null)
                ? bookService.update(bookDto)
                : bookService.insert(bookDto);

        return "redirect:/book/" + savedBook.id();
    }

    @PostMapping("/bookDelete")
    public String deleteBook(@RequestParam("bookId") String id) {
        bookService.deleteById(id);
        return "redirect:/";
    }
}
