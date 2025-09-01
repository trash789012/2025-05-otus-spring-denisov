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
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Comment {
    @Id
    private String id;

    private String text;

    //@DocumentReference(lazy = true)
    private Book book;

    public Comment(String text) {
        this.text = text;
    }
}
