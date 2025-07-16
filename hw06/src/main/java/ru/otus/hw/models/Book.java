package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "books")
@NamedEntityGraph(
        name = "book-author-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("author")
        }
)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @ToString.Exclude
    private Author author;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "books_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @ToString.Exclude
    @Fetch(FetchMode.SUBSELECT)
    private List<Genre> genres;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "book",
            cascade = CascadeType.ALL)
    @ToString.Exclude
    @Fetch(FetchMode.SUBSELECT)
    private List<Comment> comments;
}
