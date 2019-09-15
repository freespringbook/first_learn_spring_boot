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
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by freejava1191@gmail.com on 2019-09-13
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@Configuration
@AllArgsConstructor
public class InactiveUserJobConfig {

    // 한번에 읽어올 크기
    private final static int CHUNK_SIZE = 15;
    private final EntityManagerFactory entityManagerFactory;
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
    public Step inactiveJobStep(StepBuilderFactory stepBuilderFactory, JpaPagingItemReader<User> inactiveUserJpaReader) {
        return stepBuilderFactory.get("inactiveUserStep")
                .<User, User> chunk(CHUNK_SIZE)
                .reader(inactiveUserJpaReader)
                .processor(inactiveUserProcessor())
                .writer(inactiveUserWriter())
                .build();
    }

    /**
     * 기본 빈 생성은 싱글턴이지만 @StepScope를 사용하면 해당 메서드는 Step의 주기에 따라 새로운 빈을 생성함
     * 즉, 각 Step의 실행마다 새로운 빈을 만들기 때문에 지연 생성이 가능함
     * 주의할 사항은 @StepScope는 기본 프록시 모드가 반환되는 클래스 타입을 참조하기 때문에 @StepScope를 사용하면 반드시 구현된 반환 타입을 명시해 반환해야 함
     * 여기서는 반환 타입을 JpaPagingItemReader<User>라고 명시했음
     */
    /*
        스프링에서 destroyMethod를 사용해 삭제할 빈을 자동으로 추적함
        destroyMethod=""와 같이 하여 기능을 사용하지 않도록 설정하면 실행 시 출력되는 다음과 같은 warning 메시지를 삭제할 수 있다
     */
    @Bean(destroyMethod = "")
    @StepScope
    public JpaPagingItemReader<User> inactiveUserJpaReader() {

        /*
            조회용 인덱스값을 항상 0으로 반환하여 item 5개를 수정하고
            다음 5개를 건너뛰지 않고 원하는 순서/청크 단위로 처리가 가능해짐
         */
        JpaPagingItemReader<User> jpaPagingItemReader = new JpaPagingItemReader<>(){
            @Override
            public int getPage() {
                return 0;
            }
        };
        /*
            JpaPagingItemReader를 사용하려면 쿼리를 직접 짜서 실행하는 방법밖에 없음
            마지막 정보 갱신 일자를 나타내는 updatedDate 파라미터와 상태값을 나타내는 status 파라미터를 사용해 쿼리를 작성함
         */
        jpaPagingItemReader.setQueryString("select u from User as u where u.updatedDate < :updatedDate and u.status = :status");

        Map<String, Object> map = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        map.put("updatedDate", now.minusYears(1));
        map.put("status", UserStatus.ACTIVE);

        // 쿼리에서 사용된 updatedDate.status 파라미터를 Map에 추가해 사용할 파라미터를 설정함
        jpaPagingItemReader.setParameterValues(map);
        // 트랜잭션을 관리해줄 entityManagerFactory를 설정함
        jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);
        // 한번에 읽어올 크기를 15개로 설정함
        jpaPagingItemReader.setPageSize(CHUNK_SIZE);
        return jpaPagingItemReader;
    }


    /**
     * ListItemReader 객체를 사용하면 모든 데이터를 한번에 가져와 메모리에 올려놓고 read() 메서드로 하나씩 배치 처리 작업을 수행할 수 있음
     * @return
     */
    @Bean
    @StepScope
    public ListItemReader<User> inactiveUserReader() {
        List<User> oldUsers = userRepository.findByUpdatedDateBeforeAndStatusEquals(LocalDateTime.now().minusYears(1), UserStatus.ACTIVE);
        return new ListItemReader<>(oldUsers);
    }

    /**
     * reader에서 읽은 User를 휴면 상태로 전환하는 processor 메서드
     * @return
     */
    private ItemProcessor<User, User> inactiveUserProcessor() {
        return User::setInactive;
        //메서드 레퍼런스 표현이 아닌 방식
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
     * 제네릭에 저장할 타입을 명시하고 EntityManagerFactory만 설정하면 Processor에서 넘어온 데이터를 청크 단위로 저장함
     * @return
     */
    private JpaItemWriter<User> inactiveUserWriter() {
        JpaItemWriter<User> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }


}
