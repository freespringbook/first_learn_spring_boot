package com.community.rest;

import com.community.rest.domain.Board;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by freejava1191@gmail.com on 2019-09-07
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@RunWith(SpringRunner.class)
/*
    스프링 부트 데이터 레스트를 테스트하기 위해 시큐리티 설정이 들어 있는 DataRestApplication 클래스를 주입함
    포트도 설정에 정의되어 있는 8081을 동일하게 사용하기 위해 DEFINED_PORT로 지정하여 사용함
 */
@SpringBootTest(classes = DataRestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
/*
    @AutoConfigureTestDatabase 어노테이션은 H2가 build.gradle의 클래스 경로에 포함되어 있으면 자동으로 H2를 테스트 데이터베이스로 지정함
    만약 이 어노테이션을 사용하지 않는다면 테스트에서 Board를 저장할 때마다 실제 데이터베이스에 반영될 겁니다
 */
@AutoConfigureTestDatabase
public class BoardEventTest {
    /*
        TestRestTemplate은 RestTemplate을 래핑한 객체로서 GET, POST, PUT, DELETE와 같은 HttpRequest를 편하게 테스트하도록 도와줌
        예제에서처럼 시큐리티 설정에 ADMIN으로 생성한 'havi'의 정보를 파라미터로 넣어주면 해당 아이디로 권한 인증이 통과됨
        이와 같은 설정을 해주는 이유는 '각 메서드 권한 제한'에서 저장한 메서드에 권한을 ADMIN으로 설정했기 때문
     */
    private TestRestTemplate testRestTemplate = new TestRestTemplate("havi", "test");

    @Test
    public void 저장할때_이벤트가_적용되어_생성날짜가_생성되는가() {
        /*
            Board에 title 값만 부여하여 저장했음
            저장된 Board 객체의 createdDate 값이 null이 아니라면 beforeCreateBoard 메서드가 Board 객체가 저장되기 전에 제대로 실행된 것임
         */
        Board createdBoard = createBoard();
        assertThat(createdBoard.getCreatedDate()).isNotNull();
    }

    @Test
    public void 수정할때_이벤트가_적용되어_수정날짜가_생성되는가() {
        Board createdBoard = createBoard();
        /*
            수정 시 이벤트가 적용되는지 테스트하기 위해 createBoard 메서드를 재사용하여 Board 객체를 생성하고 updateBoard 메서드를 통해 PUT 방식으로 데이터를 수정하는 요청을 보냈음
            수정이 정상적으로 완료된 Board 객체의 updatedDate 값이 null이 아니라면 설정했던 beforeSaveBoard 메서드가 수정되기 전에 제대로 실행된 것임
         */
        Board updatedBoard = updatedBoard(createdBoard);
        assertThat(updatedBoard.getUpdatedDate()).isNotNull();
    }

    private Board createBoard() {
        Board board = Board.builder().title("저장 이벤트 테스트").build();
        return testRestTemplate.postForObject("http://127.0.0.1:8081/api/boards", board, Board.class);
    }

    private Board updatedBoard(Board createdBoard) {
        String updateUri = "http://127.0.0.1:8081/api/boards/1";
        testRestTemplate.put(updateUri, createdBoard);
        return testRestTemplate.getForObject(updateUri, Board.class);
    }
}
