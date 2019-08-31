package com.web.service;

import com.web.domain.enums.SocialType;
import com.web.oauth.ClientResources;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import java.util.List;
import java.util.Map;

/**
 * Created by freejava1191@gmail.com on 2019-08-31
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 *
 * User 정보를 비동기 통신으로 가져오는 Rest Service인 UserInfoTokenServices를 커스터마이징
 * OAuth2에서 제공하는 소셜 미디어 원격 서버와 통신하여 User 정보를 가져오는 UserInfoTokenServices 상속 받는 클래스
 */
public class UserTokenService extends UserInfoTokenServices {

    public UserTokenService(ClientResources resources, SocialType socialType) {
        // 각각의 소셜 미디어 정보를 주입할 수 있도록 함
        super(resources.getResource().getUserInfoUri(), resources.getClient().getClientId());
        // 권한 등록
        setAuthoritiesExtractor(new OAuth2AuthoritiesExtractor(socialType));
    }

    /**
     * AuthoritiesExtractor 인터페이스를 구현한 내부 클래스 생성
     */
    private static class OAuth2AuthoritiesExtractor implements AuthoritiesExtractor {

        private  String socialType;

        // 권한 생성 방식을 ROLE_FACEBOOK 형식으로 하기위해 getRoleType() 메서드 사용
        public OAuth2AuthoritiesExtractor(SocialType socialType) {
            this.socialType = socialType.getRoleType();
        }

        /**
         * 권한을 리스트 형식으로 생성하여 반환하도록 함
         * @param map
         * @return
         */
        @Override
        public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
            return AuthorityUtils.createAuthorityList(this.socialType);
        }
    }
}
