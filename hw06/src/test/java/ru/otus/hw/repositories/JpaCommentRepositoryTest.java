package ru.otus.hw.repositories;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaCommentRepository.class)
@DisplayName("Репозиторий JPA для работы с комментариями ")
public class JpaCommentRepositoryTest {

    public static final long FIRST_COMMENT_ID = 1L;

    public static final long BOOK_ID = 1L;

    @Autowired
    private JpaCommentRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName(" должен загружать комментарий по id")
    void shouldFindExpectedCommentById() {
        val optionalActualComment = repository.findById(FIRST_COMMENT_ID);
        val expectedComment = em.find(Comment.class, FIRST_COMMENT_ID);

        assertThat(optionalActualComment).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedComment);
    }

    @Test
    @DisplayName(" должен загружать список комментариев по id книги")
    void shouldReturnCorrectCommentsListByBookId() {
        val comments = repository.findByBookId(BOOK_ID);
        val expectedComments = em.find(Book.class, BOOK_ID).getComments();

        assertThat(comments).usingRecursiveComparison().isEqualTo(expectedComments);
    }

    @Test
    @DisplayName(" должен корректно сохранять комментарий")
    void shouldCorrectSaveComment() {
        val book = em.find(Book.class, BOOK_ID);
        Comment comment = new Comment(0, "Comment", book);

        Comment savedComment = repository.save(comment);
        em.flush();
        em.clear();

        assertThat(savedComment.getId()).isGreaterThan(0);
        assertThat(savedComment.getText()).isNotBlank();

        val persistentComment = em.find(Comment.class, savedComment.getId());
        assertThat(persistentComment)
                .isNotNull()
                .extracting(
                        Comment::getText,
                        c -> c.getBook().getId()
                )
                .containsExactly(
                        "Comment",
                        BOOK_ID
                );

    }

    @Test
    @DisplayName(" должен корректно обновлять комментарий")
    void shouldCorrectUpdateComment() {
        Comment comment = em.find(Comment.class, FIRST_COMMENT_ID);
        String newText = "New comment text";
        comment.setText(newText);
        em.detach(comment); //отсоединяем от persistent контекста

        repository.save(comment);
        em.flush();
        em.clear();

        val persistentComment = em.find(Comment.class, FIRST_COMMENT_ID);
        assertThat(persistentComment)
                .isNotNull()
                .extracting(Comment::getText)
                .isEqualTo(newText);
    }

    @Test
    @DisplayName(" должен корректно удалять комментарий по id")
    void shouldDeleteCommentById() {
        Comment comment = em.find(Comment.class, FIRST_COMMENT_ID);
        assertThat(comment).isNotNull();
        em.detach(comment);

        repository.deleteById(FIRST_COMMENT_ID);
        em.flush();
        em.clear();

        assertThat(em.find(Comment.class, FIRST_COMMENT_ID)).isNull();
    }
}
