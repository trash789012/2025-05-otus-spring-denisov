package ru.otus.hw.config.batch.Steps;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.config.batch.MappingCache;
import ru.otus.hw.models.h2.Book;
import ru.otus.hw.models.mongo.AuthorMongo;
import ru.otus.hw.models.mongo.BookMongo;
import ru.otus.hw.models.mongo.GenreMongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class BookMigrationConfig {
    private final MongoTemplate mongoTemplate;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final MappingCache mappingCache;

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    @StepScope
    public JpaCursorItemReader<Book> bookReader() {
        JpaCursorItemReader<Book> reader = new JpaCursorItemReader<>();
        reader.setName("bookReader");
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("select b from Book b");
        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<Book, BookMongo> bookProcessor() {
        return book -> {
            String mongoId = new ObjectId().toString();

            String authorMongoId = mappingCache.get("author", book.getAuthor().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Author mapping not found for id " + book.getAuthor().getId()
                    ));
            AuthorMongo authorRef = new AuthorMongo(authorMongoId, null, null);

            Map<Long, List<String>> bookGenresMapping = mappingCache.getBatchList("book_genre", List.of(book.getId()));
            List<GenreMongo> mongoGenres = new ArrayList<>();

            List<String> genreIds = bookGenresMapping.getOrDefault(book.getId(), Collections.emptyList());
            for (String genreId : genreIds) {
                String mongoGenreId = mappingCache.get("genre", Long.parseLong(genreId))
                        .orElseThrow(() -> new RuntimeException(
                                "Genre mapping not found for id " + genreId
                        ));
                GenreMongo genreMongo = new GenreMongo();
                genreMongo.setId(mongoGenreId);
                mongoGenres.add(genreMongo);
            }

            return new BookMongo(mongoId,
                    book.getTitle(),
                    authorRef,
                    mongoGenres,
                    book.getId());
        };
    }

    @Bean
    @StepScope
    public ItemWriter<BookMongo> bookWriter() {
        MongoItemWriter<BookMongo> mongoWriter = new MongoItemWriter<>();
        mongoWriter.setTemplate(mongoTemplate);
        mongoWriter.setCollection("books");
        mongoWriter.setMode(MongoItemWriter.Mode.INSERT);

        return items -> {
            if (items.isEmpty()) return;

            mongoWriter.write(items);

            Map<Long, String> bookMapping = new HashMap<>();
            for (BookMongo b : items) {
                bookMapping.put(b.getOldId(), b.getId());
            }

            mappingCache.putAll("book", bookMapping);
        };
    }

    @Bean
    public Step bookMigrationStep(ItemReader<Book> reader,
                                  ItemProcessor<Book, BookMongo> processor,
                                  ItemWriter<BookMongo> writer) {
        return new StepBuilder("bookMigrationStep", jobRepository)
                .<Book, BookMongo>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
