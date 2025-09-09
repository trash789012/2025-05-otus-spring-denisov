package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final GenreConverter genreConverter;

    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream()
                .map(genreConverter::genreToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> findByIds(Set<Long> ids) {
        return genreRepository.findAllById(ids).stream()
                .map(genreConverter::genreToDto)
                .toList();
    }

    @Override
    @Transactional
    public GenreDto insert(GenreDto genreDto) {
        return save(genreDto);
    }

    @Override
    @Transactional
    public GenreDto update(GenreDto genreDto) {
        return save(genreDto);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        genreRepository.deleteById(id);
    }

    private GenreDto save(GenreDto genreDto) {
        if (genreDto.name() == null) {
            throw new IllegalArgumentException("Genre name is empty");
        }

        Genre genre;
        if (genreDto.id() == 0) {
            genre = new Genre();
        } else {
            genre = genreRepository.findById(genreDto.id())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Genre with id %s not found".formatted(genreDto.id()))
                    );
        }

        genre.setId(genreDto.id());
        genre.setName(genreDto.name());

        return genreConverter.genreToDto(genreRepository.save(genre));
    }
}
