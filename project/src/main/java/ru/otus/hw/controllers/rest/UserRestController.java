package ru.otus.hw.controllers.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.user.UserDto;
import ru.otus.hw.dto.user.UserWithRolesAndGroupsDto;
import ru.otus.hw.dto.user.UserWithRolesDto;
import ru.otus.hw.exceptions.BadRequestException;
import ru.otus.hw.service.CustomUserDetailService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "Users", description = "Операции с пользователями")
public class UserRestController {

    private final CustomUserDetailService userDetailsService;

    @Operation(summary = "Получить информацию по текущему авторизованному пользователю")
    @GetMapping("/self")
    public UserDto getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetailsService.findByNameWithGroupsAndMembers(userDetails.getUsername());
    }

    @Operation(summary = "Получить информацию по пользователю, с ролями и группами")
    @GetMapping("/{id}")
    public UserWithRolesAndGroupsDto getUserById(@PathVariable Long id) {
        return userDetailsService.findUserById(id);
    }

    @Operation(summary = "Получить информацию о пользователях с ролями")
    @GetMapping
    public List<UserWithRolesDto> getAllUsersWithRoles() {
        return userDetailsService.getAllUsersWithRoles();
    }


    @Operation(summary = "Обновить основные данные о пользователе")
    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id,
                              @RequestBody UserDto userDto,
                              @AuthenticationPrincipal UserDetails userDetails
    ) {
        var userDb = userDetailsService.findIdByName(userDetails.getUsername());
        if (!id.equals(userDb.id())) {
            throw new BadRequestException("Id in path and principal must match");
        }

        if (!id.equals(userDto.id())) {
            throw new BadRequestException("Id in path and body must match");
        }

        return userDetailsService.updateUserInfo(userDto);
    }

    @Operation(summary = "Удаление пользователя по ID")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userDetailsService.deleteUserById(id);
    }

}
