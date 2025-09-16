package ru.otus.hw.config.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.IdMapping;
import ru.otus.hw.repositories.mongo.IdMappingRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MappingCache {
    private final IdMappingRepository repository;

    public void clean() {
        repository.deleteAll();
    }

    public void putAll(String entity, Map<Long, String> oldIdToNewIdMap) {
        if (oldIdToNewIdMap == null || oldIdToNewIdMap.isEmpty()) return;

        List<IdMapping> mappings = oldIdToNewIdMap.entrySet().stream()
                .map(e -> new IdMapping(null, entity, e.getKey(), e.getValue()))
                .toList();

        repository.saveAll(mappings);
    }

    public void putAllList(String entity, Map<Long, List<String>> oldIdToNewIdsMap) {
        if (oldIdToNewIdsMap == null || oldIdToNewIdsMap.isEmpty()) return;

        List<IdMapping> mappings = new ArrayList<>();
        for (Map.Entry<Long, List<String>> entry : oldIdToNewIdsMap.entrySet()) {
            Long oldId = entry.getKey();
            for (String newId : entry.getValue()) {
                mappings.add(new IdMapping(null, entity, oldId, newId));
            }
        }

        repository.saveAll(mappings);
    }

    public Optional<String> get(String entity, Long oldId) {
        return repository.findByEntityAndOldId(entity, oldId)
                .map(IdMapping::getNewId);
    }

    public Map<Long, String> getBatch(String entity, Collection<Long> oldIds) {
        if (oldIds == null || oldIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return repository.findByEntityAndOldIdIn(entity, oldIds)
                .stream()
                .collect(Collectors.toMap(IdMapping::getOldId, IdMapping::getNewId));
    }

    public Map<Long, List<String>> getBatchList(String entity, Collection<Long> oldIds) {
        if (oldIds == null || oldIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, List<String>> result = new HashMap<>();
        repository.findByEntityAndOldIdIn(entity, oldIds)
                .forEach(mapping -> {
                    result.computeIfAbsent(mapping.getOldId(), k -> new ArrayList<>())
                            .add(mapping.getNewId());
                });

        return result;
    }
}
