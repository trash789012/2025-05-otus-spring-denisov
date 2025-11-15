package ru.otus.hw.service;

import org.springframework.security.acls.model.Permission;
import ru.otus.hw.domain.Slot;

public interface AclService {

    void createPermission(Object object, Permission permission);

    void createSlotPermission(Slot object, Permission permission);

    void createAdminPermission(Object object);

    void createRootPermission(Object object);

}

