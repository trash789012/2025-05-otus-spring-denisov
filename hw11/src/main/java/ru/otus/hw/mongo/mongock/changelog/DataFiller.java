package ru.otus.hw.mongo.mongock.changelog;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataFiller implements ApplicationRunner {

    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    private final GenreRepository genreRepository;

    private final CommentRepository commentRepository;

    private final Scheduler workerPool;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        dropDB()
                .then(createAuthorsInitial())
                .then(createGenresInitial())
                .then(createBooksInitial())
                .then(createCommentsInitial())
                .subscribe();
    }

    private Mono<Void> createCommentsInitial() {
        return bookRepository.findAll()
                .collectList()
                .publishOn(workerPool)
                .flatMapMany(books -> {
                    if (books.size() < 3) {
                        return Flux.error(new IllegalStateException("Not enough books to create comments"));
                    }

                    Comment comment1 = new Comment("Вымышленный мир снов");
                    comment1.setBook(books.get(0));
                    comment1.setBookId(books.get(0).getId());

                    Comment comment2 = new Comment("Борьба с абортами");
                    comment2.setBook(books.get(0));
                    comment2.setBookId(books.get(0).getId());

                    Comment comment3 = new Comment("Абсурдный вымысел!");
                    comment3.setBook(books.get(1));
                    comment3.setBookId(books.get(1).getId());

                    Comment comment4 = new Comment("Смешные имена персонажей ))");
                    comment4.setBook(books.get(1));
                    comment4.setBookId(books.get(1).getId());

                    Comment comment5 = new Comment("Загадочная история, требующая внимания к деталям...");
                    comment5.setBook(books.get(2));
                    comment5.setBookId(books.get(2).getId());

                    return commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4, comment5));
                })
                .then();
    }


    private Mono<Void> createBooksInitial() {
        return Mono.zip(
                        authorRepository.findAll().collectList(),
                        genreRepository.findAll().collectList()
                )
                .publishOn(workerPool)
                .flatMapMany(tuple -> {
                    List<Author> authors = tuple.getT1();
                    List<Genre> genres = tuple.getT2();

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
                })
                .then();
    }

    private Mono<Void> createGenresInitial() {
        List<Genre> genres = List.of(
                new Genre("Ужасы"),
                new Genre("Мистика"),
                new Genre("Драма"),
                new Genre("Фантастика"),
                new Genre("Детектив"),
                new Genre("Криминал")
        );
        return genreRepository.saveAll(genres)
                .publishOn(workerPool)
                .then();
    }

    private Mono<Void> createAuthorsInitial() {
        List<Author> authors = List.of(
                new Author("Стивен Кинг"),
                new Author("Борис Виан"),
                new Author("Джон Гришен")
        );
        return authorRepository.saveAll(authors)
                .publishOn(workerPool)
                .then();
    }

    private Mono<Void> dropDB() {
        return Mono.when(
                commentRepository.deleteAll(),
                bookRepository.deleteAll(),
                genreRepository.deleteAll(),
                authorRepository.deleteAll()
        ).publishOn(workerPool).then();
    }
}
