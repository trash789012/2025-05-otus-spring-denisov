package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookCondensedDto;
import ru.otus.hw.models.Book;

@Component
@RequiredArgsConstructor
public class BookCondensedConverter {

    private final AuthorConverter authorConverter;

    public String bookCondensedDtoToString(BookCondensedDto bookDto) {
        return "id: %d, title: %s, author: $s".formatted(
                bookDto.id(),
                bookDto.title(),
                bookDto.author().fullName()
        );
    }

    public BookCondensedDto bookToBookCondensedDto(Book book) {
        if (book == null) {
            return null;
        }

        return new BookCondensedDto(
                book.getId(),
                book.getTitle(),
                authorConverter.authorToDto(book.getAuthor())
        );
    }

}
