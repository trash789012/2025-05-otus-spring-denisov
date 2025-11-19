package ru.otus.hw.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.otus.hw.domain.Group;
import ru.otus.hw.domain.Slot;
import ru.otus.hw.domain.User;
import ru.otus.hw.domain.enums.SlotStatus;
import ru.otus.hw.domain.enums.UserRole;
import ru.otus.hw.repositories.GroupRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий JPA для работы с группами")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class JpaGroupRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private GroupRepository groupRepository;

    private Group testGroup;
    private Group testGroup2;
    private User testUser;

    @BeforeEach
    void setUp() {
        List<UserRole> userRoles = new ArrayList<>();
        userRoles.add(UserRole.USER);

        testUser = User.builder()
                .name("testuser")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .roles(userRoles)
                .build();

        User testUser2 = User.builder()
                .name("anotheruser")
                .password("password456")
                .firstName("Another")
                .lastName("User")
                .roles(userRoles)
                .build();

        testUser = em.persistAndFlush(testUser);
        testUser2 = em.persistAndFlush(testUser2);

        List<User> users = new ArrayList<>();
        users.add(testUser);
        users.add(testUser2);

        // Создаем тестовые группы
        testGroup = Group.builder()
                .name("testgroup")
                .description("Test group description")
                .members(users)
                .build();

        List<User> users2 = new ArrayList<>();
        users2.add(testUser);

        testGroup2 = Group.builder()
                .name("anothergroup")
                .description("Another group description")
                .members(users2)
                .build();

        testGroup = em.persistAndFlush(testGroup);
        testGroup2 = em.persistAndFlush(testGroup2);

        LocalDateTime now = LocalDateTime.now();
        Slot testSlot = Slot.builder()
                .startTime(now.plusHours(1))
                .endTime(now.plusHours(2))
                .status(SlotStatus.FREE)
                .bookedBy(testGroup)
                .build();

        testSlot = em.persistAndFlush(testSlot);

        List<Slot> slots = new ArrayList<>();
        slots.add(testSlot);

        testGroup.setSlots(slots);
        em.merge(testGroup);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("Должен найти группу по ID с загруженными участниками")
    void shouldFindGroupByIdWithMembers() {
        // When
        Optional<Group> foundGroup = groupRepository.findById(testGroup.getId());

        // Then
        assertThat(foundGroup).isPresent();
        Group group = foundGroup.get();
        assertThat(group.getId()).isEqualTo(testGroup.getId());
        assertThat(group.getName()).isEqualTo("testgroup");
        assertThat(group.getDescription()).isEqualTo("Test group description");

        // Проверяем, что участники загружены через @EntityGraph
        assertThat(group.getMembers()).hasSize(2);
        assertThat(group.getMembers())
                .extracting(User::getName)
                .containsExactlyInAnyOrder("testuser", "anotheruser");
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional при поиске несуществующей группы")
    void shouldReturnEmptyWhenGroupNotFound() {
        // When
        Optional<Group> foundGroup = groupRepository.findById(999L);

        // Then
        assertThat(foundGroup).isEmpty();
    }

    @Test
    @DisplayName("Должен проверить существование участника в группе")
    void shouldCheckIfMemberExistsInGroup() {
        // When & Then
        assertThat(groupRepository.existsMemberInGroup(testGroup.getId(), "testuser")).isTrue();
        assertThat(groupRepository.existsMemberInGroup(testGroup.getId(), "anotheruser")).isTrue();
        assertThat(groupRepository.existsMemberInGroup(testGroup2.getId(), "testuser")).isTrue();
    }

    @Test
    @DisplayName("Должен вернуть false при проверке несуществующего участника в группе")
    void shouldReturnFalseWhenMemberNotInGroup() {
        // When & Then
        assertThat(groupRepository.existsMemberInGroup(testGroup.getId(), "nonexistent")).isFalse();
        assertThat(groupRepository.existsMemberInGroup(testGroup2.getId(), "anotheruser")).isFalse();
    }

    @Test
    @DisplayName("Должен вернуть false при проверке участника в несуществующей группе")
    void shouldReturnFalseWhenGroupNotExists() {
        // When & Then
        assertThat(groupRepository.existsMemberInGroup(999L, "testuser")).isFalse();
    }

    @Test
    @DisplayName("Должен сохранить новую группу с участниками")
    void shouldSaveNewGroupWithMembers() {
        // Given
        User newUser = User.builder()
                .name("newuser")
                .password("newpass")
                .firstName("New")
                .lastName("User")
                .roles(List.of(UserRole.USER))
                .build();
        newUser = em.persistAndFlush(newUser);

        List<User> users = new ArrayList<>();
        users.add(newUser);

        Group newGroup = Group.builder()
                .name("newgroup")
                .description("New group description")
                .members(users)
                .build();

        // When
        Group savedGroup = groupRepository.save(newGroup);
        em.flush();
        em.clear();

        // Then
        Group foundGroup = groupRepository.findById(savedGroup.getId()).orElseThrow();
        assertThat(foundGroup.getName()).isEqualTo("newgroup");
        assertThat(foundGroup.getDescription()).isEqualTo("New group description");
        assertThat(foundGroup.getMembers()).hasSize(1);
        assertThat(foundGroup.getMembers().get(0).getName()).isEqualTo("newuser");
    }

    @Test
    @DisplayName("Должен обновить существующую группу")
    void shouldUpdateExistingGroup() {
        // Given
        Group groupToUpdate = groupRepository.findById(testGroup.getId()).orElseThrow();
        groupToUpdate.setDescription("Updated description");
        groupToUpdate.setName("updatedgroup");

        // When
        Group updatedGroup = groupRepository.save(groupToUpdate);
        em.flush();
        em.clear();

        // Then
        Group foundGroup = groupRepository.findById(testGroup.getId()).orElseThrow();
        assertThat(foundGroup.getName()).isEqualTo("updatedgroup");
        assertThat(foundGroup.getDescription()).isEqualTo("Updated description");
        // Участники должны остаться неизменными
        assertThat(foundGroup.getMembers()).hasSize(2);
    }

    @Test
    @DisplayName("Должен удалить группу")
    void shouldDeleteGroup() {
        // Given
        Long groupId = testGroup.getId();

        Group groupToDelete = em.find(Group.class, groupId);

        // Удаляем связи в правильном порядке через EntityManager
        // 1. Удаляем слоты (orphanRemoval = true сработает при установке null)
        for (Slot slot : groupToDelete.getSlots()) {
            slot.setBookedBy(null);
            em.remove(slot);
        }
        groupToDelete.getSlots().clear();

        // 2. Разрываем связи с пользователями
        for (User member : groupToDelete.getMembers()) {
            member.getGroups().remove(groupToDelete);
        }
        groupToDelete.getMembers().clear();

        em.flush();
        em.clear();

        // When
        groupRepository.deleteById(groupId);
        em.flush();
        em.clear();

        // Then
        Optional<Group> foundGroup = groupRepository.findById(groupId);
        assertThat(foundGroup).isEmpty();
    }

    @Test
    @DisplayName("Должен найти все группы")
    void shouldFindAllGroups() {
        // When
        List<Group> allGroups = groupRepository.findAll();

        // Then
        assertThat(allGroups).hasSize(2);
        assertThat(allGroups)
                .extracting(Group::getName)
                .containsExactlyInAnyOrder("testgroup", "anothergroup");
    }

    @Test
    @DisplayName("Должен добавить участника в группу")
    void shouldAddMemberToGroup() {
        // Given
        User newMember = User.builder()
                .name("newmember")
                .password("password")
                .firstName("New")
                .lastName("Member")
                .roles(List.of(UserRole.USER))
                .build();
        newMember = em.persistAndFlush(newMember);

        Group group = groupRepository.findById(testGroup.getId()).orElseThrow();
        group.getMembers().add(newMember);

        // When
        Group updatedGroup = groupRepository.save(group);
        em.flush();
        em.clear();

        // Then
        Group foundGroup = groupRepository.findById(testGroup.getId()).orElseThrow();
        assertThat(foundGroup.getMembers()).hasSize(3);
        assertThat(groupRepository.existsMemberInGroup(testGroup.getId(), "newmember")).isTrue();
    }

    @Test
    @DisplayName("Должен удалить участника из группы")
    void shouldRemoveMemberFromGroup() {
        // Given
        Group group = groupRepository.findById(testGroup.getId()).orElseThrow();
        group.getMembers().removeIf(member -> member.getName().equals("anotheruser"));

        // When
        Group updatedGroup = groupRepository.save(group);
        em.flush();
        em.clear();

        // Then
        Group foundGroup = groupRepository.findById(testGroup.getId()).orElseThrow();
        assertThat(foundGroup.getMembers()).hasSize(1);
        assertThat(foundGroup.getMembers().get(0).getName()).isEqualTo("testuser");
        assertThat(groupRepository.existsMemberInGroup(testGroup.getId(), "anotheruser")).isFalse();
    }

    @Test
    @DisplayName("Должен проверить уникальность имени группы")
    void shouldEnforceGroupNameUniqueness() {
        // Given
        Group duplicateGroup = Group.builder()
                .name("testgroup") // Такое же имя как у testGroup
                .description("Duplicate group")
                .members(List.of(testUser))
                .build();

        // When & Then
        // При сохранении группы с существующим именем должна быть ошибка уникальности
        try {
            groupRepository.save(duplicateGroup);
            em.flush();
            // Если дошли сюда, значит ограничение уникальности не сработало
            throw new AssertionError("Expected constraint violation for duplicate group name");
        } catch (Exception e) {
            // Ожидаемое поведение - исключение из-за нарушения уникальности
            assertThat(e).isNotNull();
        }
    }

    @Test
    @DisplayName("Должен найти группу по имени через стандартный метод JPA")
    void shouldFindGroupByName() {
        // When
        Optional<Group> foundGroup = groupRepository.findAll().stream()
                .filter(group -> group.getName().equals("testgroup"))
                .findFirst();

        // Then
        assertThat(foundGroup).isPresent();
        assertThat(foundGroup.get().getId()).isEqualTo(testGroup.getId());
        assertThat(foundGroup.get().getDescription()).isEqualTo("Test group description");
    }

    @Test
    @DisplayName("Должен обновить описание группы")
    void shouldUpdateGroupDescription() {
        // Given
        String newDescription = "Completely new description";

        // When
        Group group = groupRepository.findById(testGroup.getId()).orElseThrow();
        group.setDescription(newDescription);
        groupRepository.save(group);
        em.flush();
        em.clear();

        // Then
        Group foundGroup = groupRepository.findById(testGroup.getId()).orElseThrow();
        assertThat(foundGroup.getDescription()).isEqualTo(newDescription);
    }
}
