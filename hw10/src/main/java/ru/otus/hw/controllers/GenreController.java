package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Controller
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/genres")
    public String listPage(Model model) {
        return "genres";
    }

    @GetMapping({"/genre/{id}", "/genre/new"})
    public String editPage(@PathVariable(required = false) String id, Model model) {
        return "genre";
    }

//    @PostMapping("/genre")
//    public String saveGenre(@ModelAttribute("genre") GenreDto genreDto) {
//        GenreDto savedGenre = (genreDto.id() != null)
//                ? genreService.update(genreDto)
//                : genreService.insert(genreDto);
//
//        return "redirect:/genre/" + savedGenre.id();
//    }
//
//    @PostMapping("/genreDelete")
//    public String deleteGenre(@RequestParam("genreId") String id) {
//        genreService.deleteById(id);
//        return "redirect:/genres";
//    }

}
