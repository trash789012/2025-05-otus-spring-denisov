package ru.otus.hw.services;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import ru.otus.hw.config.acl.AclConfig;
import ru.otus.hw.config.acl.CacheConfig;

@DataJpaTest()
@Import({
        CommentServiceImpl.class,
        AclServiceWrapperServiceImpl.class,
        AclConfig.class,
        CacheConfig.class
})
@EnableMethodSecurity
public class CommentServiceImplSecurityTest {
}
