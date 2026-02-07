package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.config.PathConfig;
import ru.otus.hw.config.security.SecurityConfig;
import ru.otus.hw.config.security.jwt.JwtAuthenticationFilter;
import ru.otus.hw.config.security.jwt.JwtTokenProvider;
import ru.otus.hw.converters.UserConverter;
import ru.otus.hw.domain.Group;
import ru.otus.hw.domain.User;
import ru.otus.hw.domain.enums.UserRole;
import ru.otus.hw.dto.user.UserDto;
import ru.otus.hw.dto.user.UserExistsDto;
import ru.otus.hw.dto.user.UserFormInfoDto;
import ru.otus.hw.dto.user.UserFormWithRolesAndPasswordDto;
import ru.otus.hw.dto.user.UserWithRolesAndGroupsDto;
import ru.otus.hw.dto.user.UserWithRolesDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.GroupRepository;
import ru.otus.hw.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        UserServiceImpl.class,
        SecurityConfig.class,
})
@EnableMethodSecurity
@DisplayName("Тесты для сервиса пользователей")
public class UserServiceImplTest {

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private SecurityConfig securityConfig;

    @MockBean
    private PathConfig pathConfig;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GroupRepository groupRepository;

    @MockBean
    private UserConverter userConverter;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Должен вернуть все роли пользователей")
    void shouldFindAllUserRoles() {
        // When
        List<String> roles = userService.findAllUserRoles();

        // Then
        assertThat(roles).containsExactlyInAnyOrder(
                "USER", "ADMIN", "ROOT"
        );
    }

    @Test
    @DisplayName("Должен найти пользователя по ID с ролями и группами")
    void shouldFindUserById() {
        // Given
        Long userId = 1L;
        User user = createTestUser();
        UserWithRolesAndGroupsDto expectedDto = createUserWithRolesAndGroupsDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userConverter.toUserWithRolesAndGroupsDto(user)).thenReturn(expectedDto);

        // When
        UserWithRolesAndGroupsDto result = userService.findUserById(userId);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(userRepository).findById(userId);
        verify(userConverter).toUserWithRolesAndGroupsDto(user);
    }

    @Test
    @DisplayName("Должен выбросить исключение при поиске несуществующего пользователя по ID")
    void shouldThrowExceptionWhenUserNotFoundById() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.findUserById(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Пользователь не найден 999");
    }

    @Test
    @DisplayName("Должен найти всех пользователей с ролями")
    void shouldFindAllUsersWithRoles() {
        // Given
        User user1 = createTestUser();
        User user2 = createTestUser();
        user2.setId(2L);
        user2.setName("anotheruser");

        UserWithRolesDto dto1 = createUserWithRolesDto();

        List<String> userRoles = new ArrayList<>();
        userRoles.add(String.valueOf(UserRole.USER));
        userRoles.add(String.valueOf(UserRole.ADMIN));
        UserWithRolesDto dto2 = new UserWithRolesDto(
                2L,
                "anotheruser",
                "Another",
                "User",
                "",
                userRoles
        );

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userConverter.toUserWithRolesDto(user1)).thenReturn(dto1);
        when(userConverter.toUserWithRolesDto(user2)).thenReturn(dto2);

        // When
        List<UserWithRolesDto> result = userService.findAllUsersWithRoles();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);
        verify(userRepository).findAll();
        verify(userConverter, times(2)).toUserWithRolesDto(any(User.class));
    }

    @Test
    @DisplayName("Должен найти пользователя по имени с группами и участниками")
    void shouldFindByNameWithGroupsAndMembers() {
        // Given
        String username = "testuser";
        User user = createTestUser();
        UserDto expectedDto = createUserDto();

        when(userRepository.findByNameWithGroupsAndMembers(username)).thenReturn(Optional.of(user));
        when(userConverter.toDto(user)).thenReturn(expectedDto);

        // When
        UserDto result = userService.findByNameWithGroupsAndMembers(username);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(userRepository).findByNameWithGroupsAndMembers(username);
        verify(userConverter).toDto(user);
    }

    @Test
    @DisplayName("Должен выбросить исключение при поиске несуществующего пользователя по имени")
    void shouldThrowExceptionWhenUserNotFoundByName() {
        // Given
        String username = "nonexistent";
        when(userRepository.findByNameWithGroupsAndMembers(username)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.findByNameWithGroupsAndMembers(username))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Пользователь с логином nonexistent не найден");
    }

    @Test
    @DisplayName("Должен найти ID и имя пользователя по логину")
    void shouldFindIdByName() {
        // Given
        String username = "testuser";
        User user = createTestUser();
        UserExistsDto expectedDto = createUserExistsDto();

        when(userRepository.findIdAndNameByName(username)).thenReturn(Optional.of(user));
        when(userConverter.toExistsDto(user)).thenReturn(expectedDto);

        // When
        UserExistsDto result = userService.findIdByName(username);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(userRepository).findIdAndNameByName(username);
        verify(userConverter).toExistsDto(user);
    }

    @Test
    @DisplayName("Должен обновить основную информацию пользователя")
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void shouldUpdateUserInfo() {
        // Given
        UserFormInfoDto updateDto = new UserFormInfoDto(
                1L,
                "testuser",
                "Updated",
                "Name",
                "Updated description"
        );
        User existingUser = createTestUser();
        User savedUser = createTestUser();
        savedUser.setFirstName("Updated");
        savedUser.setLastName("Name");
        savedUser.setShortDescription("Updated description");

        UserDto expectedDto = new UserDto(
                1L,
                "testuser",
                "Updated",
                "Name",
                "Updated description",
                null
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(savedUser);
        when(userConverter.toDto(savedUser)).thenReturn(expectedDto);

        // When
        UserDto result = userService.updateUserInfo(updateDto);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        assertThat(existingUser.getFirstName()).isEqualTo("Updated");
        assertThat(existingUser.getLastName()).isEqualTo("Name");
        assertThat(existingUser.getShortDescription()).isEqualTo("Updated description");
        verify(userRepository).findById(1L);
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Должен выбросить исключение при обновлении с пустым логином пользователя")
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void shouldThrowExceptionWhenUpdateWithEmptyUsername() {
        // Given
        UserFormInfoDto updateDto = new UserFormInfoDto(1L, null, "Test", "User", null);

        // When & Then
        assertThatThrownBy(() -> userService.updateUserInfo(updateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Пользователь не найден 1");
    }

    @Test
    @DisplayName("Должен обновить пользователя с ролями")
    @WithMockUser(username = "user", roles = {"ROOT"})
    void shouldUpdateUserWithRoles() {
        // Given
        UserWithRolesDto updateDto = createUserWithRolesDto();
        User existingUser = createTestUser();
        User savedUser = createTestUser();
        savedUser.setFirstName("Updated");
        savedUser.setLastName("Name");
        savedUser.setShortDescription("Updated description");
        savedUser.setRoles(List.of(UserRole.ADMIN, UserRole.USER));

        UserDto expectedDto = new UserDto(
                1L,
                "testuser",
                "Updated",
                "Name",
                "Updated description",
                null
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userConverter.toUserRoles(List.of("USER", "ADMIN")))
                .thenReturn(List.of(UserRole.ADMIN, UserRole.USER));
        when(userRepository.save(existingUser)).thenReturn(savedUser);
        when(userConverter.toDto(savedUser)).thenReturn(expectedDto);

        // When
        UserDto result = userService.updateUserWithRoles(updateDto);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        assertThat(existingUser.getRoles()).containsExactlyInAnyOrder(UserRole.ADMIN, UserRole.USER);
        verify(userRepository).findById(1L);
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Должен выбросить исключение при обновлении с пустыми ролями")
    @WithMockUser(username = "admin", roles = {"ROOT"})
    void shouldThrowExceptionWhenUpdateWithEmptyRoles() {
        // Given
        User existingUser = createTestUser();

        UserWithRolesDto updateDto = new UserWithRolesDto(
                1L,
                "testuser",
                "Test",
                "User",
                "",
                List.of()
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThatThrownBy(() -> userService.updateUserWithRoles(updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Роли должны быть заполнены");
    }

    @Test
    @DisplayName("Должен создать нового пользователя")
    @WithMockUser(username = "user", roles = {"ROOT"})
    void shouldCreateUser() {
        // Given
        UserFormWithRolesAndPasswordDto createDto = new UserFormWithRolesAndPasswordDto(
                0L,
                "newuser",
                "password123",
                "New",
                "User",
                "New user description",
                List.of("USER", "ADMIN")
        );

        User newUser = User.builder()
                .name("newuser")
                .password("encodedPassword")
                .firstName("New")
                .lastName("User")
                .shortDescription("New user description")
                .roles(List.of(UserRole.USER, UserRole.ADMIN))
                .build();

        User savedUser = createTestUser();
        savedUser.setId(2L);
        savedUser.setName("newuser");

        UserWithRolesDto expectedDto = new UserWithRolesDto(
                1L,
                "newuser",
                "New",
                "User",
                "New user description",
                List.of("USER", "ADMIN")
        );

        doReturn("encodedPassword").when(passwordEncoder).encode("password123");
        when(userConverter.toUserRoles(List.of("USER", "ADMIN")))
                .thenReturn(List.of(UserRole.USER, UserRole.ADMIN));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userConverter.toUserWithRolesDto(savedUser)).thenReturn(expectedDto);

        // When
        UserWithRolesDto result = userService.createUser(createDto);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение при создании пользователя с пустым паролем")
    @WithMockUser(username = "admin", roles = {"ROOT"})
    void shouldThrowExceptionWhenCreateWithEmptyPassword() {
        // Given
        UserFormWithRolesAndPasswordDto createDto = new UserFormWithRolesAndPasswordDto(
                0L,
                "newuser",
                "",
                "New",
                "User",
                "Description",
                List.of("USER")
        );

        // When & Then
        assertThatThrownBy(() -> userService.createUser(createDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Пароль не заполнен");
    }

    @Test
    @DisplayName("Должен удалить пользователя")
    @WithMockUser(username = "admin", roles = {"ROOT"})
    void shouldDeleteUserById() {
        // Given
        Long userId = 1L;
        User user = createTestUser();

        List<User> users = new ArrayList<>();
        users.add(user);
        Group group1 = Group.builder()
                .id(1L)
                .name("group1")
                .members(users)
                .build();

        Group group2 = Group.builder()
                .id(2L)
                .name("group2")
                .members(users)
                .build();
        user.setGroups(List.of(group1, group2));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(groupRepository.saveAll(any())).thenReturn(List.of(group1, group2));

        // When
        userService.deleteUserById(userId);

        // Then
        verify(userRepository).findById(userId);
        verify(groupRepository).saveAll(List.of(group1, group2));
        verify(userRepository).delete(user);
        assertThat(group1.getMembers()).isEmpty();
        assertThat(group2.getMembers()).isEmpty();
    }

    // -------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    // -------------------------------------------------------------------------
    private User createTestUser() {
        List<UserRole> userRoles = new ArrayList<>();
        userRoles.add(UserRole.USER);
        userRoles.add(UserRole.ADMIN);

        return User.builder()
                .id(1L)
                .name("testuser")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .shortDescription("Test user")
                .roles(userRoles)
                .groups(List.of())
                .build();
    }

    private UserDto createUserDto() {
        return new UserDto(
                1L,
                "testuser",
                "Test",
                "User",
                "Test user",
                null
        );
    }

    private UserWithRolesDto createUserWithRolesDto() {
        List<String> userRoles = new ArrayList<>();
        userRoles.add(String.valueOf(UserRole.USER));
        userRoles.add(String.valueOf(UserRole.ADMIN));
        return new UserWithRolesDto(
                1L,
                "testuser",
                "Test",
                "User",
                "Test user",
                userRoles
        );
    }

    private UserWithRolesAndGroupsDto createUserWithRolesAndGroupsDto() {
        List<String> userRoles = new ArrayList<>();
        userRoles.add(String.valueOf(UserRole.USER));
        userRoles.add(String.valueOf(UserRole.ADMIN));
        return new UserWithRolesAndGroupsDto(
                1L,
                "testuser",
                "Test",
                "User",
                "Test user",
                List.of(),
                userRoles
        );
    }

    private UserExistsDto createUserExistsDto() {
        return new UserExistsDto(1L, "testuser");
    }
}
