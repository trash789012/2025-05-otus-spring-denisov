package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph("user-roles-graph")
    Optional<User> findByName(String username);

    @EntityGraph(attributePaths = {"groups", "groups.members"})
    @Query("SELECT DISTINCT u FROM User u WHERE u.name = :username")
    Optional<User> findByNameWithGroupsAndMembers(@Param("username") String username);

    Optional<User> findIdAndNameByName(String name);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> findBySearchTerm(@Param("searchTerm") String searchTerm);

    @Query("""
                SELECT u FROM User u
                WHERE (
                    LOWER(u.name) LIKE LOWER(CONCAT('%', :term, '%'))
                    OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :term, '%'))
                    OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :term, '%'))
                )
                AND u.id NOT IN :excludedIds
            """)
    List<User> findBySearchTermAndIdNotIn(@Param("term") String term, @Param("excludedIds") List<Long> excludedIds);

    // Загрузка пользователя с группами
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.groups WHERE u.name = :username")
    Optional<User> findByNameWithGroups(@Param("username") String username);

    // Загрузка нескольких пользователей по ID с ролями (для участников групп)
    @EntityGraph("user-roles-graph")
    @Query("SELECT u FROM User u WHERE u.id IN :userIds")
    List<User> findAllByIdsWithRoles(@Param("userIds") List<Long> userIds);

}