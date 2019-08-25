package com.havi.service;

import com.havi.domain.Book;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by freejava1191@gmail.com on 2019-08-25
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@Service
public class BookRestService {

    private final RestTemplate restTemplate;

    /**
     * RestTemplateBuilder를 사용하여 RestTemplate를 생성
     * RestTemplateBuilder는 RestTemplate를 핸들링하는 빌더 객체로 connectionTimeout, ReadTimeOut
     * 설정뿐만 아니라 여러 다른 설정을 간편하게 제공
     * @param restTemplateBuilder
     */
    public BookRestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.rootUri("/rest/test").build();
    }

    /**
     * RestTemplate의 Get 방식으로 통신하는 getForObject() 메서드를 사용하여 '/rest/test' URI에
     * 요청을 보내고 요청에 대한 응답을 Book 객체 형식으0로 받아옴
     * @return
     */
    public Book getRestBook() {
        return this.restTemplate.getForObject("/rest/test", Book.class);
    }
}
