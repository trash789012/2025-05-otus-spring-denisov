package ru.otus.hw.services;

import ru.otus.hw.dto.GenreDto;

import java.util.List;
import java.util.Set;

public interface GenreService {
    List<GenreDto> findAll();

    List<GenreDto> findByIds(Set<String> ids);

    GenreDto insert(GenreDto genreDto);

    GenreDto update(GenreDto genreDto);

    void deleteById(String id);
}
