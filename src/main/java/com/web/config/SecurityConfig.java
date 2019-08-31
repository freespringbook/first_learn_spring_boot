package com.web.config;

import com.web.domain.enums.SocialType;
import com.web.oauth.ClientResources;
import com.web.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

import static com.web.domain.enums.SocialType.*;

/**
 * Created by freejava1191@gmail.com on 2019-08-31
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 *
 * 각 소셜 미디어 리소스 정보를 빈으로 등록
 * 3개의 소셜 미디어 프로퍼티를 @ConfigurationProperties 어노테이션에 접두사를 사용하여 바인딩
 */
@Configuration
/**
 * @EnableWebSecurity 어노테이션은 웹에서 시큐리티 기능을 사용하겠다는 어노테이션임
 * 스프링부트에서는 @EnableWebSecurity를 사용하면 자동 설정이 적용됨
 */
@EnableWebSecurity
/**
 * OAuth2 설정 적용
 * @EnableAuthorizationServer - 권한 부여 서버 설정
 * @EnableResourceServer - 리소스 서버 설정
 */
@EnableOAuth2Client
/**
 * 자동 설정 그대로 사용할 수도 있지만 요청, 권한, 기타 설정에 대해서는 필수적으로 최적화한 설정이 들어가야 함
 * 최적화 설정을 위해 WebSecurityConfigurerAdapter를 상속받고 configure(HttpSecurity http) 메서드를 오버라이드하여 원하는 형식의 시큐리트 설정을 함
 */
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2ClientContext oAuth2ClientContext;

    /**
     * 오버라이드한 configure() 메서드의 설정 프로퍼티
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        http
            // 인증 메커니즘을 요청한 HttpServletRequest 기반으로 설정
            .authorizeRequests()
                // 요청 패턴을 리스트 형식으로 설정
                .antMatchers("/", "/login/**", "/css/**", "/images/**", "/js/**", "/console/**")
                    // 설정한 리퀘스트 패턴을 누구나 접근할 수 있도록 허용
                    .permitAll()
                // 설정한 요청 이외의 리퀘스트 요청을 표현
                .anyRequest()
                    // 해당 요청은 인증된 사용자만 할 수 있음
                    .authenticated()
            .and()
                // 응답에 해당하는 header를 설정함 설정하지 않으면 디폴트 값으로 설정됨
                .headers()
                    // XFrameOptionsHeaderWriter의 최적화 설정을 허용하지 않음
                    .frameOptions().disable()
            .and()
                .exceptionHandling()
                // 인증의 진입지점임 인증되지 않은 사용자가 허용되지 않은 경로로 리퀘스트를 요청할 경우 '/login'으로 이동됨
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
            .and()
                // 로그인에 성공하면 설정된 경로로 포워딩 됨
                .formLogin()
                .successForwardUrl("/board/list")
            .and()
                // 로그아웃에 대한 설정을 할 수 있음
                .logout()
                // 로그아웃이 수행될 URL(logoutUrl)
                .logoutUrl("/logout")
                // 로그아웃이 성공했을 때 포워딩 될 URL(logoutSuccessUrl)
                .logoutSuccessUrl("/")
                // 로그아웃을 성공했을 때 삭제될 쿠키값(deleteCookies)
                .deleteCookies("JSESSIONID")
                // 설정된 세션의 무효화(invalidateHttpSession)를 수행하게끔 설정
                .invalidateHttpSession(true)
            .and()
                // 첫 번째 인자보다 먼저 시작될 필터를 등록
                .addFilterBefore(filter, CsrfFilter.class)
                .addFilterBefore(oauth2Filter(), BasicAuthenticationFilter.class)
                // 문자 인코딩 필터(filter)보다 CsrfFilter를 먼저 실행하도록 설정함
                .csrf().disable();
    }

    /**
     * OAuth2 클라이언트용 시큐리티 필터인 OAuth2ClientContextFilter를 불러와
     * 올바른 순서로 필터가 동작하도록 설정함
     * 낮은 순서로 필터 등록
     * @param filter
     * @return
     */
    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    /**
     * 각 소셜 미디어 타입을 받아 필터 설정을 할 수 있음
     * @return
     */
    private Filter oauth2Filter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(oauth2Filter(facebook(), "/login/facebook", FACEBOOK));
        filters.add(oauth2Filter(facebook(), "/login/google", GOOGLE));
        filters.add(oauth2Filter(facebook(), "/login/kakao", KAKAO));
        return filter;
    }

    /**
     * 각 소셜 미디어 필터를 리스트 형식으로 한꺼번에 설정하여 반환함
     * @param client
     * @param path
     * @param socialType
     * @return
     */
    private Filter oauth2Filter(ClientResources client, String path, SocialType socialType) {
        // 인증이 수행될 경로를 넣어 OAuth2 클라이언트용 인증 처리 필터를 생성함
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        // 권한 서버와의 통신을 위해 OAuth2RestTemplate을 생성함
        // 이를 생성하기 위해선 client 프로퍼티 정보와 OAuth2ClientContext가 필요함
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext);
        filter.setRestTemplate(template);
        // User의 권한을 최적화해서 생성하고자 UserInfoTokenServices를 상속받은 UserTokenService를 생성함
        // OAuth2 AccessToken 검증을 위해 생성한 UserTokenService를 필터의 토큰 서비스로 등록함
        // SocialType을 OAuth2AuthoritiesExtractor 클래스에 넘겨주면 권한 네이밍을 알아서 일괄적으로 처리하도록 설정이 완료됨
        filter.setTokenServices(new UserTokenService(client, socialType));
        // 인증이 성공적으로 이루어지면 필터에 리다이렉트 될 URL을 설정함
        filter.setAuthenticationSuccessHandler((request, response, authentication) ->
                response.sendRedirect("/" + socialType.getValue() + "/complete"));
        // 인증이 실패하면 필터에 리다이렉트 될 URL을 설정함
        filter.setAuthenticationFailureHandler((request, response, exception) ->
                response.sendRedirect("/error"));
        return filter;
    }

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
