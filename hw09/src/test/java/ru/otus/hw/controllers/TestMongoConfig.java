package ru.otus.hw.controllers;

import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class TestMongoConfig {
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(MongoClients.create(), "test");
    }
}