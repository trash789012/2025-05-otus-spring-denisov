package ru.otus.hw.service;

import ru.otus.hw.dto.user.UserDto;
import ru.otus.hw.dto.user.UserExistsDto;
import ru.otus.hw.dto.user.UserFormInfoDto;
import ru.otus.hw.dto.user.UserWithRolesAndGroupsDto;
import ru.otus.hw.dto.user.UserFormWithRolesAndPasswordDto;
import ru.otus.hw.dto.user.UserWithRolesDto;

import java.util.List;

/**
 * Сервис для управления пользователями
 */
public interface UserService {

    /**
     * Получить все возможные роли пользователей
     *
     * @return список всех ролей
     */
    List<String> findAllUserRoles();

    /**
     * Найти пользователя по ID с ролями и группами
     *
     * @param userId ID пользователя
     * @return пользователь с ролями и группами
     */
    UserWithRolesAndGroupsDto findUserById(Long userId);

    /**
     * Найти всех пользователей с ролями
     *
     * @return список пользователей с ролями
     */
    List<UserWithRolesDto> findAllUsersWithRoles();

    /**
     * Найти пользователя по имени с группами и участниками
     *
     * @param username имя пользователя
     * @return пользователь с группами и участниками
     */
    UserDto findByNameWithGroupsAndMembers(String username);

    /**
     * Найти ID пользователя по имени
     *
     * @param username имя пользователя
     * @return DTO с ID и именем пользователя
     */
    UserExistsDto findIdByName(String username);

    /**
     * Обновить основную информацию о пользователе
     * Требует роли ROOT или ADMIN
     *
     * @param userDto DTO с информацией о пользователе
     * @return обновленный пользователь
     */
    UserDto updateUserInfo(UserFormInfoDto userDto);

    /**
     * Обновить пользователя с ролями
     * Требует роль ROOT
     *
     * @param userDto DTO с пользователем и ролями
     * @return обновленный пользователь
     */
    UserDto updateUserWithRoles(UserWithRolesDto userDto);

    /**
     * Создать нового пользователя
     * Требует роль ROOT
     *
     * @param userDto DTO с данными нового пользователя
     * @return созданный пользователь с ролями
     */
    UserWithRolesDto createUser(UserFormWithRolesAndPasswordDto userDto);

    /**
     * Удалить пользователя по ID
     * Требует роль ROOT
     *
     * @param id ID пользователя
     */
    void deleteUserById(Long id);
}