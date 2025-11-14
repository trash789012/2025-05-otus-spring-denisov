package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.UserConverter;
import ru.otus.hw.domain.User;
import ru.otus.hw.domain.enums.UserRole;
import ru.otus.hw.dto.user.*;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.GroupRepository;
import ru.otus.hw.repositories.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private final UserConverter userConverter;

    private final PasswordEncoder passwordEncoder;

    // -------------------------------------------------------------------------
    // ROLES
    // -------------------------------------------------------------------------

    public List<String> findAllUserRoles() {
        return Arrays.stream(UserRole.values())
                .map(Enum::name)
                .toList();
    }

    // -------------------------------------------------------------------------
    // USER READ
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public UserWithRolesAndGroupsDto findUserById(Long userId) {
        return userConverter.toUserWithRolesAndGroupsDto(
                getUserById(userId)
        );
    }

    @Transactional(readOnly = true)
    public List<UserWithRolesDto> findAllUsersWithRoles() {
        return userRepository.findAll().stream()
                .map(userConverter::toUserWithRolesDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDto findByNameWithGroupsAndMembers(String username) {
        var dbUser = userRepository.findByNameWithGroupsAndMembers(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with name %s not found".formatted(username)
                ));
        return userConverter.toDto(dbUser);
    }

    @Transactional(readOnly = true)
    public UserExistsDto findIdByName(String username) {
        var dbUser = userRepository.findIdAndNameByName(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with name %s not found".formatted(username)
                ));
        return userConverter.toExistsDto(dbUser);
    }

    // -------------------------------------------------------------------------
    // USER UPDATE
    // -------------------------------------------------------------------------

    @Transactional
    public UserDto updateUserInfo(UserInfoDto userDto) {
        var userDb = getUserById(userDto.id());
        validateBasicUserFields(userDto.name(), userDto.firstName());

        applyUserBasicData(userDb,
                userDto.firstName(),
                userDto.lastName(),
                userDto.shortDescription()
        );

        return userConverter.toDto(userRepository.save(userDb));
    }

    @Transactional
    public UserDto updateUserWithRoles(UserWithRolesDto userDto) {
        var userDb = getUserById(userDto.id());
        validateBasicUserFields(userDto.name(), userDto.firstName());

        if (userDto.roles().isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be empty");
        }

        applyUserBasicData(userDb,
                userDto.firstName(),
                userDto.lastName(),
                userDto.shortDescription()
        );

        userDb.setRoles(userConverter.toUserRoles(userDto.roles()));

        return userConverter.toDto(userRepository.save(userDb));
    }

    // -------------------------------------------------------------------------
    // USER CREATE
    // -------------------------------------------------------------------------

    @Transactional
    public UserWithRolesDto createUser(UserWithRolesAndPasswordDto userDto) {

        var roles = userConverter.toUserRoles(userDto.roles());

        validateCreationFields(userDto.name(), userDto.password(), userDto.firstName(), roles);

        var newUser = User.builder()
                .name(userDto.name())
                .password(passwordEncoder.encode(userDto.password()))
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .shortDescription(userDto.shortDescription())
                .roles(roles)
                .build();

        return userConverter.toUserWithRolesDto(userRepository.save(newUser));
    }

    // -------------------------------------------------------------------------
    // USER DELETE
    // -------------------------------------------------------------------------

    @Transactional
    public void deleteUserById(Long id) {
        var user = getUserById(id);

        // Убираем участника из всех групп
        user.getGroups().forEach(group -> group.getMembers().remove(user));
        groupRepository.saveAll(user.getGroups());

        // Теперь можно удалять пользователя
        userRepository.delete(user);
    }

    // -------------------------------------------------------------------------
    // PRIVATE HELPERS
    // -------------------------------------------------------------------------

    private void validateBasicUserFields(String username, String firstName) {
        if (username == null) {
            throw new IllegalArgumentException("Username is empty");
        }
        if (firstName == null) {
            throw new IllegalArgumentException("Firstname is empty");
        }
    }

    private void validateCreationFields(String username, String password,
                                        String firstName, List<UserRole> roles) {
        if (username == null) {
            throw new IllegalArgumentException("Username is empty");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password is empty");
        }
        if (firstName == null) {
            throw new IllegalArgumentException("Firstname is empty");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be empty");
        }
    }

    private void applyUserBasicData(User user,
                                    String firstName,
                                    String lastName,
                                    String shortDescription) {

        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        if (shortDescription != null) {
            user.setShortDescription(shortDescription);
        }
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found %s".formatted(id)));
    }

    private User getUserByName(String username) {
        return userRepository.findByName(username)
                .orElseThrow(() ->
                        new EntityNotFoundException("User with name %s not found".formatted(username)));
    }
}
