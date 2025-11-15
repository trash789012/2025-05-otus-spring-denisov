package ru.otus.hw.config.security.matchers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.GroupRepository;
import ru.otus.hw.service.GroupService;

@Component
@RequiredArgsConstructor
public class GroupSecurity {

    private final GroupRepository groupRepository;

    public boolean isMember(Long groupID) {
        if (groupID == null) {
            return true;
        }
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return groupRepository.existsMemberInGroup(groupID, username);
    }

}
