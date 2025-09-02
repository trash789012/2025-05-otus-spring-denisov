package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final AuthorConverter authorConverter;

    @Override
    public Flux<AuthorDto> findAll() {
        return authorRepository.findAll()
                .map(authorConverter::authorToDto);
    }

    @Override
    public Mono<AuthorDto> findById(String id) {
        return authorRepository.findById(id)
                .map(authorConverter::authorToDto);
    }

    @Override
    public Mono<AuthorDto> insert(AuthorDto authorDto) {
        return save(authorDto);
    }

    @Override
    public Mono<AuthorDto> update(AuthorDto authorDto) {
        return save(authorDto);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return authorRepository.deleteById(id);
    }

    private Mono<AuthorDto> save(AuthorDto authorDto) {
        if (authorDto.fullName() == null) {
            return Mono.error(new IllegalArgumentException("Author name is empty"));
        }

        Mono<Author> authorMono;
        if (authorDto.id() == null) {
            Author newAuthor = new Author();
            newAuthor.setFullName(authorDto.fullName());
            authorMono = Mono.just(newAuthor);
        } else {
            authorMono = authorRepository.findById(authorDto.id())
                    .switchIfEmpty(Mono.error(
                            new EntityNotFoundException("Author with id %s not found".formatted(authorDto.id()))
                    ))
                    .map(existing -> {
                        existing.setFullName(authorDto.fullName());
                        return existing;
                    });
        }

        return authorMono
                .flatMap(authorRepository::save)
                .map(authorConverter::authorToDto);
    }
}
