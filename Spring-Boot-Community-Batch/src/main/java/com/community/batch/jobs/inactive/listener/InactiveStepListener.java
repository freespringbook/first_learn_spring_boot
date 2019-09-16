package com.community.batch.jobs.inactive.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;

/**
 * Created by freejava1191@gmail.com on 2019-09-16
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@Slf4j
@Component
public class InactiveStepListener {

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        log.info("Before Step");
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        log.info("After Step");
    }
}
