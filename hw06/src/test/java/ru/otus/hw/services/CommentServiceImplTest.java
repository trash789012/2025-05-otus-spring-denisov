package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.converters.*;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Интеграционный тест сервиса комментариев")
@DataJpaTest
@Import({CommentServiceImpl.class,
        CommentConverter.class,
        BookConverter.class,
        BookCondensedConverter.class,
        AuthorConverter.class,
        GenreConverter.class,
        JpaCommentRepository.class,
        JpaBookRepository.class})
public class CommentServiceImplTest {

    public static final long FIRST_COMMENT_ID = 1L;
    public static final long FIRST_BOOK_ID = 1L;

    private final CommentService commentService;

    @Autowired
    public CommentServiceImplTest(CommentService commentService) {
        this.commentService = commentService;
    }

    @Test
    @DisplayName("Должен находить комментарий")
    void findByIdShouldNotThrowLazyException() {
        var optionalComment = commentService.findById(FIRST_COMMENT_ID);

        //чекаем обращение ко всем возможным атрибутам и связанным атрибутам
        assertThat(optionalComment).isPresent();
        assertThat(optionalComment.get().text()).isNotBlank();
        assertThat(optionalComment.get().id()).isEqualTo(FIRST_COMMENT_ID);
        assertThat(optionalComment.get().book().id()).isGreaterThan(0L);
        assertThat(optionalComment.get().book().author().id()).isGreaterThan(0L);
        assertThat(optionalComment.get().book().author().fullName()).isNotBlank();
    }

    @Test
    @DisplayName("Должен находить комментарии по id книги")
    void findByBookIdShouldNotThrowLazyException() {
        var comments = commentService.findByBookId(FIRST_BOOK_ID);

        assertThat(comments).isNotNull().hasSize(2)
                .allSatisfy(comment -> {
                   assertThat(comment.text()).isNotBlank();
                   assertThat(comment.book().id()).isEqualTo(FIRST_BOOK_ID);
                   assertThat(comment.book().author().fullName()).isNotBlank();
                });
    }

    @Test
    @DisplayName("Должен удалять комментарий")
    void deleteByIdShouldNotThrowLazyException() {
        var comment = commentService.findById(FIRST_COMMENT_ID);
        assertThat(comment).isPresent();

        commentService.deleteById(FIRST_COMMENT_ID);
        assertThat(commentService.findById(FIRST_COMMENT_ID)).isEmpty();
    }

    @Test
    @DisplayName("Должен создавать новый комментарий")
    void insertShouldNotThrowLazyException() {

        String text = "New Comment";
        var comment = commentService.insert(text, FIRST_BOOK_ID);

        assertThat(comment.id()).isGreaterThan(0L);
        assertThat(comment.text()).isEqualTo(text);
        assertThat(comment.book().id()).isEqualTo(FIRST_BOOK_ID);

        //находим сохраненный коммент, проверяем его
        var savedComment = commentService.findById(comment.id());
        assertThat(savedComment).isPresent();
        assertThat(savedComment.get().text()).isEqualTo(text);
    }

    @Test
    @DisplayName("Должен обновлять комментарий")
    void updateByIdShouldNotThrowLazyException() {
        String text = "New Comment Text Test";

        var updatedComment = commentService.update(FIRST_COMMENT_ID, text);
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.id()).isEqualTo(FIRST_COMMENT_ID);
        assertThat(updatedComment.text()).isEqualTo(text);

        //находим заново через сервис и проверяем
        var actualComment = commentService.findById(FIRST_COMMENT_ID);
        assertThat(actualComment).isPresent();
        assertThat(actualComment.get().text()).isEqualTo(text);
    }

}
