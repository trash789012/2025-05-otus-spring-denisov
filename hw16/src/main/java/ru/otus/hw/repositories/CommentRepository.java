package ru.otus.hw.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.Comment;

import javax.annotation.Nonnull;
import java.util.List;

@RepositoryRestResource(path = "comment")
public interface CommentRepository extends CrudRepository<Comment, Long> {
    @RestResource(path = "bookId", rel = "bookId")
    @Nonnull
    List<Comment> findByBookId(long bookId);
}
