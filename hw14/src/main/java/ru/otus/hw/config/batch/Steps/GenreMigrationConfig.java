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

import java.util.Map;

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
    public JpaCursorItemReader<Genre> genreReader() {
        JpaCursorItemReader<Genre> reader = new JpaCursorItemReader<>();
        reader.setName("genresReader");
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("select g from Genre g");
        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<Genre, GenreMongo> genreProcessor() {
        return genre -> {
            String mongoId = new ObjectId().toString();
            return new GenreMongo(mongoId, genre.getName(), genre.getId());
        };
    }

    @Bean
    @StepScope
    public ItemWriter<GenreMongo> genreWriter() {
        MongoItemWriter<GenreMongo> mongoWriter = new MongoItemWriter<>();
        mongoWriter.setTemplate(mongoTemplate);
        mongoWriter.setCollection("genres");
        mongoWriter.setMode(MongoItemWriter.Mode.INSERT);

        return items -> {
            if (items.isEmpty()) return;

            mongoWriter.write(items);

            Map<Long, String> genreMapping = new java.util.HashMap<>();
            for (GenreMongo genreMongo : items) {
                genreMapping.put(genreMongo.getOldId(), genreMongo.getId());
            }

            mappingCache.putAll("genre", genreMapping);
        };
    }

    @Bean
    public Step genreMigrationStep(ItemReader<Genre> reader,
                                   ItemProcessor<Genre, GenreMongo> processor,
                                   ItemWriter<GenreMongo> writer) {
        return new StepBuilder("genreMigrationStep", jobRepository)
                .<Genre, GenreMongo>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
