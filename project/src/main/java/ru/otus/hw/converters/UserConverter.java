package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.domain.User;
import ru.otus.hw.dto.GroupFormDto;
import ru.otus.hw.dto.UserDto;
import ru.otus.hw.dto.UserExistsDto;

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

    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        return User.builder()
                .id(userDto.id())
                .name(userDto.name())
                .build();
    }

}
