package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.UserConverter;
import ru.otus.hw.domain.User;
import ru.otus.hw.domain.enums.UserRole;
import ru.otus.hw.dto.user.UserDto;
import ru.otus.hw.dto.user.UserExistsDto;
import ru.otus.hw.dto.user.UserFormInfoDto;
import ru.otus.hw.dto.user.UserWithRolesAndGroupsDto;
import ru.otus.hw.dto.user.UserFormWithRolesAndPasswordDto;
import ru.otus.hw.dto.user.UserWithRolesDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.GroupRepository;
import ru.otus.hw.repositories.UserRepository;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private final UserConverter userConverter;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<String> findAllUserRoles() {
        return Arrays.stream(UserRole.values())
                .map(Enum::name)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserWithRolesAndGroupsDto findUserById(Long userId) {
        return userConverter.toUserWithRolesAndGroupsDto(
                getUserById(userId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserWithRolesDto> findAllUsersWithRoles() {
        return userRepository.findAll().stream()
                .map(userConverter::toUserWithRolesDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByNameWithGroupsAndMembers(String username) {
        var dbUser = userRepository.findByNameWithGroupsAndMembers(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Пользователь с логином %s не найден".formatted(username)
                ));
        return userConverter.toDto(dbUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserExistsDto findIdByName(String username) {
        var dbUser = userRepository.findIdAndNameByName(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Пользователь с логином %s не найден".formatted(username)
                ));
        return userConverter.toExistsDto(dbUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN')")
    public UserDto updateUserInfo(UserFormInfoDto userDto) {
        var userDb = getUserById(userDto.id());
        validateBasicUserFields(userDto.name(), userDto.firstName());

        applyUserBasicData(userDb,
                userDto.firstName(),
                userDto.lastName(),
                userDto.shortDescription()
        );

        return userConverter.toDto(userRepository.save(userDb));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROOT')")
    public UserDto updateUserWithRoles(UserWithRolesDto userDto) {
        var userDb = getUserById(userDto.id());
        validateBasicUserFields(userDto.name(), userDto.firstName());

        if (userDto.roles().isEmpty()) {
            throw new IllegalArgumentException("Роли должны быть заполнены");
        }

        applyUserBasicData(userDb,
                userDto.firstName(),
                userDto.lastName(),
                userDto.shortDescription()
        );

        userDb.setRoles(userConverter.toUserRoles(userDto.roles()));

        return userConverter.toDto(userRepository.save(userDb));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROOT')")
    public UserWithRolesDto createUser(UserFormWithRolesAndPasswordDto userDto) {

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

        var savedUser = userRepository.save(newUser);

        return userConverter.toUserWithRolesDto(savedUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ROOT')")
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
            throw new IllegalArgumentException("Имя пользователя не заполнено");
        }
        if (firstName == null) {
            throw new IllegalArgumentException("Фамилия не заполнена");
        }
    }

    private void validateCreationFields(String username, String password,
                                        String firstName, List<UserRole> roles) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Имя пользователя не заполнено");
        }
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Пароль не заполнен");
        }
        if (StringUtils.isBlank(firstName)) {
            throw new IllegalArgumentException("Фамилия не заполнена");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Нужно присвоить роли");
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
                        new EntityNotFoundException("Пользователь не найден %s".formatted(id)));
    }
}
