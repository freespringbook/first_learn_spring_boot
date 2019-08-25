package com.havi;

import com.havi.domain.Book;
import com.havi.service.BookRestService;
import com.sun.net.httpserver.HttpServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Created by freejava1191@gmail.com on 2019-08-25
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@RunWith(SpringRunner.class)
/**
 * @RestClientTest는 테스트 대상이 되는 빈을 주입받음
 * @RestClientTest 어노테이션이 BookRestService.class를 파라미터로 주입받지 못하면 '빈이 없다'는 에러가 뜸
 */
@RestClientTest(BookRestService.class)
public class BookRestTest {

    /**
     * @Rule로 지정한 필드값은 @Before나 @After 어노테이션에 상관없이 하나의 테스트 메서드가 끝날 때마다 정의한 값으로 초기화 시켜줌
     * 테스트에서 자체적으로 규칙을 정의하여 재사용할 떄 유용함
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private BookRestService bookRestService;

    /**
     * MockRestServiceServer는 클라이언트와 서버 사이의 REST 테스트를 위한 객체
     * 내부에서 RestTemplate을 바인딩하여 실제로 통신이 이루어지게끔 구성할 수도 있음
     * 이 코드에서에서는 목 객체와 같이 실제로 통신이 이루어지지는 않지만 지정한 경로에 예상되는 반환값
     * 혹은 에러를 반환하도록 명시하여 간단하게 테스트를 진행하도록 작성함
     */
    @Autowired
    private MockRestServiceServer server;

    /**
     * rest_테스트() 메서드는 요청에 대해 응답과 기댓값이 같은지 테스트
     * '/rest/test' 경로로 요청을 보내면 현재 리소스 폴더에 생성되어 있는 test.json 파일의 데이터로 응답을 주도록 설정
     * (이전에 보았던 목 객체와 비슷한 역할을 함) 그리고 bookRestService의 getRestBook() 메서드를 실행하여 컨트롤러에서 가져온 기댓값
     * (test.json 파일의 데이터)과 직접 가져온 Book 값이 일치하는지 확인
     * 반드시 test 디렉토리 하위 경로로 생성해야 테스트 메서드에서 읽을 수 있음
     * 테스트 코드의 리소스 루트 경로 '/test/resources'로 잡히기 때문임
     */
    @Test
    public void rest_테스트() {
        this.server.expect(requestTo("/rest/test"))
                .andRespond(withSuccess(new ClassPathResource("/test.json", getClass()), MediaType.APPLICATION_JSON));
        Book book = this.bookRestService.getRestBook();
        assertThat(book.getTitle()).isEqualTo("테스트");
    }

    /**
     * rest_error_테스트() 메서드는 서버 에러가 발생했을 경우를 테스트
     * '/rest/test' 경로로 요청이 들어오면 서버 에러가 발생한다고 가정하여 설정함
     * 그리고 어떠한 에러가 발생했는지 ExpectedException 객체의 expect() 메서드로 지정하여 테스트
     * HTTP 500 에러 발생 클래스인 HttpServerErrorException.class를 설정하였음
     * 마지막 줄(getRestBook() 메서드 실행 코드)에서 REST 요청을 발생시킴
     * 이때 발생하는 에러가 미리 작성해둔 에러와 일치하면 성공적으로 테스트를 마치게 됨
     */
    @Test
    public void rest_error_테스트() {
        this.server.expect(requestTo("/rest/test"))
                .andRespond(withServerError());
        this.thrown.expect(HttpServerErrorException.class);
        this.bookRestService.getRestBook();
    }
}
