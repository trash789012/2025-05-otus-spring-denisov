package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.IdMapping;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IdMappingRepository extends MongoRepository<IdMapping, String> {
    Optional<IdMapping> findByEntityAndOldId(String entity, Long oldId);
    List<IdMapping> findByEntityAndOldIdIn(String entity, Collection<Long> oldIds);
}
