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
import ru.otus.hw.models.h2.Genre;
import ru.otus.hw.models.mongo.GenreMongo;

@Configuration
@RequiredArgsConstructor
public class GenreMigrationConfig {

    private final MongoTemplate mongoTemplate;

    private final JobRepository jobRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final PlatformTransactionManager transactionManager;

    private final MappingCache mappingCache;


    @Bean
    @StepScope
    public JpaCursorItemReader<Genre> reader() {
        JpaCursorItemReader<Genre> reader = new JpaCursorItemReader<>();
        reader.setName("genresReader");
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("select g from Genre g");
        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<Genre, GenreMongo> processor() {
        return genre -> {
            String mongoId = new ObjectId().toString();
            mappingCache.addGenreMapping(genre.getId(), mongoId);
            return new GenreMongo(mongoId, genre.getName());
        };
    }

    @Bean
    @StepScope
    public MongoItemWriter<GenreMongo> writer() {
        MongoItemWriter<GenreMongo> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("genres");
        writer.setMode(MongoItemWriter.Mode.INSERT);
        return writer;
    }

    @Bean
    public Step migrationStep(ItemReader<Genre> reader,
                                   ItemProcessor<Genre, GenreMongo> processor,
                                   ItemWriter<GenreMongo> writer) {
        return new StepBuilder("authorMigrationStep", jobRepository)
                .<Genre, GenreMongo>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }


}
