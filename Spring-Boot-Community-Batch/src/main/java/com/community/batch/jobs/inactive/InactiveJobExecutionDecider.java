package com.community.batch.jobs.inactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import java.util.Random;

/**
 * Created by freejava1191@gmail.com on 2019-09-16
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 *
 * 랜덤하게 정수를 생성해 양수면 Step을 실행
 * 음수면 아무런 행동도 취하지 않도록 Flow를 사용해 설정
 *
 * 랜덤 정숫값이 음수가 나와 테스트가 실패함
 */
@Slf4j
public class InactiveJobExecutionDecider implements JobExecutionDecider {
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        // Random 객체를 사용해 랜덤한 정숫값을 생성하고 양수인지 확인함
        if(new Random().nextInt() > 0) {
            log.info("FlowExecutionStatus.COMPLETED");
            // 양수면 FlowExecutionStatus.COMPLETED를 반환함
            return FlowExecutionStatus.COMPLETED;
        }
        log.info("FlowExecutionStatus.FAILED");
        // 음수면 FlowExecutionStatus.FAILED를 반환함
        return FlowExecutionStatus.FAILED;
    }
}
