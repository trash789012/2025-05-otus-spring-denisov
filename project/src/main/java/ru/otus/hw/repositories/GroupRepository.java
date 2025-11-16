package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.domain.Group;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    @EntityGraph("group-members-graph")
    Optional<Group> findById(Long id);

    @EntityGraph("group-members-graph")
    Optional<Group> findByName(String name);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
            "FROM Group g JOIN g.members m " +
            "WHERE g.id = :groupId " +
            "AND m.name = :name")
    boolean existsMemberInGroup(@Param("groupId") Long groupId, @Param("name") String name);}
