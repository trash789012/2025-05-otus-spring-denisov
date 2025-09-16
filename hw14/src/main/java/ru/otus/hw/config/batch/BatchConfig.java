package ru.otus.hw.config.batch;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;

    private final MappingCache mappingCache;

    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job migrationJob(Step authorMigrationStep,
                            Step genreMigrationStep,
                            Step bookGenresMigrationStep,
                            Step bookMigrationStep,
                            Step commentMigrationStep) {
        return new JobBuilder("migration", jobRepository)
                .start(parallelMigrationSteps(authorMigrationStep, genreMigrationStep, bookGenresMigrationStep))
                .next(bookMigrationStep)
                .next(commentMigrationStep)
                .next(cleanUpStep())
                .build()
                .build();
    }

    @Bean
    public Flow parallelMigrationSteps(Step authorMigrationStep,
                                       Step genreMigrationStep,
                                       Step bookGenresMigrationStep) {

        return new FlowBuilder<Flow>("parallelMigrationSteps")
                .split(new SimpleAsyncTaskExecutor())
                .add(
                        new FlowBuilder<Flow>("authorFlow")
                                .start(authorMigrationStep)
                                .build(),
                        new FlowBuilder<Flow>("genreFlow")
                                .start(genreMigrationStep)
                                .build(),
                        new FlowBuilder<Flow>("bookGenresFlow")
                                .start(bookGenresMigrationStep)
                                .build()
                )
                .build();
    }

    @Bean
    public Step cleanUpStep() {
        return new StepBuilder("cleanCache", jobRepository)
                .tasklet(cleanCacheTasklet(), transactionManager)
                .build();
    }

    public MethodInvokingTaskletAdapter cleanCacheTasklet() {
        MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();
        adapter.setTargetObject(mappingCache);
        adapter.setTargetMethod("clean");

        return adapter;
    }

}
