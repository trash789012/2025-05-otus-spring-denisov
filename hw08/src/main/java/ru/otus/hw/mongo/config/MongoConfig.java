package ru.otus.hw.mongo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw.mongo.listener.BookCascadeDeleteMongoListener;
import ru.otus.hw.repositories.CommentRepository;

@Configuration
@RequiredArgsConstructor
public class MongoConfig {

    private final CommentRepository commentRepository;

    @Bean
    public BookCascadeDeleteMongoListener bookCascadeDeleteMongoListener() {
        return new BookCascadeDeleteMongoListener(commentRepository);
    }

}
