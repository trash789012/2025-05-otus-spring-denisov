package ru.otus.hw.service;

import org.springframework.security.acls.model.Permission;
import ru.otus.hw.domain.Slot;

/**
 * Сервис управления ACL (Access Control List) правами доступа
 * Обеспечивает механизм тонкой настройки прав доступа к объектам системы.
 */
public interface AclService {

    /**
     * Создать произвольное разрешение на объект для текущего аутентифицированного пользователя.
     * Текущий пользователь становится владельцем указанного разрешения на объект.
     *
     * @param object объект доменной модели (User, Group, Slot и т.д.)
     * @param permission уровень доступа из enum Permission
     */
    void createPermission(Object object, Permission permission);

    /**
     * Создать разрешение на слот с дополнительным присвоением динамических ролей на группы
     *
     * @param object слот - временной интервал для бронирования
     */
    void createSlotPermissions(Slot object, Permission... permissions);

    /**
     * Flush накопленных изменений
     *
     */
    void flushAclCache();

    /**
     * Создать права администратора на объект.
     * Назначает разрешение ADMINISTRATION, позволяющее управлять правами других пользователей на объект.
     *
     * @param object объект доменной модели
     */
    void createAdminPermission(Object object);

    /**
     * Создать права root-пользователя на объект.
     * Назначает системный уровень доступа, обходящий обычные проверки прав.
     *
     * @param object объект доменной модели
     */
    void createRootPermission(Object object);
}

