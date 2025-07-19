package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

@Primary
@RequiredArgsConstructor
@Repository
public class JpaGenreRepository implements GenreRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public List<Genre> findAll() {
        //language=jpql
        String jpql = """
                 SELECT g 
                   FROM Genre g 
                """;
        return em.createQuery(jpql, Genre.class).getResultList();
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {

        //language=jpql
        String jpql = """
                SELECT g 
                  FROM Genre g
                  WHERE g.id IN (:ids)
                """;

        TypedQuery<Genre> query = em.createQuery(jpql, Genre.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }
}
