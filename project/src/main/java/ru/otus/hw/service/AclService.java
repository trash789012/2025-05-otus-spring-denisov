package ru.otus.hw.service;

import org.springframework.security.acls.model.Permission;

public interface AclService {

    void createPermission(Object object, Permission permission);

    void createAdminPermission(Object object);

}
