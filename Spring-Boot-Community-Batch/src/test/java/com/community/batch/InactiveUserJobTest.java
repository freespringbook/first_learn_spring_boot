package com.community.batch;

import com.community.batch.domain.enums.UserStatus;
import com.community.batch.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.batch.runtime.BatchStatus;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * Created by freejava1191@gmail.com on 2019-09-13
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InactiveUserJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 휴면_회원_전환_테스트() throws Exception {
        // launchJob() 메서드로 Job을 실행시킴
        // launchJob() 메서드의 반환값으로 실행 결과에 대한 정보를 담고 있는 JobExecution이 반환됨
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // getStatus() 값이 COMPLETED로 출력되면 Job의 실행 여부 테스트는 성공
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        // 업데이트된 날짜가 1년 전이며 User 상탯값이 ACTIVE인 사용자들이 없어야 휴면회원 배치 테스트가 성공
        assertEquals(0, userRepository.findByUpdatedDateBeforeAndStatusEquals(LocalDateTime.now().minusYears(1), UserStatus.ACTIVE).size());
    }

}
