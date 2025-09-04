package ru.otus.hw.mongo.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.repositories.UserRepository;

import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "dropDb", author = "nadenisov", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "createAuthorsInitial", author = "nadenisov")
    public void createAuthorsInitial(AuthorRepository repository) {
        repository.save(new Author("Стивен Кинг"));
        repository.save(new Author("Борис Виан"));
        repository.save(new Author("Джон Гришен"));
    }

    @ChangeSet(order = "003", id = "creteGenresInitial", author = "nadenisov")
    public void creteGenresInitial(GenreRepository repository) {
        repository.save(new Genre("Ужасы"));
        repository.save(new Genre("Мистика"));
        repository.save(new Genre("Драма"));
        repository.save(new Genre("Фантастика"));
        repository.save(new Genre("Детектив"));
        repository.save(new Genre("Криминал"));
    }

    @ChangeSet(order = "004", id = "createBooksInitial", author = "nadenisov")
    public void createBooksInitial(BookRepository bookRepository,
                                   AuthorRepository authorRepository,
                                   GenreRepository genreRepository) {
        var authors = authorRepository.findAll();
        var genres = genreRepository.findAll();

        Book book1 = new Book("Бессонница");
        book1.setAuthor(authors.get(0));
        book1.setGenres(List.of(genres.get(0), genres.get(1)));

        Book book2 = new Book("Осень в Пекине");
        book2.setAuthor(authors.get(1));
        book2.setGenres(List.of(genres.get(2), genres.get(3)));

        Book book3 = new Book("Камера");
        book3.setAuthor(authors.get(2));
        book3.setGenres(List.of(genres.get(4), genres.get(5)));

        bookRepository.saveAll(List.of(book1, book2, book3));
    }

    @ChangeSet(order = "005", id = "createCommentsInitial", author = "nadenisov")
    public void createCommentsInitial(CommentRepository commentRepository,
                                      BookRepository bookRepository) {

        var books = bookRepository.findAll();

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

        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4, comment5));
    }

    @ChangeSet(order = "006", id = "createUsersInitial", author = "nadenisov")
    public void createUsersInitial(UserRepository userRepository) {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("$2a$10$b.fzuJt6/2MuoWJbTjkfROKnWgEtOEL4ptZZQ34cMaCmKoero7WRi"); //admin
        admin.setRoles(List.of("ADMIN", "USER"));

        User user = new User();
        user.setUsername("user");
        user.setPassword("$2a$10$fEDUfr3CGdsykk4KwP7LWuW3v577oIEgqgcQcYcBcL1aOJpV5cdX6"); //user
        user.setRoles(List.of("USER"));

        userRepository.saveAll(List.of(admin, user));
    }
}
