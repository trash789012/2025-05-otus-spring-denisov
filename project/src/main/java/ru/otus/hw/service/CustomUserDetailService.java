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
import ru.otus.hw.dto.user.UserDto;
import ru.otus.hw.dto.user.UserExistsDto;
import ru.otus.hw.dto.user.UserInfoDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UserConverter userConverter;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var dbUser = getUserByName(username);
        return new SecurityUserDetails(dbUser);
    }

    @Transactional(readOnly = true)
    public UserDto findByUserName(String username) {
        var dbUser = getUserByName(username);
        return userConverter.toDto(dbUser);
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
    public UserDto updateUserInfo(UserDto userDto) {
        var userDb = getUserByName(userDto.name());

        userDb.setFirstName(userDto.firstName());
        userDb.setLastName(userDto.lastName());
        userDb.setShortDescription(userDto.shortDescription());

        return userConverter.toDto(userRepository.save(userDb));
    }

    private User getUserByName(String username) {
        return userRepository.findByName(username)
                .orElseThrow(
                        () -> new EntityNotFoundException("User with name %s not found".formatted(username))
                );
    }
}
