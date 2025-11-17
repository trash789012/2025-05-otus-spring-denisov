package ru.otus.hw.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
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
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Import(UserRepository.class)
public class JpaUserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User testUser2;
    private Group testGroup;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

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

        testGroup = Group.builder()
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

}
