package com.emard.batch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.emard.batch.listener.HelloJobExecutionListener;
import com.emard.batch.listener.HelloStepExecutionListener;
import com.emard.batch.processor.InMemItemProcessor;
import com.emard.batch.reader.InMemoryReader;
import com.emard.batch.writter.ConsoleItemWritter;

//@EnableBatchProcessing
//@Configuration
public class BatchConfiguration {

    private final JobBuilderFactory jobs;

    private final StepBuilderFactory steps;

    private final HelloJobExecutionListener executionListener;
    private final HelloStepExecutionListener stepListner;
    private final InMemItemProcessor inMemItemProcessor; 
    private final InMemoryReader inMemoryReader;
    private final ConsoleItemWritter consoleItemWritter;

    public BatchConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps,
            HelloJobExecutionListener executionListener, HelloStepExecutionListener stepListner,
            InMemItemProcessor inMemItemProcessor, InMemoryReader inMemoryReader, 
            ConsoleItemWritter consoleItemWritter) {
        this.jobs = jobs;
        this.steps = steps;
        this.executionListener = executionListener;
        this.stepListner = stepListner;
        this.inMemItemProcessor = inMemItemProcessor;
        this.inMemoryReader = inMemoryReader;
        this.consoleItemWritter = consoleItemWritter;
    }

    public Tasklet helloWorldTasklet() {
        return (new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("Hello world");
                return RepeatStatus.FINISHED;
            }
        });
    }

    @Bean
    public Step step1() {
        return steps.get("step1")
                .listener(stepListner)
                .tasklet(helloWorldTasklet())
                .build();
    }

    /*@Bean
    private ItemReader<? extends Integer> reader() {
        return new InMemoryReader();
    }

    @Bean
    private ItemWriter<? super Integer> writer() {
        return null;
    }

    @Bean
    private ItemProcessor<? super Integer, ? extends Integer> process() {
        return null;
    }*/

    @Bean
    public Step step2(){
        return steps.get("step2")
        .<Integer, Integer>chunk(3)
        //.reader(reader())
        .reader(inMemoryReader)
        .processor(inMemItemProcessor)
        .writer(consoleItemWritter)
        .build();
    }

    

    @Bean
    public Job helloWorldJob() {
        return jobs.get("helloWorldJob")
        .incrementer(new RunIdIncrementer())
                .listener(executionListener)
                .start(step1())
                .next(step2())
                .build();
    }
}
