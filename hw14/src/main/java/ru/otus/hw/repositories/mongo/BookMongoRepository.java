package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.BookMongo;

public interface BookMongoRepository extends MongoRepository<BookMongo, String> {
}
