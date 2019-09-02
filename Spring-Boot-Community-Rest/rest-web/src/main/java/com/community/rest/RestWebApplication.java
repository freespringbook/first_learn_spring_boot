package com.community.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by freejava1191@gmail.com on 2019-09-02
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@SpringBootApplication
public class RestWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestWebApplication.class, args);
    }

    @Configuration
    // @PreAuthorize 와 @PostAuthorize 를 사용하기 위해 붙이는 어노테이션
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    // 웹용 시큐리티를 활성화하는 어노테이션
    @EnableWebSecurity
    static class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            CorsConfiguration configuration = new CorsConfiguration();
            // CorsConfiguration 객체를 생성하여 CORS에서 Origin, Method, Header 별로 허용할 값을 설정할 수 있다
            // CorsConfiguration.ALL은 '*'과 같음
            // 모든 경로에 대해 허용함
            configuration.addAllowedOrigin(CorsConfiguration.ALL);
            configuration.addAllowedMethod(CorsConfiguration.ALL);
            configuration.addAllowedHeader(CorsConfiguration.ALL);
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            // 특정 경로에 대해 CorsConfiguration 객체에서 설정한 값을 CorsConfigurationSource 인터페이스를 구현한
            // UrlBasedCorsConfigurationSource에 적용시킴
            // 여기서는 모든 경로로 설정되어 있음
            source.registerCorsConfiguration("/**", configuration);

            http.httpBasic()
                    .and().authorizeRequests()
                    .anyRequest().permitAll()
                    // 스프링 시큐리티의 CORS 설정에서는 CorsConfigurationSource 인터페이스의 구현체를 파라미터로 받는 configurationSource가 있음
                    // 여기에 설정한 UrlBasedCorsConfigurationSource 객체를 넣어주면 위에 설정한 내용이 시큐리티 설정에 추가됨
                    .and().cors().configurationSource(source)
                    .and().csrf().disable();
        }
    }
}
