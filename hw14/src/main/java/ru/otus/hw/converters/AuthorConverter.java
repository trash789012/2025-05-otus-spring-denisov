package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.h2.Author;

@Component
public class AuthorConverter {
    public String authorDtoToString(AuthorDto author) {
        return "Id: %s, FullName: %s".formatted(author.id(), author.fullName());
    }

    public AuthorDto authorToDto(Author author) {
        if (author != null) {
            return new AuthorDto(author.getId(), author.getFullName());
        } else {
            return null;
        }
    }
}
