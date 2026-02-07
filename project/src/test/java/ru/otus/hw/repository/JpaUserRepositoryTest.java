package ru.otus.hw.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.domain.Group;
import ru.otus.hw.domain.User;
import ru.otus.hw.domain.enums.UserRole;
import ru.otus.hw.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий JPA для работы с пользователями")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class JpaUserRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User testUser2;

    @BeforeEach
    void setUp() {
        List<UserRole> userAndAdminRoles = new ArrayList<>();
        userAndAdminRoles.add(UserRole.USER);
        userAndAdminRoles.add(UserRole.ADMIN);

        testUser = User.builder()
                .name("testuser")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .shortDescription("Test user description")
                .roles(userAndAdminRoles)
                .build();

        List<UserRole> userRoles = new ArrayList<>();
        userRoles.add(UserRole.USER);

        testUser2 = User.builder()
                .name("anotheruser")
                .password("password456")
                .firstName("Another")
                .lastName("User")
                .shortDescription("Another user description")
                .roles(userRoles)
                .build();

        testUser = em.persistAndFlush(testUser);
        testUser2 = em.persistAndFlush(testUser2);

        Group testGroup = Group.builder()
                .name("testgroup")
                .description("Test group description")
                .members(List.of(testUser, testUser2))
                .build();

        testGroup = em.persistAndFlush(testGroup);

        List<Group> groups = new ArrayList<>();
        groups.add(testGroup);
        // Обновляем пользователей с группой (связь many-to-many)
        testUser.setGroups(groups);
        testUser2.setGroups(groups);
        em.merge(testUser);
        em.merge(testUser2);
        em.flush();
        em.clear(); // Очищаем контекст для чистоты тестов
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional при поиске несуществующего пользователя")
    void shouldReturnEmptyWhenUserNotFound() {
        // When
        Optional<User> foundUser = userRepository.findByName("nonexistent");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Должен найти пользователя по имени с ролями")
    void shouldFindUserByNameWithRoles() {
        // When
        Optional<User> foundUser = userRepository.findByName("testuser");

        // Then
        assertThat(foundUser).isPresent();
        User user = foundUser.get();
        assertThat(user.getName()).isEqualTo("testuser");
        assertThat(user.getFirstName()).isEqualTo("Test");
        assertThat(user.getLastName()).isEqualTo("User");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getShortDescription()).isEqualTo("Test user description");
        assertThat(user.getRoles())
                .containsExactlyInAnyOrder(UserRole.USER, UserRole.ADMIN);
    }

    @Test
    @DisplayName("Должен найти пользователя по имени с группами и участниками групп")
    void shouldFindUserByNameWithGroupsAndMembers() {
        // When
        Optional<User> foundUser = userRepository.findByNameWithGroupsAndMembers("testuser");

        // Then
        assertThat(foundUser).isPresent();
        User user = foundUser.get();
        assertThat(user.getName()).isEqualTo("testuser");
        assertThat(user.getGroups()).isNotEmpty();

        Group group = user.getGroups().get(0);
        assertThat(group.getName()).isEqualTo("testgroup");
        assertThat(group.getDescription()).isEqualTo("Test group description");
        assertThat(group.getMembers()).hasSize(2);
        assertThat(group.getMembers())
                .extracting(User::getName)
                .containsExactlyInAnyOrder("testuser", "anotheruser");
    }

    @Test
    @DisplayName("Должен найти ID и имя пользователя по имени")
    void shouldFindIdAndNameByName() {
        // When
        Optional<User> result = userRepository.findIdAndNameByName("testuser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(testUser.getId());
        assertThat(result.get().getName()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Должен найти пользователей по поисковому термину (case insensitive)")
    void shouldFindBySearchTermCaseInsensitive() {
        // When
        List<User> users = userRepository.findBySearchTerm("TEST");

        // Then
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Должен найти пользователей по поисковому термину в имени")
    void shouldFindBySearchTermInName() {
        // When
        List<User> users = userRepository.findBySearchTerm("another");

        // Then
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("anotheruser");
    }

    @Test
    @DisplayName("Должен найти пользователей по поисковому термину в фамилии")
    void shouldFindBySearchTermInLastName() {
        // When
        List<User> users = userRepository.findBySearchTerm("User");

        // Then
        assertThat(users).hasSize(2);
        assertThat(users)
                .extracting(User::getName)
                .containsExactlyInAnyOrder("testuser", "anotheruser");
    }

    @Test
    @DisplayName("Должен найти пользователей по поисковому термину в имени")
    void shouldFindBySearchTermInFirstName() {
        // When
        List<User> users = userRepository.findBySearchTerm("Test");

        // Then
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Должен найти пользователей по поисковому термину с исключением ID")
    void shouldFindBySearchTermAndIdNotIn() {
        // Given
        List<Long> excludedIds = List.of(testUser.getId());

        // When
        List<User> users = userRepository.findBySearchTermAndIdNotIn("User", excludedIds);

        // Then
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("anotheruser");
        assertThat(users.get(0).getId()).isEqualTo(testUser2.getId());
    }

    @Test
    @DisplayName("Должен вернуть пустой список при поиске с исключением всех ID")
    void shouldReturnEmptyWhenAllIdsExcluded() {
        // Given
        List<Long> excludedIds = List.of(testUser.getId(), testUser2.getId());

        // When
        List<User> users = userRepository.findBySearchTermAndIdNotIn("User", excludedIds);

        // Then
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("Должен найти всех пользователей по списку ID")
    void shouldFindAllById() {
        // Given
        List<Long> ids = List.of(testUser.getId(), testUser2.getId());

        // When
        List<User> users = userRepository.findAllById(ids);

        // Then
        assertThat(users).hasSize(2);
        assertThat(users)
                .extracting(User::getName)
                .containsExactlyInAnyOrder("testuser", "anotheruser");
    }

    @Test
    @DisplayName("Должен вернуть пустой список при поиске по несуществующим ID")
    void shouldReturnEmptyWhenFindByNonExistentIds() {
        // Given
        List<Long> nonExistentIds = List.of(999L, 1000L);

        // When
        List<User> users = userRepository.findAllById(nonExistentIds);

        // Then
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("Должен сохранить нового пользователя с ролями")
    void shouldSaveNewUserWithRoles() {
        // Given
        User newUser = User.builder()
                .name("newuser")
                .password("newpass")
                .firstName("New")
                .lastName("User")
                .shortDescription("New user description")
                .roles(List.of(UserRole.USER, UserRole.ADMIN))
                .build();

        // When
        User savedUser = userRepository.save(newUser);
        em.flush();
        em.clear();

        // Then
        User foundUser = userRepository.findByName("newuser").orElseThrow();
        assertThat(foundUser.getName()).isEqualTo("newuser");
        assertThat(foundUser.getFirstName()).isEqualTo("New");
        assertThat(foundUser.getLastName()).isEqualTo("User");
        assertThat(foundUser.getRoles())
                .containsExactlyInAnyOrder(UserRole.USER, UserRole.ADMIN);
    }

    @Test
    @DisplayName("Должен обновить существующего пользователя")
    void shouldUpdateExistingUser() {
        // Given
        User userToUpdate = userRepository.findByName("testuser").orElseThrow();
        userToUpdate.setFirstName("Updated");
        userToUpdate.setLastName("Name");
        userToUpdate.setShortDescription("Updated description");

        // When
        User updatedUser = userRepository.save(userToUpdate);
        em.flush();
        em.clear();

        // Then
        User foundUser = userRepository.findByName("testuser").orElseThrow();
        assertThat(foundUser.getFirstName()).isEqualTo("Updated");
        assertThat(foundUser.getLastName()).isEqualTo("Name");
        assertThat(foundUser.getShortDescription()).isEqualTo("Updated description");
    }

    @Test
    @DisplayName("Должен удалить пользователя")
    void shouldDeleteUser() {
        // Given
        Long userId = testUser.getId();

        // Сначала разрываем связи пользователя с группами
        User userToDelete = userRepository.findById(userId).orElseThrow();
        for (Group group : userToDelete.getGroups()) {
            group.getMembers().remove(userToDelete);
        }
        userToDelete.getGroups().clear();
        userRepository.save(userToDelete);
        em.flush();
        em.clear();

        // When
        userRepository.deleteById(userId);
        em.flush();
        em.clear();

        // Then
        Optional<User> foundUser = userRepository.findById(userId);
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Должен проверить уникальность имени пользователя")
    void shouldEnforceUsernameUniqueness() {
        // Given
        User duplicateUser = User.builder()
                .name("testuser") // Такое же имя как у testUser
                .password("password")
                .firstName("Duplicate")
                .lastName("User")
                .roles(List.of(UserRole.USER))
                .build();

        // When & Then
        // При сохранении пользователя с существующим именем должна быть ошибка уникальности
        try {
            userRepository.save(duplicateUser);
            em.flush();
            // Если дошли сюда, значит ограничение уникальности не сработало
            throw new AssertionError("Expected constraint violation for duplicate username");
        } catch (Exception e) {
            // Ожидаемое поведение - исключение из-за нарушения уникальности
            assertThat(e).isNotNull();
        }
    }

}
