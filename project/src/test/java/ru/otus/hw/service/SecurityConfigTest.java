package ru.otus.hw.service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.otus.hw.config.security.matchers.GroupSecurityMatcher;
import ru.otus.hw.repositories.GroupRepository;
import ru.otus.hw.repositories.SlotRepository;

@TestConfiguration
public class SecurityConfigTest {
    @Bean
    public GroupSecurityMatcher groupSecurityMatcher() {
        return new TestSecurityMatcher(null, null); // всегда разрешает доступ
    }

    public static class TestSecurityMatcher extends GroupSecurityMatcher {

        public TestSecurityMatcher(GroupRepository groupRepository, SlotRepository slotRepository) {
            super(groupRepository, slotRepository);
        }

        @Override
        public boolean isMember(Long groupID) {
            return true;
        }

        @Override
        public boolean isMemberBoth(Long slotId, Long groupId) {
            return true;
        }
    }
}
