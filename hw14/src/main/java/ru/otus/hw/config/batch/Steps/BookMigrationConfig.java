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

import java.util.List;

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
        reader.setQueryString("select b from Book a");
        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<Book, BookMongo> bookProcessor() {
        return book -> {
            String mongoId = new ObjectId().toString();
            String authorMongoId = mappingCache.getAuthorId(book.getAuthor().getId());

            var genres = mappingCache.getBookGenreIds(book.getId());
            List<GenreMongo> mongoGenres = genres.stream()
                    .map(mappingCache::getGenreId)     // получаем Mongo-ID жанра
                    .map(id -> {
                        GenreMongo genreMongo = new GenreMongo();
                        genreMongo.setId(id);
                        return genreMongo;
                    })
                    .toList();

            mappingCache.addBookMapping(book.getId(), mongoId);
            return new BookMongo(mongoId,
                    book.getTitle(),
                    new AuthorMongo(authorMongoId, null),
                    mongoGenres);
        };
    }

    @Bean
    @StepScope
    public MongoItemWriter<BookMongo> bookWriter() {
        MongoItemWriter<BookMongo> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("books");
        writer.setMode(MongoItemWriter.Mode.INSERT);
        return writer;
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
