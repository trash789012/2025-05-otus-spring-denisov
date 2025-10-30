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

}
