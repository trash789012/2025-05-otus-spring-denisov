package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import ru.otus.hw.config.acl.AclConfig;
import ru.otus.hw.config.acl.CacheConfig;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest()
@Import({
        CommentServiceImpl.class,
        CommentConverter.class,
        AclServiceWrapperServiceImpl.class,
        AclConfig.class,
        CacheConfig.class
})
@EnableMethodSecurity
public class CommentServiceImplSecurityTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("должен разрешать удалять коммент админу")
    void shouldAllowDeleteCommentWithGrantedUser() {
        commentService.deleteById(1L);

        Optional<CommentDto> deletedComment = commentService.findById(1L);
        assertThat(deletedComment).isEmpty();
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("должен запрещать удалять коммент не админу")
    void shouldDenyDeleteCommentWithUngrantedUser() {
        assertThatThrownBy(() -> commentService.deleteById(1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("должен разрешать удалять коммент его автору")
    @WithMockUser(username = "user")
    void shouldAllowDeleteCommentForAuthor() {
        CommentDto newComment = new CommentDto(0L, "Comment", 1L);
        CommentDto savedComment = commentService.insert(newComment);

        commentService.deleteById(savedComment.id());
        Optional<CommentDto> deletedComment = commentService.findById(savedComment.id());
        assertThat(deletedComment).isEmpty();
    }

}
