package ru.otus.hw.models.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@Setter
@AllArgsConstructor
@Document(collection = "id_mappings")
@CompoundIndex(def = "{'entity': 1, 'oldId': 1}", name = "entity_oldid_idx", unique = true)
public class IdMapping {
    @Id
    private String id;
    private String entity;
    private Long oldId;
    private String newId;
}
