package com.emard.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class HelloStepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("this is from before STEP execution context "+stepExecution.getJobExecution().getExecutionContext());
        System.out.println("Inside step exec "+stepExecution.getJobExecution().getJobParameters());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("this is from after STEP execution context "+stepExecution.getJobExecution().getExecutionContext());
        return null;
    }
    
}
