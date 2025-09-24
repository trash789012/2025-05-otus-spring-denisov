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
import ru.otus.hw.models.h2.Comment;
import ru.otus.hw.models.mongo.BookMongo;
import ru.otus.hw.models.mongo.CommentMongo;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CommentMigrationConfig {
    private final MongoTemplate mongoTemplate;

    private final JobRepository jobRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final PlatformTransactionManager transactionManager;

    private final MappingCache mappingCache;

    @Bean
    @StepScope
    public JpaCursorItemReader<Comment> commentReader() {
        JpaCursorItemReader<Comment> reader = new JpaCursorItemReader<>();
        reader.setName("commentsReader");
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("select c from Comment c");
        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<Comment, CommentMongo> commentProcessor() {
        return comment -> {
            String mongoId = new ObjectId().toString();
            String mongoBookId = mappingCache.get("book", comment.getBook().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Book mapping not found for id " + comment.getBook().getId()
                    ));
            BookMongo bookRef = new BookMongo(mongoBookId, null, null, null, null);
            return new CommentMongo(mongoId, comment.getText(), bookRef, comment.getId());
        };
    }

    @Bean
    @StepScope
    public ItemWriter<CommentMongo> commentWriter() {
        MongoItemWriter<CommentMongo> mongoWriter = new MongoItemWriter<>();
        mongoWriter.setTemplate(mongoTemplate);
        mongoWriter.setCollection("comments");
        mongoWriter.setMode(MongoItemWriter.Mode.INSERT);

        return items -> {
            if (items.isEmpty()) return;

            mongoWriter.write(items);

            Map<Long, String> commentMapping = new java.util.HashMap<>();
            for (CommentMongo c : items) {
                commentMapping.put(c.getOldId(), c.getId());
            }

            mappingCache.putAll("comment", commentMapping);
        };
    }

    @Bean
    public Step commentMigrationStep(ItemReader<Comment> reader,
                                     ItemProcessor<Comment, CommentMongo> processor,
                                     ItemWriter<CommentMongo> writer) {
        return new StepBuilder("commentMigrationStep", jobRepository)
                .<Comment, CommentMongo>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
