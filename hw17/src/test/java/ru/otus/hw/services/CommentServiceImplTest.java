package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@DisplayName("Интеграционный тест сервиса комментариев ")
@Import({CommentServiceImpl.class, CommentConverter.class, TestMongoConfig.class})
public class CommentServiceImplTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    private final CommentService commentService;

    @Autowired
    CommentServiceImplTest(CommentService commentService) {
        this.commentService = commentService;
    }

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();
    }

    @Test
    @DisplayName("должен находить комментарий по id")
    void shouldFindCommentById() {
        Author author = new Author("Author");
        mongoTemplate.save(author);

        Book book = new Book("Book");
        book.setAuthor(author);
        mongoTemplate.save(book);

        Comment comment = new Comment("Comment");
        comment.setBook(book);
        mongoTemplate.save(comment);

        Optional<CommentDto> savedComment = commentService.findById(comment.getId());

        assertThat(savedComment).isPresent();
        assertThat(savedComment.get())
                .extracting(CommentDto::text, CommentDto::bookId)
                .containsExactly(comment.getText(), book.getId());
    }

    @Test
    @DisplayName("должен находить комментарии по id книги")
    void shouldFindCommentByBookId() {
        Author author = new Author("Author");
        mongoTemplate.save(author);

        Book book = new Book("Book");
        book.setAuthor(author);
        mongoTemplate.save(book);

        Comment comment1 = new Comment("Comment");
        comment1.setBook(book);
        mongoTemplate.save(comment1);

        Comment comment2 = new Comment("Comment2");
        comment2.setBook(book);
        mongoTemplate.save(comment2);

        var savedComments = commentService.findByBookId(book.getId());

        assertThat(savedComments).isNotNull().isNotEmpty().hasSize(2);
        assertThat(savedComments)
                .extracting(CommentDto::text)
                .containsExactlyInAnyOrder(comment1.getText(), comment2.getText());
        assertThat(savedComments)
                .allMatch(i -> i.bookId().equals(book.getId()));

    }

    @Test
    @DisplayName("должен создавать комментарий")
    void shouldCreateNewComment() {
        Author author = new Author("Author");
        mongoTemplate.save(author);

        Book book = new Book("Book");
        book.setAuthor(author);
        mongoTemplate.save(book);

        var comment = commentService.insert(new CommentDto(null, "Comment", book.getId()));

        var savedComment = mongoTemplate.findById(comment.id(), Comment.class);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getText()).isEqualTo(comment.text()).isEqualTo("Comment");
        assertThat(savedComment.getBook().getId()).isEqualTo(book.getId());
        assertThat(comment.bookId()).isEqualTo(book.getId());
    }

    @Test
    @DisplayName("должен корректно обновлять комментарий")
    void shouldUpdateComment() {
        Author author = new Author("Author");
        mongoTemplate.save(author);

        Book book = new Book("Book");
        book.setAuthor(author);
        mongoTemplate.save(book);

        Comment comment1 = new Comment("Comment");
        comment1.setBook(book);
        mongoTemplate.save(comment1);

        var updatedComment = commentService.update(new CommentDto(comment1.getId(), "New Text", book.getId()));

        var savedComment = mongoTemplate.findById(updatedComment.id(), Comment.class);

        assertThat(savedComment).isNotNull();
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.text()).isEqualTo("New Text");
        assertThat(savedComment.getText()).isEqualTo(updatedComment.text());
        assertThat(savedComment.getBook().getId()).isEqualTo(book.getId());
        assertThat(updatedComment.bookId()).isEqualTo(book.getId());
    }

    @Test
    @DisplayName("должен удалять комментарий")
    void shouldDeleteComment() {
        Author author = new Author("Author");
        mongoTemplate.save(author);

        Book book = new Book("Book");
        book.setAuthor(author);
        mongoTemplate.save(book);

        Comment comment1 = new Comment("Comment");
        comment1.setBook(book);
        mongoTemplate.save(comment1);

        Comment comment2 = new Comment("Comment2");
        comment2.setBook(book);
        mongoTemplate.save(comment2);

        commentService.deleteById(comment1.getId());

        var allComments = mongoTemplate.findAll(Comment.class);
        var deletedComment = mongoTemplate.findById(comment1.getId(), Comment.class);

        assertThat(allComments).hasSize(1);
        assertThat(deletedComment).isNull();
    }


}
