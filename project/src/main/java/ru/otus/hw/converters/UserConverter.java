package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.domain.User;
import ru.otus.hw.dto.UserDto;

@Component
public class UserConverter {

    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName()
        );
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
