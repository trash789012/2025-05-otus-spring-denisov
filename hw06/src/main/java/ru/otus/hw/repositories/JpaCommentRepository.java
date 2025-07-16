package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {

    public static final String BOOK_AUTHOR_ENTITY_GRAPH = "comment-book-author-entity-graph";

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Comment> findById(long id) {

        EntityGraph<?> graph = em.getEntityGraph(BOOK_AUTHOR_ENTITY_GRAPH);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(FETCH.getKey(), graph);

        return Optional.ofNullable(em.find(Comment.class, id, attributes));
    }

    @Override
    public List<Comment> findByBookId(long bookId) {
        //language=jpql
        String jpql = """
                 select c 
                   from Comment c 
                   where c.book.id = :bookId
                """;

        EntityGraph<?> graph = em.getEntityGraph(BOOK_AUTHOR_ENTITY_GRAPH);

        TypedQuery<Comment> query = em.createQuery(jpql, Comment.class);
        query.setParameter("bookId", bookId);
        query.setHint(FETCH.getKey(), graph);
        return query.getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        }

        return em.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        Comment comment = em.getReference(Comment.class, id);
        em.remove(comment);
    }
}
