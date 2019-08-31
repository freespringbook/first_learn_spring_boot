package com.web.config;

import com.web.oauth.ClientResources;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by freejava1191@gmail.com on 2019-08-31
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 *
 * 각 소셜 미디어 리소스 정보를 빈으로 등록
 * 3개의 소셜 미디어 프로퍼티를 @ConfigurationProperties 어노테이션에 접두사를 사용하여 바인딩
 */
@Configuration
public class SecurityConfig {
    @Bean
    @ConfigurationProperties("facebook")
    public ClientResources facebook() {
        return new ClientResources();
    }

    @Bean
    @ConfigurationProperties("google")
    public ClientResources google() {
        return new ClientResources();
    }

    @Bean
    @ConfigurationProperties("kakao")
    public ClientResources kakao() {
        return new ClientResources();
    }

}
