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
import ru.otus.hw.domain.Slot;

@Service
@RequiredArgsConstructor
public class AclServiceImpl implements AclService {

    private final MutableAclService aclService;

    @Override
    public void createPermission(Object object, Permission permission) {
        var acl = getMutableAcl(new ObjectIdentityImpl(object), permission);
        aclService.updateAcl(acl);
    }

    @Override
    public void createSlotPermission(Slot object, Permission permission) {
        var acl = getMutableAcl(new ObjectIdentityImpl(object), permission);

        Sid groupSid = new GrantedAuthoritySid("ROLE_GROUP_" + object.getBookedBy().getId());

        acl.insertAce(
                acl.getEntries().size(),
                BasePermission.WRITE,
                groupSid,
                true
        );

        aclService.updateAcl(acl);
    }

    @Override
    public void createAdminPermission(Object object) {
        createPermission(object, "ADMIN");
    }

    private MutableAcl getMutableAcl(ObjectIdentityImpl object, Permission permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Sid sid = new PrincipalSid(authentication);

        MutableAcl acl = getAcl(object);

        acl.insertAce(acl.getEntries().size(), permission, sid, true);

        return acl;
    }

    @Override
    public void createRootPermission(Object object) {
        createPermission(object, "ROOT");
    }


    private void createPermission(Object object, String role) {
        ObjectIdentity oid = new ObjectIdentityImpl(object);
        final Sid sid = new GrantedAuthoritySid(role);

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
