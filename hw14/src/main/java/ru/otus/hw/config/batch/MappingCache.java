package ru.otus.hw.config.batch;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MappingCache {

    private final Map<Long, String> authorIdMap = new ConcurrentHashMap<>();

    private final Map<Long, String> genreIdMap = new ConcurrentHashMap<>();

    private final Map<Long, String> bookIdMap = new ConcurrentHashMap<>();

    private final Map<Long, List<Long>> booksGenresIdMap = new ConcurrentHashMap<>();

    public void clean() {
        authorIdMap.clear();
        genreIdMap.clear();
        bookIdMap.clear();
        booksGenresIdMap.clear();
    }

    public void addAuthorMapping(Long id, String mongoId) {
        authorIdMap.put(id, mongoId);
    }

    public String getAuthorId(Long id) {
        return authorIdMap.get(id);
    }

    public void addGenreMapping(Long id, String mongoId) {
        genreIdMap.put(id, mongoId);
    }

    public String getGenreId(Long id) {
        return genreIdMap.get(id);
    }

    public void addBookMapping(Long id, String mongoId) {
        bookIdMap.put(id, mongoId);
    }

    public String getBookId(Long id) {
        return bookIdMap.get(id);
    }

    public void addBookGenreMapping(Long bookId, Long genreId) {
        booksGenresIdMap.computeIfAbsent(bookId, k -> new ArrayList<>()).add(genreId);
    }

    public List<Long> getBookGenreIds(Long id) {
        return booksGenresIdMap.get(id);
    }

}
