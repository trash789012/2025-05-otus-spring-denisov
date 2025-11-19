package ru.otus.hw.service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import ru.otus.hw.config.security.matchers.GroupSecurityMatcher;
import ru.otus.hw.repositories.GroupRepository;
import ru.otus.hw.repositories.SlotRepository;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class SecurityConfigTest {
    @Bean
    public GroupSecurityMatcher groupSecurityMatcher() {
        return new TestSecurityMatcher(null, null); // всегда разрешает доступ
    }

    @Bean
    public PermissionEvaluator permissionEvaluator() {
        return mock(PermissionEvaluator.class);
    }

    @Bean
    public MethodSecurityExpressionHandler expressionHandler(PermissionEvaluator permissionEvaluator) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(permissionEvaluator);
        return handler;
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

        @Override
        public boolean isMemberBySlotId(Long slotId) {
            return true;
        }
    }
}
