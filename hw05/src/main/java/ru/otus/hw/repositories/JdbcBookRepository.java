package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {
        //TODO
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("book_id", id);
        //language=sql
        String sql = """
                DELETE FROM books WHERE id = :book_id
                """;

        jdbcOperations.update(sql, params);
    }

    private List<Book> getAllBooksWithoutGenres() {
        //language=sql
        String sql = """
                SELECT 
                    book.id as book_id,
                    book.title as book_title,
                    author.id as author_id,
                    author.full_name as author_name,
                  FROM books 
                  JOIN authors
                    ON authors.id = books.author_id
                """;
        return jdbcOperations.query(sql, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        //language=sql
        String sql = """
                SELECT 
                    book_id,
                    genre_id
                  FROM books_genres
                """;

        var relations = jdbcOperations.query(sql, (rs, rowNum) ->
                new BookGenreRelation(
                        rs.getLong("book_id"),
                        rs.getLong("genre_id")
                ));

        return relations != null ? relations : Collections.emptyList();
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres,
                                List<Genre> genres,
                                List<BookGenreRelation> relations) {
        //для быстрого доступа сделаем lookup мэпы
        Map<Long, Book> bookMap = booksWithoutGenres.stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));

        Map<Long, Genre> genreMap = genres.stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity()));

        //группируем связь между книгой и жанром (1..N)
        Map<Long, List<Long>> bookIdToGenreIds = relations.stream()
                .collect(Collectors.groupingBy(
                        BookGenreRelation::bookId,
                        Collectors.mapping(BookGenreRelation::genreId, Collectors.toList())
                ));

        bookIdToGenreIds.forEach((bookId, genreIds) -> {
            //взяли book
            Book book = bookMap.get(bookId);
            if (book != null) {
                //теперь наполним жанрами
                List<Genre> genreList = genreIds.stream()
                        .map(genreMap::get)
                        .filter(Objects::nonNull)
                        .toList();

                book.setGenres(genreList);
            }
        });
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());

        //language=sql
        String  sql = """
                 INSERT INTO books (title, author_id) 
                    VALUES (:title, :author_id)
                """;
        jdbcOperations.update(sql, params, keyHolder, new String[]{"id"});

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        //...

        // Выбросить EntityNotFoundException если не обновлено ни одной записи в БД
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        // Использовать метод batchUpdate
        //TODO
    }

    private void removeGenresRelationsFor(Book book) {
        //...
        //TODO
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book();

            book.setId(rs.getLong("book_id"));
            book.setTitle(rs.getString("book_title"));
            book.setAuthor(new Author(
                    rs.getLong("author_id"),
                    rs.getString("author_name")
            ));

            return book;
        }
    }

    // Использовать для findById
    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            return null;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
