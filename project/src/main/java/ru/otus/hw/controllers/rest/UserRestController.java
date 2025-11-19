package ru.otus.hw.controllers.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.config.security.SecurityUserDetails;
import ru.otus.hw.dto.user.UserDto;
import ru.otus.hw.dto.user.UserFormInfoDto;
import ru.otus.hw.dto.user.UserFormWithRolesAndPasswordDto;
import ru.otus.hw.dto.user.UserWithRolesAndGroupsDto;
import ru.otus.hw.dto.user.UserWithRolesDto;
import ru.otus.hw.exceptions.BadRequestException;
import ru.otus.hw.service.UserServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "Users", description = "Операции с пользователями")
public class UserRestController {

    private final UserServiceImpl userDetailsService;

    @Operation(summary = "Получить информацию по текущему авторизованному пользователю")
    @GetMapping("/self")
    public UserDto getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetailsService.findByNameWithGroupsAndMembers(userDetails.getUsername());
    }

    @Operation(summary = "Роли, присвоенные текущему пользователю")
    @GetMapping("/self/roles")
    public List<String> getUserRoles(@AuthenticationPrincipal SecurityUserDetails userDetails) {
        return userDetails.getRoles();
    }

    @Operation(summary = "Получить список возможных ролей в системе")
    @GetMapping("/roles")
    public List<String> getAllRoles() {
        return userDetailsService.findAllUserRoles();
    }

    @Operation(summary = "Получить информацию по пользователю, с ролями и группами")
    @GetMapping("/{id}")
    public UserWithRolesAndGroupsDto getUserById(@PathVariable Long id) {
        return userDetailsService.findUserById(id);
    }

    @Operation(summary = "Получить информацию о всех пользователях с ролями")
    @GetMapping
    public List<UserWithRolesDto> getAllUsersWithRoles() {
        return userDetailsService.findAllUsersWithRoles();
    }

    @Operation(summary = "Создать нового пользователя")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserWithRolesDto createUser(@RequestBody @Valid UserFormWithRolesAndPasswordDto userDto) {
        return userDetailsService.createUser(userDto);
    }

    @Operation(summary = "Обновить основные данные о пользователе")
    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id,
                              @RequestBody @Valid UserFormInfoDto userDto,
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

    @Operation(summary = "Обновить данные о пользователе и роли")
    @PutMapping("/{id}/roles")
    public UserDto updateUserRoles(
            @PathVariable Long id,
            @RequestBody @Valid UserWithRolesDto userDto
    ) {

        if (!id.equals(userDto.id())) {
            throw new BadRequestException("Id in path and principal must match");
        }
        return userDetailsService.updateUserWithRoles(userDto);
    }

    @Operation(summary = "Удаление пользователя по ID")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userDetailsService.deleteUserById(id);
    }

}
