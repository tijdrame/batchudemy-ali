package com.emard.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class HelloJobExecutionListener implements JobExecutionListener{

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("before starting the job "+ jobExecution.getJobInstance().getJobName());
        System.out.println("before starting the job "+ jobExecution.getExecutionContext());
        jobExecution.getExecutionContext().put("my_name", "Tidiane");
        System.out.println("before starting the job set props "+ jobExecution.getExecutionContext());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("After completing the job "+ jobExecution.getExecutionContext());       
    }
    
}
