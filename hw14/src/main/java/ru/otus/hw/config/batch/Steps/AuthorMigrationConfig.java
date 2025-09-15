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
import ru.otus.hw.models.h2.Author;
import ru.otus.hw.models.mongo.AuthorMongo;

@Configuration
@RequiredArgsConstructor
public class AuthorMigrationConfig {

    private final MongoTemplate mongoTemplate;

    private final JobRepository jobRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final PlatformTransactionManager transactionManager;

    private final MappingCache mappingCache;

    @Bean
    @StepScope
    public JpaCursorItemReader<Author> authorReader() {
        JpaCursorItemReader<Author> reader = new JpaCursorItemReader<>();
        reader.setName("authorsReader");
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("select a from Author a");
        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<Author, AuthorMongo> auhthorProcessor() {
        return author -> {
            String mongoId = new ObjectId().toString();
            mappingCache.addAuthorMapping(author.getId(), mongoId);
            return new AuthorMongo(mongoId, author.getFullName());
        };
    }

    @Bean
    @StepScope
    public MongoItemWriter<AuthorMongo> authorWriter() {
        MongoItemWriter<AuthorMongo> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("authors");
        writer.setMode(MongoItemWriter.Mode.INSERT);
        return writer;
    }

    @Bean
    public Step authorMigrationStep(ItemReader<Author> reader,
                                    ItemProcessor<Author, AuthorMongo> processor,
                                    ItemWriter<AuthorMongo> writer) {
        return new StepBuilder("authorMigrationStep", jobRepository)
                .<Author, AuthorMongo>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
