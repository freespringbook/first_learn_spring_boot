package com.havi.controller;

import com.havi.domain.Book;
import com.havi.service.BookService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.contains;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by freejava1191@gmail.com on 2019-08-25
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @Test
    public void Book_MVC_테스트() throws Exception {
        Book book = new Book("Spring Boot Book", LocalDateTime.now());
        given(bookService.getBookList()).willReturn(Collections.singletonList(book));
        mvc.perform(get("/books"))
                //HTTP 상태값이 200인지 테스트
                .andExpect(status().isOk())
                //반환되는 뷰의 이름이 'book'인지 테스트
                .andExpect(view().name("book"))
                //모델의 프로퍼티 중 'bookList'라는 프로퍼티가 존재하는지 테스트
                .andExpect(model().attributeExists("bookList"))
                //모델의 프로퍼티 중 'bookList'라는 프로퍼티에 book 객체가 담겨져 있는지 테스트
                .andExpect(model().attribute("bookList", contains(book)));
    }
}