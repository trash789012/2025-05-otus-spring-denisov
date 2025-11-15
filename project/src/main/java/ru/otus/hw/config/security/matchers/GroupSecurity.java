package ru.otus.hw.config.security.matchers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Group;
import ru.otus.hw.domain.Slot;
import ru.otus.hw.repositories.GroupRepository;
import ru.otus.hw.repositories.SlotRepository;
import ru.otus.hw.repositories.UserRepository;

@Component
@RequiredArgsConstructor
public class GroupSecurity {

    private final GroupRepository groupRepository;

    private final SlotRepository slotRepository;

    public boolean isMember(Long groupID) {
        if (groupID == null) {
            return true;
        }
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return groupRepository.existsMemberInGroup(groupID, username);
    }

    public boolean isMemberTwoGroups(Long slotId, Long groupId) {
        if (groupId == null) return true;

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentGroupId = slotRepository.findById(slotId)
                .map(Slot::getBookedBy)
                .map(Group::getId)
                .orElse(null);

        var newGroup = groupRepository.existsMemberInGroup(groupId, username);
        var currentGroup = currentGroupId == null || groupRepository.existsMemberInGroup(currentGroupId, username);
        return newGroup && currentGroup;
    }

}
