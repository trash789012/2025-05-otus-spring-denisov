package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import ru.otus.hw.config.acl.AclConfig;
import ru.otus.hw.config.acl.CacheConfig;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookFormDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest()
@Import({
        BookServiceImpl.class,
        BookConverter.class,
        AuthorConverter.class,
        GenreConverter.class,
        AclServiceWrapperServiceImpl.class,
        AclConfig.class,
        CacheConfig.class
})
@EnableMethodSecurity
public class BookServiceImplSecurityTest {

    @Autowired
    private BookService bookService;

    @Test
    @WithMockUser(username = "user")
    void shouldAllowOwnerToUpdateBook() {
        BookFormDto bookDto = new BookFormDto(0, "Title", 1, List.of(1L, 2L));
        BookDto createdBook = bookService.insert(bookDto);

        BookFormDto bookUpdatedDto = new BookFormDto(createdBook.id(), "Upd Title", 1, List.of(1L, 2L));
        BookDto updatedBook = bookService.update(bookUpdatedDto);

        assertThat(updatedBook.title()).isEqualTo("Upd Title");
    }
}
