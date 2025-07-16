package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.converters.*;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Интеграционный тест сервиса книг")
@DataJpaTest
@Import({BookServiceImpl.class,
        JpaAuthorRepository.class,
        JpaGenreRepository.class,
        JpaBookRepository.class,
        JpaCommentRepository.class,
        BookConverter.class,
        AuthorConverter.class,
        GenreConverter.class,
        CommentConverter.class,
        BookCondensedConverter.class})
public class BookServiceImplTest {

    private final BookService bookService;

    @Autowired
    public BookServiceImplTest(BookService bookService) {
        this.bookService = bookService;
    }

    @Test
    @DisplayName("Должен находить книгу по id и всю информацию")
    public void findBookByIdShouldNotThrowLazyException() {
        var optionalBook = bookService.findById(1L);
        assertThat(optionalBook).isPresent();

        var book = optionalBook.get();

        //обращаемся ко всем элементам
        assertThat(book.id()).isGreaterThan(0);
        assertThat(book.title()).isNotBlank();

        assertThat(book.author()).isNotNull();
        assertThat(book.author().fullName()).isNotBlank();

        assertThat(book.genres()).isNotEmpty();
        assertThat(book.genres()).allSatisfy(genreDto -> {
            assertThat(genreDto.id()).isGreaterThan(0);
            assertThat(genreDto.name()).isNotBlank();
        });

        assertThat(book.comments()).isNotNull();
    }

}
