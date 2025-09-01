package ru.otus.hw.mongo.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.reactivestreams.client.MongoDatabase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "dropDb", author = "nadenisov", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "createAuthorsInitial", author = "nadenisov")
    public Flux<Author> createAuthorsInitial(AuthorRepository repository) {
        return repository.saveAll(List.of(
                new Author("Стивен Кинг"),
                new Author("Борис Виан"),
                new Author("Джон Гришен")
        ));
    }

    @ChangeSet(order = "003", id = "createGenresInitial", author = "nadenisov")
    public Flux<Genre> createGenresInitial(GenreRepository repository) {
        return repository.saveAll(List.of(
                new Genre("Ужасы"),
                new Genre("Мистика"),
                new Genre("Драма"),
                new Genre("Фантастика"),
                new Genre("Детектив"),
                new Genre("Криминал")
        ));
    }

    @ChangeSet(order = "004", id = "createBooksInitial", author = "nadenisov")
    public Flux<Book> createBooksInitial(BookRepository bookRepository,
                                         AuthorRepository authorRepository,
                                         GenreRepository genreRepository) {

        return Mono.zip(authorRepository.findAll().collectList(),
                        genreRepository.findAll().collectList())
                .flatMapMany(tuple -> {
                    var authors = tuple.getT1();
                    var genres = tuple.getT2();

                    Book book1 = new Book("Бессонница");
                    book1.setAuthor(authors.get(0));
                    book1.setGenres(List.of(genres.get(0), genres.get(1)));

                    Book book2 = new Book("Осень в Пекине");
                    book2.setAuthor(authors.get(1));
                    book2.setGenres(List.of(genres.get(2), genres.get(3)));

                    Book book3 = new Book("Камера");
                    book3.setAuthor(authors.get(2));
                    book3.setGenres(List.of(genres.get(4), genres.get(5)));

                    return bookRepository.saveAll(List.of(book1, book2, book3));
                });
    }


    @ChangeSet(order = "005", id = "createCommentsInitial", author = "nadenisov")
    public Flux<Comment> createCommentsInitial(CommentRepository commentRepository,
                                               BookRepository bookRepository) {

        return bookRepository.findAll().collectList()
                .flatMapMany(books -> {
                    Comment comment1 = new Comment("Вымышленный мир снов");
                    comment1.setBook(books.get(0));

                    Comment comment2 = new Comment("Борьба с абортами");
                    comment2.setBook(books.get(0));

                    Comment comment3 = new Comment("Абсурдный вымысел!");
                    comment3.setBook(books.get(1));

                    Comment comment4 = new Comment("Смешные имена персонажей ))");
                    comment4.setBook(books.get(1));

                    Comment comment5 = new Comment("Загадочная история, требующая внимания к деталям...");
                    comment5.setBook(books.get(2));

                    return commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4, comment5));
                });
    }

}
