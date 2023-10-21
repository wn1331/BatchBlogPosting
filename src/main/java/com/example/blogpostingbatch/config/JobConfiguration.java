package com.example.blogpostingbatch.config;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobConfiguration {

    private final PlatformTransactionManager platformTransactionManager;
    private final JobRepository jobRepository;
    int size = 0;

    @Bean
    public Job exampleJob(Step step1, Step step2) {
        return new JobBuilder("exampleJob", jobRepository)
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean
    @JobScope
    public Step step1() {
        return new StepBuilder("exampleStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("hello step1");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step step2() {
        return new StepBuilder("exampleStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("hello step2");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step step3() {
        return new StepBuilder("step1", jobRepository)
                .chunk(10, platformTransactionManager)
                .reader(() -> {
                    if (size == 11) {
                        return null;
                    }
                    System.out.println("read");
                    size++;
                    return 1;
                })
                .processor(item -> {
                    System.out.println((int) item + 1);
                    return (int) item + 1;
                })
                .writer(it -> {
                    System.out.println(it.getItems());
                })
                .build();
    }

}
