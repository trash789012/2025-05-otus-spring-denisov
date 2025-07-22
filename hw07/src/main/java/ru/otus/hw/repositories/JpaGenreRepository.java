package ru.otus.hw.repositories;

//@Primary
//@RequiredArgsConstructor
//@Repository
public class JpaGenreRepository {

//    @PersistenceContext
//    private final EntityManager em;
//
//    @Override
//    public List<Genre> findAll() {
//        //language=jpql
//        String jpql = """
//                 SELECT g
//                   FROM Genre g
//                """;
//        return em.createQuery(jpql, Genre.class).getResultList();
//    }
//
//    @Override
//    public List<Genre> findAllByIds(Set<Long> ids) {
//
//        //language=jpql
//        String jpql = """
//                SELECT g
//                  FROM Genre g
//                  WHERE g.id IN (:ids)
//                """;
//
//        TypedQuery<Genre> query = em.createQuery(jpql, Genre.class);
//        query.setParameter("ids", ids);
//        return query.getResultList();
//    }
}
