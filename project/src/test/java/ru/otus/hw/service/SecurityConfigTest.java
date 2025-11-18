package ru.otus.hw.service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.otus.hw.config.security.matchers.GroupSecurityMatcher;

@TestConfiguration
public class SecurityConfigTest {
    @Bean
    public GroupSecurityMatcher groupSecurityMatcher() {
        return new Config.TestSecurityMatcher(); // всегда разрешает доступ
    }

    public static class TestSecurityMatcher implements GroupSecurityMatcher {

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
