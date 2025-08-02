package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "authors")
public class Author {
    @Id
    private String id;

    private String fullName;

    public Author(String fullName) {
        this.fullName = fullName;
    }
}
