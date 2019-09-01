package com.web;

import com.web.domain.Board;
import com.web.domain.User;
import com.web.domain.enums.BoardType;
import com.web.repository.BoardRepository;
import com.web.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by freejava1191@gmail.com on 2019-08-27
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
// Junit에 내장된 러너를 사용하는 대신 어노테이션에 정의된 클래스를 호출
// Junit의 확장 기능을 지정하여 각 테스트 시 독립적인 애플리케이션 컨텍스트를 보장함
@RunWith(SpringRunner.class)
// 스프링 부트에서 JPA 테스트를 위한 전용 어노테이션
// 첫 설계 시 엔티티 간의 관계 설정 및 기능 테스트를 가능하게 도와줌
// 테스트가 끝날 때마다 자동 롤백을 해주어 편리한 JPA 테스트가 가능함
@DataJpaTest
public class JpaMappingTest {
    private final String boardTestTitle = "테스트";
    private final String email = "test@gmail.com";

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Before // 각 테스트가 실행되기 전에 실행될 메서드를 선언
    public void init() {
        User user = userRepository.save(User.builder()
            .name("havi")
            .password("test")
            .email(email)
            .createdDate(LocalDateTime.now())
            .build());

        boardRepository.save(Board.builder()
            .title(boardTestTitle)
            .subTitle("서브 타이틀")
            .content("콘텐츠")
            .boardType(BoardType.free)
            .createdDate(LocalDateTime.now())
            .updatedDate(LocalDateTime.now())
            .user(user).build());
    }

    @Test // 실제 테스트가 진행될 메서드를 선언
    public void 제대로_생성됐는지_테스트() {
        User user = userRepository.findByEmail(email); // init()에서 저장된 user를 email로 조회
        assertThat(user.getName(), is("havi")); // 각 필드가 저장된 값과 일치하는지 검사
        assertThat(user.getPassword(), is("test"));
        assertThat(user.getEmail(), is(email));

        Board board = boardRepository.findByUser(user); // init()에서 저장한 board를 작성자인 user를 사용하여 조회하고 해당 필드가 올바른지 체크
        assertThat(board.getTitle(), is(boardTestTitle));
        assertThat(board.getSubTitle(), is("서브 타이틀"));
        assertThat(board.getContent(), is("콘텐츠"));
        assertThat(board.getBoardType(), is(BoardType.free));

    }

}
