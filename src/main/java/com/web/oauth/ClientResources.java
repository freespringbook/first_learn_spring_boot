package com.web.oauth;

import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

import javax.annotation.Resource;

/**
 * Created by freejava1191@gmail.com on 2019-08-31
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 *
 * 소셜 미디어 리소스 프로퍼티를 객체로 매핑해주는 ClientResource rorcp
 */
public class ClientResources {

    /**
     * @NestedConfigurationProperty는 해당 필드가 단일값이 아닌 중복으로 바인딩된다고 표시하는 어노테이션임
     * 소셜 미디어 세 곳의 프로퍼티를 각각 바인딩하므로 @NestedConfigurationProperty 어노테이션을 붙임
     */
    @NestedConfigurationProperty
    //@AuthorizationCodeResourceDetails 객체는 설정한 각 소셜의 프로퍼티값 중 'client'를 기준으로 하위의 키/값을 매핑해주는 대상 객체임
    private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

    /**
     * ResourceServerProperties 객체는 원래 OAuth2 리소스값을 매핑하는 데 사용하지만 예제에서는
     * 회원 정보를 얻는 userInfoUri 값을 받는 데 사용함
     */
    @NestedConfigurationProperty
    private ResourceServerProperties resource = new ResourceServerProperties();

    public AuthorizationCodeResourceDetails getClient() {
        return client;
    }

    public ResourceServerProperties getResource() {
        return resource;
    }
}
