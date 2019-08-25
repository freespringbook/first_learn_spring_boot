package com.havi;

import com.havi.domain.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by freejava1191@gmail.com on 2019-08-25
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@RunWith(SpringRunner.class)
@JsonTest
public class BookJsonTest {

    @Autowired
    private JacksonTester<Book> json;

    @Test
    public void json_테스트() throws IOException {
        Book book = Book.builder()
                .title("테스트")
                .build();
        String content = "{\test\":\"테스트\"}";

        // 문자열을 객체로 변환, 변환된 객체의 title이 일치하는지 테스트
        assertThat(this.json.parseObject(content).getTitle()).isEqualTo(book.getTitle());
        // 문자열을 객체로 변환, publishedAt 값을 정의하지 않았기 때문에 null인지 테스트
        assertThat(this.json.parseObject(content).getPublishedAt()).isNull();
        // 각 필드를 변화한 문자열이 test.json 파일에 정의한 내용과 일치하는지 테스트
        assertThat(this.json.write(book)).isEqualToJson("/test.json");
        // title 값이 있는지 테스트
        assertThat(this.json.write(book)).hasJsonPathStringValue("title");
        // title 값이 일치하는지 테스트
        assertThat(this.json.write(book)).extractingJsonPathStringValue("title").isEqualTo("테스트");
    }
}
