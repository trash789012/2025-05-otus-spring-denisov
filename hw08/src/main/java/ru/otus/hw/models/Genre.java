package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "genres")
public class Genre {
    @Id
    private long id;

    private String name;

    public Genre(String name) {
        this.name = name;
    }
}
