package com.community.batch;

import com.community.batch.domain.enums.UserStatus;
import com.community.batch.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by freejava1191@gmail.com on 2019-09-13
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@RunWith(SpringRunner.class)
@SpringBootTest
// 테스트 시에는 H2를 사용하도록 설정
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class InactiveUserJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 휴면_회원_전환_테스트() throws Exception {
        /*
            현재 날짜를 Date 타입으로 생성함
            Date 타입은 JobParameter에서 허용하는 파라미터 중 하나임
         */
        Date nowDate = new Date();
        // launchJob() 메서드로 Job을 실행시킴
        // launchJob() 메서드의 반환값으로 실행 결과에 대한 정보를 담고 있는 JobExecution이 반환됨
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                /*
                    JobParametersBuilder를 사용하면 간편하게 JobParameters를 생성할 수 있음
                    JobParameters는 여러 JobParameter를 받는 객체임
                    JobLauncher를 사용하려면 JobParameters가 필요함
                 */
                new JobParametersBuilder().addDate("nowDate", nowDate).toJobParameters()
        );

        // getStatus() 값이 COMPLETED로 출력되면 Job의 실행 여부 테스트는 성공
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        assertEquals(11, userRepository.findAll().size());
        // 업데이트된 날짜가 1년 전이며 User 상탯값이 ACTIVE인 사용자들이 없어야 휴면회원 배치 테스트가 성공
        assertEquals(0, userRepository.findByUpdatedDateBeforeAndStatusEquals(LocalDateTime.now().minusYears(1), UserStatus.ACTIVE).size());
    }

}
