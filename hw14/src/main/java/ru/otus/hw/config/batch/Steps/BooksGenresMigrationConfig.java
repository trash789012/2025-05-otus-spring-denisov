package ru.otus.hw.config.batch.Steps;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.config.batch.MappingCache;
import ru.otus.hw.models.BookGenre;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class BooksGenresMigrationConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final MappingCache mappingCache;

    private final DataSource dataSource;

    @Bean
    @StepScope
    public JdbcCursorItemReader<BookGenre> reader() {
        JdbcCursorItemReader<BookGenre> reader = new JdbcCursorItemReader<>();
        reader.setName("booksGenresReader");
        reader.setDataSource(dataSource);
        reader.setSql("select * from books_genres");
        reader.setRowMapper((rs, rowNum) -> {
            var bookId = rs.getLong("book_id");
            var genreId = rs.getLong("genre_id");
            return new BookGenre(bookId, genreId);
        });
        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<BookGenre, BookGenre> processor() {
        return bookGenre -> {
            mappingCache.addBookGenreMapping(bookGenre.bookId(), bookGenre.genreId());
            return bookGenre;
        };
    }

    @Bean
    public Step migrationStep(ItemReader<BookGenre> reader,
                              ItemProcessor<BookGenre, BookGenre> processor) {
        return new StepBuilder("booksGenresMigrationStep", jobRepository)
                .<BookGenre, BookGenre>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(i -> {})
                .build();
    }
}
