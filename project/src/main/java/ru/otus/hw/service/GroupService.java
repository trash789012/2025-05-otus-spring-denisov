package ru.otus.hw.service;

import ru.otus.hw.dto.group.GroupDto;
import ru.otus.hw.dto.group.GroupFormDto;
import ru.otus.hw.dto.group.GroupInfoDto;
import ru.otus.hw.dto.group.GroupWithMembersAndSlotsDto;
import ru.otus.hw.dto.group.GroupWithMembersDto;
import ru.otus.hw.dto.user.UserInfoDto;
import ru.otus.hw.exceptions.EntityNotFoundException;

import java.util.List;
/**
 * Сервис для управления группами пользователей
 * Предоставляет методы для работы с группами, их участниками и слотами
 */

public interface GroupService {

    /**
     * Найти группу по идентификатору
     *
     * @param id идентификатор группы
     * @return DTO группы с основной информацией
     * @throws EntityNotFoundException если группа не найдена
     */
    GroupDto findById(long id);

    /**
     * Получить все группы
     *
     * @return список всех групп с основной информацией
     */
    List<GroupDto> findAll();

    /**
     * Получить все группы без вложенных данных (участников и слотов)
     * Оптимизировано для случаев, когда нужна только основная информация о группах
     *
     * @return список групп с базовой информацией
     */
    List<GroupInfoDto> findAllWithoutNested();

    /**
     * Найти группу по идентификатору с информацией об участниках
     *
     * @param id идентификатор группы
     * @return DTO группы с подробной информацией об участниках
     * @throws EntityNotFoundException если группа не найдена
     */
    GroupWithMembersDto findGroupWithMembersById(Long id);

    /**
     * Найти группу по идентификатору с информацией об участниках и слотах
     *
     * @param id идентификатор группы
     * @return DTO группы с полной информацией (участники и слоты)
     * @throws EntityNotFoundException если группа не найдена
     */
    GroupWithMembersAndSlotsDto findGroupWithMembersAndSlotsById(Long id);

    /**
     * Удалить участника из группы
     *
     * @param memberId идентификатор пользователя-участника
     * @param groupId идентификатор группы
     * @throws EntityNotFoundException если группа или пользователь не найдены
     * @throws IllegalArgumentException если пользователь не является участником группы
     */
    void deleteMemberFromGroup(Long memberId, Long groupId);

    /**
     * Найти пользователей для добавления в группу по поисковому запросу
     * Используется для поиска пользователей при добавлении новых участников в группу
     *
     * @param groupId идентификатор группы
     * @param searchTerm поисковый запрос (имя, фамилия, username)
     * @return список пользователей, подходящих под критерии поиска
     */
    List<UserInfoDto> findUsersForGroupBySearchTerm(Long groupId, String searchTerm);

    /**
     * Добавить участников в группу
     *
     * @param memberIds список идентификаторов пользователей для добавления
     * @param groupId идентификатор группы
     * @return DTO группы с обновленным списком участников
     * @throws EntityNotFoundException если группа или любой из пользователей не найдены
     * @throws IllegalArgumentException если пользователь уже является участником группы
     */
    GroupWithMembersDto addMembersToGroup(List<Long> memberIds, Long groupId);

    /**
     * Создать новую группу
     *
     * @param groupDto DTO с данными для создания группы
     * @return DTO созданной группы
     * @throws IllegalArgumentException если данные группы некорректны
     */
    GroupDto insert(GroupFormDto groupDto);

    /**
     * Обновить информацию о группе
     *
     * @param groupDto DTO с обновленными данными группы
     * @return DTO обновленной группы
     * @throws EntityNotFoundException если группа не найдена
     * @throws IllegalArgumentException если данные группы некорректны
     */
    GroupDto update(GroupFormDto groupDto);

    /**
     * Удалить группу по идентификатору
     *
     * @param id идентификатор группы для удаления
     * @throws EntityNotFoundException если группа не найдена
     */
    void deleteById(Long id);
}