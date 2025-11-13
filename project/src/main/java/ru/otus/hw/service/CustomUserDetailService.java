package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.config.security.SecurityUserDetails;
import ru.otus.hw.converters.UserConverter;
import ru.otus.hw.domain.User;
import ru.otus.hw.domain.enums.UserRole;
import ru.otus.hw.dto.user.UserDto;
import ru.otus.hw.dto.user.UserExistsDto;
import ru.otus.hw.dto.user.UserInfoDto;
import ru.otus.hw.dto.user.UserWithRolesAndGroupsDto;
import ru.otus.hw.dto.user.UserWithRolesDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.GroupRepository;
import ru.otus.hw.repositories.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private final UserConverter userConverter;

    public List<String> findAllUserRoles() {
        return Arrays.stream(UserRole.values())
                .map(Enum::name)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var dbUser = getUserByName(username);
        return new SecurityUserDetails(dbUser);
    }

    @Transactional(readOnly = true)
    public UserWithRolesAndGroupsDto findUserById(Long userId) {
        var dbUser = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found %s".formatted(userId))
        );
        return userConverter.toUserWithRolesAndGroupsDto(dbUser);
    }

    @Transactional(readOnly = true)
    public List<UserWithRolesDto> getAllUsersWithRoles() {
        return userRepository.findAll().stream()
                .map(userConverter::toUserWithRolesDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDto findByNameWithGroupsAndMembers(String username) {
        var dbUser = userRepository.findByNameWithGroupsAndMembers(username)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with name %s not found".formatted(username))
                );
        return userConverter.toDto(dbUser);
    }

    @Transactional(readOnly = true)
    public UserExistsDto findIdByName(String username) {
        var dbUser = userRepository.findIdAndNameByName(username)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with name %s not found".formatted(username))
                );
        return userConverter.toExistsDto(dbUser);
    }

    @Transactional
    public UserDto updateUserInfo(UserInfoDto userDto) {
        var userDb = userRepository.findById(userDto.id()).orElseThrow(
                () -> new EntityNotFoundException("User not found %s".formatted(userDto.id()))
        );

        prepareForUpdate(userDto.firstName(), userDb, userDto.lastName(), userDto.shortDescription());
        return userConverter.toDto(userRepository.save(userDb));
    }

    @Transactional
    public UserDto updateUserWithRoles(UserWithRolesDto userDto) {
        var userDb = getUserById(userDto.id());

        prepareForUpdate(userDto.firstName(), userDb, userDto.lastName(), userDto.shortDescription());
        if (!userDto.roles().isEmpty()) {
            userDb.setRoles(userDto.roles().stream()
                    .map(String::toUpperCase)
                    .map(UserRole::valueOf)
                    .collect(Collectors.toList())
            );
        }

        return userConverter.toDto(userRepository.save(userDb));
    }

    private static void prepareForUpdate(String userDto, User userDb, String userDto1, String userDto2) {
        if (userDto != null) {
            userDb.setFirstName(userDto);
        }
        if (userDto1 != null) {
            userDb.setLastName(userDto1);
        }
        if (userDto2 != null) {
            userDb.setShortDescription(userDto2);
        }
    }

    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found %s".formatted(id))
        );
    }

    @Transactional
    public void deleteUserById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found %s".formatted(id))
                );

        // Удаляем пользователя из всех групп
        user.getGroups().forEach(group -> group.getMembers().remove(user));
        groupRepository.saveAll(user.getGroups());

        // Теперь можно безопасно удалить пользователя
        userRepository.delete(user);
    }

    private User getUserByName(String username) {
        return userRepository.findByName(username)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with name %s not found".formatted(username))
                );
    }
}
