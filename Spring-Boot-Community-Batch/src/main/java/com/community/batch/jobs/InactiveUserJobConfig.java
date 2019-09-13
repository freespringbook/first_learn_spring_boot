package com.community.batch.jobs;

import com.community.batch.domain.User;
import com.community.batch.domain.enums.UserStatus;
import com.community.batch.jobs.readers.QueueItemReader;
import com.community.batch.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by freejava1191@gmail.com on 2019-09-13
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@Configuration
@AllArgsConstructor
public class InactiveUserJobConfig {

    private final UserRepository userRepository;

    @Bean
    // Job 생성을 직관적이고 편리하게 도와주는 빌더인 JobBuilderFactory를 주입
    // 빈에 주입할 객체를 파라미터로 명시하면 @Autowired 어노테이션을 쓰는 것과 같은 효과가 있음
    public Job InactiveUserJob(JobBuilderFactory jobBuilderFactory, Step inactiveJobStep) {
        // JobBuilderFactory의 get("inactiveUserJob")은'inactiveUserJob'이라는 이름의 JobBuilder를 생성
        return jobBuilderFactory.get("inactiveUserJob")
                // preventRestart()는 Job의 재실행을 막음
                .preventRestart()
                // start(inactiveJobStep)은 파라미터에서 주입받은 휴면회원 관련 Step인 inactiveJobStep을 제일 먼저 실행하도록 설정하는 부분임
                // inactiveJobStep은 앞선 inactiveUserJob과 같이 InactiveUserJobConfig 클래스에 빈으로 등록
                .start(inactiveJobStep)
                .build();
    }

    @Bean
    public Step getInactiveJobStep(StepBuilderFactory stepBuilderFactory) {
        // StepBuilderFactory의 get("inactiveUserStep")은 'inactiveUserStep'이라는 이름의 StepBuilder를 생성함
        return stepBuilderFactory.get("inactiveUserStep")
                // 제네릭을 사용해 chunk()의 입력 타입과 출력 타입을 User 타입으로 설정했음
                // chunk의 인잣값은 10으로 설정했는데 쓰기 시에 청크 단위로 묶어서 writer() 메서드를 실행시킬 단위를 지정한 것
                // 즉, 커밋의 단위가 10개 임
                .<User, User> chunk(10)
                // Step의 reader.processor.writer를 각각 설정했음
                .reader(inactiveUserReader())
                .processor(inactiveUserProcessor())
                .writer(inactiveUserWriter())
                .build();
    }

    @Bean
    /**
     * 기본 빈 생성은 싱글턴이지만 @StepScope를 사용하면 해당 메서드는 Step의 주기에 따라 새로운 빈을 생성함
     * 즉, 각 Step의 실행마다 새로운 빈을 만들기 때문에 지연 생성이 가능함
     * 주의할 사항은 @StepScope는 기본 프록시 모드가 반환되는 클래스 타입을 참조하기 때문에 @StepScope를 사용하면 반드시 구현된 반환 타입을 명시해 반환해야 함
     * 여기서는 반환 타입을 QueueItemReader<User>라고 명시했음
     */
    @StepScope
    public QueueItemReader<User> inactiveUserReader() {
        // 현재 날짜 기준 1년 전의 날짜값과 User의 상태값이 ACTIVE인 User 리스트를 불러오는 쿼리
        List<User> oldUsers = userRepository.findByUpdatedDateBeforeAndStatusEquals(LocalDateTime.now().minusYears(1), UserStatus.ACTIVE);
        // QueueItemReader 객체를 생성하고 불러온 휴면회원 타깃 대상 데이터를 객체에 넣어 반환
        return new QueueItemReader<>(oldUsers);
    }


    /**
     * reader에서 읽은 User를 휴면 상태로 전환하는 processor 메서드
     * @return
     */
    private ItemProcessor<User, User> inactiveUserProcessor() {
        return User::setInactive;
        //메서드 레퍼런스 표현이 아닌 방
        /*
        return new ItemProcessor<User, User>() {
            @Override
            public User process(User user) throws Exception {
                return user.setInactive();
            }
        };
        */
    }

    /**
     * 휴면회원을 DB에 저장
     * 리스트 타입을 앞서 설정한 청크 단위로 받음
     * 청크 단위를 10으로 설정했으므로 users에는 휴면회원 10개가 주어지며 saveAll() 메서드를 사용해서 한번에 DB에 저장함
     * @return
     */
    private ItemWriter<User> inactiveUserWriter() {
        return ((List<? extends User> users) -> userRepository.saveAll(users));
    }

}
