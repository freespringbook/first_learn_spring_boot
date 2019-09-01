package com.web.config;

import com.web.oauth.CustomOAuth2Provider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
 * 자동 설정 그대로 사용할 수도 있지만 요청, 권한, 기타 설정에 대해서는 필수적으로 최적화한 설정이 들어가야 함
 * 최적화 설정을 위해 WebSecurityConfigurerAdapter를 상속받고 configure(HttpSecurity http) 메서드를 오버라이드하여 원하는 형식의 시큐리트 설정을 함
 */
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
                // OAuth2 API에서 인증 요청되는 URI가 '/oauth2/**'를 갖기 때문에 모든 사용자에게 권한을 허용하도록 설정함
                .antMatchers("/", "/oauth2/**", "/login/**", "/css/**", "/images/**", "/js/**", "/console/**")
                    // 설정한 리퀘스트 패턴을 누구나 접근할 수 있도록 허용
                    .permitAll()
                // 소셜 미디어용 경로 지정
                .antMatchers("/facebook")
                    // 메서드의 파라미터로 원하는 권한을 전달하여 해당 권한을 지닌 사용자만 경로를 사용할 수 있도록 통제
                    .hasAuthority(FACEBOOK.getRoleType())
                .antMatchers("/google").hasAuthority(GOOGLE.getRoleType())
                .antMatchers("/kakao").hasAuthority(KAKAO.getRoleType())
                // 설정한 요청 이외의 리퀘스트 요청을 표현
                .anyRequest()
                    // 해당 요청은 인증된 사용자만 할 수 있음
                    .authenticated()
            .and()
                // 단지 시큐리티 설정에서 oauth2Login()만 추가로 설정하면 기본적으로 제공되는 구글과 페이스북에 대한 OAuth2 인증방식이 적용됨
                .oauth2Login()
                .defaultSuccessUrl("/loginSuccess")
                .failureUrl("/loginFailure")
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
                // 문자 인코딩 필터(filter)보다 CsrfFilter를 먼저 실행하도록 설정함
                .csrf().disable();
    }

    /**
     * OAuth2ClientProperties와 설정했던 카카오 클라이언트 ID를 불러온다
     * @Configuration으로 등록되어 있는 클래스에서 @Bean으로 등록된 메서드의 파라미터로 지정된 객체들은 오토와이어링(autowiring) 할 수 있음
     * OAuth2ClientProperties에는 구글과 페이스북의 정보가 들어 있고 카카오는 따로 등록했기 때문에 @Value 어노테이션을 상요하여 수동으로 불러옴
     * @param oAuth2ClientProperties
     * @param kakaoClientId
     * @return
     */
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties oAuth2ClientProperties, @Value("${custom.oauth2.kakao.client-id}") String kakaoClientId) {
        List<ClientRegistration> registrations = oAuth2ClientProperties.getRegistration().keySet().stream()
                // 구글과 페이스북의 인증 정보를 빌드시켜줌
                .map(client -> getRegistration(oAuth2ClientProperties, client))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // registrations 리스트에 카카오 인증 정보를 추가함
        // 실제 요청 시 사용하는 정보는 클라이언트 ID뿐이지만 clientSecret()과 jwtSetUri()가 null이면 안되므로 임시값을 넣음
        registrations.add(CustomOAuth2Provider.KAKAO.getBuilder("kakao")
                .clientId(kakaoClientId)
                .clientSecret("test") //필요없는 값인데 null이면 실행이 안되도록 설정되어 있음
                .jwkSetUri("test") //필요없는 값인데 null이면 실행이 안되도록 설정되어 있음
                .build());

        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration getRegistration(OAuth2ClientProperties clientProperties, String client) {
        if ("google".equals(client)) {
            OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("google");
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    .scope("email", "profile")
                    .build();
        }
        if ("facebook".equals(client)) {
            OAuth2ClientProperties.Registration registration = clientProperties.getRegistration().get("facebook");
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    // 페이스북의 그래프 API의 경우 scope()로는 필요한 필드를 반환해주지 않기 때문에
                    // 직접 id, name, email, link 등을 파라미터로 넣어 요청하도록 설정함
                    .userInfoUri("https://graph.facebook.com/me?fields=id,name,email,link")
                    .scope("email")
                    .build();
        }
        return null;
    }

}
