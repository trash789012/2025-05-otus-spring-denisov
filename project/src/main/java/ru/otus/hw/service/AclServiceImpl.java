package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class AclServiceImpl implements AclService {

    private final MutableAclService aclService;

    @Override
    public void createPermission(Object object, Permission permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Sid sid = new PrincipalSid(authentication);
        ObjectIdentity oid = new ObjectIdentityImpl(object);

        MutableAcl acl = getAcl(oid);

        acl.insertAce(acl.getEntries().size(), permission, sid, true);
        aclService.updateAcl(acl);
    }

    @Override
    public void createAdminPermission(Object object) {
        ObjectIdentity oid = new ObjectIdentityImpl(object);
        final Sid sid = new GrantedAuthoritySid("ROLE_ADMIN");

        MutableAcl acl = getAcl(oid);
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, sid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, sid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, sid, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, sid, true);
        aclService.updateAcl(acl);
    }

    private MutableAcl getAcl(ObjectIdentity oid) {
        try {
            return (MutableAcl) aclService.readAclById(oid);
        } catch (NotFoundException e) {
            return aclService.createAcl(oid);
        }
    }
}
