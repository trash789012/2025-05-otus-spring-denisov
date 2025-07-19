package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Primary
@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    public static final String BOOK_AUTHOR_ENTITY_GRAPH = "book-author-entity-graph";

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Book> findById(long id) {

        EntityGraph<?> entityGraph = em.getEntityGraph(BOOK_AUTHOR_ENTITY_GRAPH);

        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put(FETCH.getKey(), entityGraph);

        return Optional.ofNullable(em.find(Book.class, id, parameters));
    }

    @Override
    public List<Book> findAll() {
        //language=jpql
        String jpql = """
                        select b 
                          from Book b
                        """;
        EntityGraph<?> entityGraph = em.getEntityGraph(BOOK_AUTHOR_ENTITY_GRAPH);

        TypedQuery<Book> query = em.createQuery(jpql, Book.class);
        query.setHint(FETCH.getKey(), entityGraph);

        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        }
        return em.merge(book);
    }

    @Override
    public void deleteById(long id) {
        Book book = em.find(Book.class, id);
        em.remove(book);
    }
}
