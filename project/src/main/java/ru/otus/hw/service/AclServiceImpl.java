package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.domain.Slot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AclServiceImpl implements AclService {

    private final MutableAclService aclService;

    private final Map<ObjectIdentity, MutableAcl> aclCache = new ConcurrentHashMap<>();

    @Override
    public void createPermission(Object object, Permission permission) {
        ObjectIdentity oid = new ObjectIdentityImpl(object);
        addPermissionToCache(oid, permission);
    }

    @Override
    public void createSlotPermissions(Slot object, Permission... permissions) {
        ObjectIdentity oid = new ObjectIdentityImpl(object);
        for (Permission permission : permissions) {
            addPermissionToCache(oid, permission);
        }
    }

    @Override
    public void createAdminPermission(Object object) {
        ObjectIdentity oid = new ObjectIdentityImpl(object);
        addRolePermissionsToCache(oid, "ROLE_ADMIN");
    }

    @Override
    public void createRootPermission(Object object) {
        ObjectIdentity oid = new ObjectIdentityImpl(object);
        addRolePermissionsToCache(oid, "ROLE_ROOT");
    }

    /**
     * Добавляет разрешение в кэшированный ACL
     */
    private void addPermissionToCache(ObjectIdentity oid, Permission permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Sid sid = new PrincipalSid(authentication);

        MutableAcl acl = getCachedAcl(oid);

        acl.insertAce(acl.getEntries().size(), permission, sid, true);
    }

    /**
     * Добавляет права для роли в кэшированный ACL
     */
    private void addRolePermissionsToCache(ObjectIdentity oid, String role) {
        final Sid sid = new GrantedAuthoritySid(role);
        MutableAcl acl = getCachedAcl(oid);

        int nextAceOrder = acl.getEntries().size();
        acl.insertAce(nextAceOrder, BasePermission.READ, sid, true);
        acl.insertAce(nextAceOrder, BasePermission.WRITE, sid, true);
        acl.insertAce(nextAceOrder, BasePermission.DELETE, sid, true);
        acl.insertAce(nextAceOrder, BasePermission.ADMINISTRATION, sid, true);
    }

    /**
     * Получает ACL из кэша или создает новый
     */
    private MutableAcl getCachedAcl(ObjectIdentity oid) {
        return aclCache.computeIfAbsent(oid, this::loadOrCreateAcl);
    }

    /**
     * Загружает ACL из базы или создает новый
     */
    private MutableAcl loadOrCreateAcl(ObjectIdentity oid) {
        try {
            return (MutableAcl) aclService.readAclById(oid);
        } catch (NotFoundException e) {
            return aclService.createAcl(oid);
        }
    }

    /**
     * Обновляет все кэшированные ACL в базе данных
     * Этот метод должен вызываться после всех операций с правами
     */
    @Override
    @Transactional
    public void flushAclCache() {
        for (Map.Entry<ObjectIdentity, MutableAcl> entry : aclCache.entrySet()) {
            try {
                aclService.updateAcl(entry.getValue());
            } catch (Exception e) {
                // Логируем ошибку, но продолжаем обновлять остальные ACL
                log.error(e.getMessage(), e);
            }
        }

        // Очищаем кэши после обновления
        aclCache.clear();
    }
}