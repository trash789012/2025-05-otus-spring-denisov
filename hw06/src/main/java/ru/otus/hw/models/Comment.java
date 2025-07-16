package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
@NamedEntityGraph(
        name = "comment-book-author-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("book"),
                @NamedAttributeNode(value = "book", subgraph = "book-author-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "book-author-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("author")
                        }
                )
        }
)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "text")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    @ToString.Exclude
    private Book book;
}
