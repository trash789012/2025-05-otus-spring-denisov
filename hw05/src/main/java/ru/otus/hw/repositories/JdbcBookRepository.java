package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    public static final String BOOK_ID = "book_id";

    public static final String TITLE = "title";

    public static final String GENRE_ID = "genre_id";

    public static final String AUTHOR_ID = "author_id";

    public static final String ID = "id";

    public static final String BOOK_TITLE = "book_title";

    public static final String AUTHOR_NAME = "author_name";

    private final NamedParameterJdbcOperations jdbcOperations;

    private final GenreRepository genreRepository;

    @Override
    public Optional<Book> findById(long id) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(BOOK_ID, id);

        String sql = getStatementForFindById();

        Book book = jdbcOperations.query(sql, params, new BookResultSetExtractor());

        return Optional.ofNullable(book);
    }

    private String getStatementForFindById() {
        //language=sql
        String sql = """
                SELECT 
                    books.id as book_id,
                    books.title as book_title,
                    authors.id as author_id,
                    authors.full_name as author_name,
                    genres.id as genre_id,
                    genres.name as genre_name
                  FROM books
                  LEFT JOIN authors 
                    ON authors.id = books.author_id
                  LEFT JOIN books_genres bg
                    ON bg.book_id = books.id
                  LEFT JOIN genres
                   ON genres.id = bg.genre_id
                  WHERE books.id = :book_id
                  ORDER BY books.id, genres.id
                """;
        return sql;
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
        params.addValue(BOOK_ID, id);
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
                    books.id as book_id,
                    books.title as book_title,
                    authors.id as author_id,
                    authors.full_name as author_name,
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
                        rs.getLong(BOOK_ID),
                        rs.getLong(GENRE_ID)
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

        fillGenresForBooks(bookIdToGenreIds, bookMap, genreMap);
    }

    private void fillGenresForBooks(Map<Long, List<Long>> bookIdToGenreIds,
                                    Map<Long, Book> bookMap,
                                    Map<Long, Genre> genreMap) {
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
        params.addValue(TITLE, book.getTitle());
        params.addValue(AUTHOR_ID, book.getAuthor().getId());

        //language=sql
        String sql = """
                 INSERT INTO books (title, author_id) 
                    VALUES (:title, :author_id)
                """;
        jdbcOperations.update(sql, params, keyHolder, new String[]{ID});

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, book.getId());
        params.addValue(TITLE, book.getTitle());
        params.addValue(AUTHOR_ID, book.getAuthor().getId());

        //language=sql
        String sql = """
                UPDATE books
                  SET title     = :title, 
                      author_id = :author_id
                  WHERE id = :id;
                """;

        int updatedCountRows = jdbcOperations.update(sql, params);
        if (updatedCountRows == 0) {
            throw new EntityNotFoundException("Book with id %d not updated".formatted(book.getId()));
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        var genres = book.getGenres();
        if (genres.isEmpty()) {
            //жанров нет, делать ничего не нужно
            return;
        }
        //готовим batch параметры для batchUpdate
        var batchParams = genres.stream()
                .filter(Objects::nonNull)
                .map(genre -> new MapSqlParameterSource()
                        .addValue(BOOK_ID, book.getId())
                        .addValue(GENRE_ID, genre.getId()))
                .toArray(MapSqlParameterSource[]::new);

        if (batchParams.length == 0) {
            //значит нет подходящих жанров (отфильтровались)
            return;
        }
        //language=sql
        String sql = """
                INSERT INTO books_genres (book_id, genre_id)
                  VALUES (:book_id, :genre_id)
                """;
        jdbcOperations.batchUpdate(sql, batchParams);
    }

    private void removeGenresRelationsFor(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(BOOK_ID, book.getId());

        //language=sql
        String sql = """
                DELETE FROM books_genres WHERE book_id = :book_id
                """;

        jdbcOperations.update(sql, params);
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book();

            book.setId(rs.getLong(BOOK_ID));
            book.setTitle(rs.getString(BOOK_TITLE));
            book.setAuthor(new Author(
                    rs.getLong(AUTHOR_ID),
                    rs.getString(AUTHOR_NAME)
            ));

            return book;
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            Book book = null;

            while (rs.next()) {
                if (book == null) {
                    book = new Book();
                    book.setId(rs.getLong(BOOK_ID));
                    book.setTitle(rs.getString(BOOK_TITLE));

                    setAuthorInExtract(rs, book);

                    book.setGenres(new ArrayList<>());
                }

                setGenreInExtract(rs, book);
            }

            return book;
        }
    }

    private static void setGenreInExtract(ResultSet rs, Book book) throws SQLException {
        if (rs.getObject(GENRE_ID) != null) {
            Genre genre = new Genre();
            genre.setId(rs.getLong(GENRE_ID));
            genre.setName(rs.getString("genre_name"));
            book.getGenres().add(genre);
        }
    }

    private static void setAuthorInExtract(ResultSet rs, Book book) throws SQLException {
        if (rs.getObject(AUTHOR_ID) != null) {
            book.setAuthor(new Author(
                    rs.getLong(AUTHOR_ID),
                    rs.getString(AUTHOR_NAME)
            ));
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
