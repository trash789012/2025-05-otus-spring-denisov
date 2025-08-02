package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.Optional;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final AuthorConverter authorConverter;

    @Override
    @Transactional(readOnly = true)
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream()
                .map(authorConverter::authorToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthorDto> findById(String id) {
        return authorRepository.findById(id)
                .map(authorConverter::authorToDto);
    }

    @Override
    public AuthorDto insert(AuthorDto authorDto) {
        return save(authorDto);
    }

    @Override
    public AuthorDto update(AuthorDto authorDto) {
        return save(authorDto);
    }

    @Override
    public void deleteById(String id) {
        authorRepository.deleteById(id);
    }

    private AuthorDto save(AuthorDto authorDto) {
        if (authorDto.fullName() == null) {
            throw new IllegalArgumentException("Author name is empty");
        }

        Author author;
        if (authorDto.id() == null) {
            author = new Author();
        } else {
            author = authorRepository.findById(authorDto.id())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Author with id %s not found".formatted(authorDto.id()))
                    );
        }

        author.setId(authorDto.id());
        author.setFullName(authorDto.fullName());

        return authorConverter.authorToDto(authorRepository.save(author));
    }
}
