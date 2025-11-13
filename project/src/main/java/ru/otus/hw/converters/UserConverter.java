package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.domain.User;
import ru.otus.hw.domain.enums.UserRole;
import ru.otus.hw.dto.group.GroupFormDto;
import ru.otus.hw.dto.user.UserDto;
import ru.otus.hw.dto.user.UserExistsDto;
import ru.otus.hw.dto.user.UserInfoDto;
import ru.otus.hw.dto.user.UserWithRolesDto;

import java.util.List;

@Component
public class UserConverter {

    public UserDto toDto(User user) {
        var userGroups = (user.getGroups() != null) ?
                user.getGroups().stream()
                        .map(group -> new GroupFormDto(
                                group.getId(),
                                group.getName(),
                                group.getDescription(),
                                (group.getMembers() != null) ?
                                        group.getMembers().stream().map(User::getId).toList() : null,
                                null
                        ))
                        .toList() : null;

        return new UserDto(
                user.getId(),
                user.getName(),
                user.getFirstName(),
                user.getLastName(),
                user.getShortDescription(),
                userGroups
        );
    }

    public UserExistsDto toExistsDto(User user) {
        return new UserExistsDto(user.getId(), user.getName());
    }

    public UserInfoDto toInfoDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserInfoDto(
                user.getId(),
                user.getName(),
                user.getFirstName(),
                user.getLastName(),
                user.getShortDescription()
        );
    }

    public UserWithRolesDto toUserWithRolesDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserWithRolesDto(
                user.getId(),
                user.getName(),
                user.getFirstName(),
                user.getLastName(),
                user.getShortDescription(),
                user.getRoles() != null
                        ? user.getRoles().stream()
                        .map(Enum::name)
                        .toList() : List.of()
        );
    }

}
