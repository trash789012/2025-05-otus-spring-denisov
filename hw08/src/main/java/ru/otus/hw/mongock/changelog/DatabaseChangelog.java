package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
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
    public void createAuthorsInitial(AuthorRepository repository) {
        repository.save(new Author("Author_1"));
        repository.save(new Author("Author_2"));
        repository.save(new Author("Author_3"));
    }

    @ChangeSet(order = "003", id = "creteGenresInitial", author = "nadenisov")
    public void creteGenresInitial(GenreRepository repository) {
        repository.save(new Genre("Genre_1"));
        repository.save(new Genre("Genre_2"));
        repository.save(new Genre("Genre_3"));
        repository.save(new Genre("Genre_4"));
        repository.save(new Genre("Genre_5"));
        repository.save(new Genre("Genre_6"));
    }

    @ChangeSet(order = "004", id = "createBooksInitial", author = "nadenisov")
    public void createBooksInitial(BookRepository bookRepository,
                                   AuthorRepository authorRepository,
                                   GenreRepository genreRepository) {
        var authors = authorRepository.findAll();
        var genres = genreRepository.findAll();

        Book book1 = new Book("BookTitle_1");
        book1.setAuthor(authors.get(0));
        book1.setGenres(List.of(genres.get(0), genres.get(1)));

        Book book2 = new Book("BookTitle_2");
        book2.setAuthor(authors.get(1));
        book2.setGenres(List.of(genres.get(2), genres.get(3)));

        Book book3 = new Book("BookTitle_3");
        book3.setAuthor(authors.get(2));
        book3.setGenres(List.of(genres.get(4), genres.get(5)));

        bookRepository.saveAll(List.of(book1, book2, book3));
    }

    @ChangeSet(order = "005", id = "createCommentsInitial", author = "nadenisov")
    public void createCommentsInitial(CommentRepository repository,
                                      BookRepository bookRepository) {

        var books = bookRepository.findAll();

        Comment comment1 = new Comment("Комментарий книги 1");
        comment1.setBook(books.get(0));

        Comment comment2 = new Comment("Второй комментарий книги 1");
        comment2.setBook(books.get(0));

        Comment comment3 = new Comment("Комментарий книги 2");
        comment3.setBook(books.get(1));

        Comment comment4 = new Comment("Второй комментарий книги 2");
        comment4.setBook(books.get(1));

        Comment comment5 = new Comment("Единственный комментарий книги 3");
        comment5.setBook(books.get(2));
    }

}
