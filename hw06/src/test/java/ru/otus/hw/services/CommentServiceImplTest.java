package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DisplayName("Интеграционный тест сервиса комментариев")
@DataJpaTest
@Import({CommentServiceImpl.class})
public class CommentServiceImplTest {

    private final CommentService commentService;

    @Autowired
    public CommentServiceImplTest(CommentService commentService) {
        this.commentService = commentService;
    }


}
