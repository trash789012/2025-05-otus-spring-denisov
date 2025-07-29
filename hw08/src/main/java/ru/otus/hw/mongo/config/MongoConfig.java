package ru.otus.hw.mongo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw.mongo.listener.BookCascadeDeleteMongoListener;

@Configuration
@RequiredArgsConstructor
public class MongoConfig {

    @Bean
    public BookCascadeDeleteMongoListener bookCascadeDeleteMongoListener() {
        return new BookCascadeDeleteMongoListener();
    }

}
