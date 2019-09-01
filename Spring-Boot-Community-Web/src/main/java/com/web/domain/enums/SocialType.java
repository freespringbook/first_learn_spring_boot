package com.web.domain.enums;

import lombok.AllArgsConstructor;

/**
 * Created by freejava1191@gmail.com on 2019-08-31
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 *
 * 소셜 미디어의 타입정보를 나타내는 enum 객체
 * enum을 권한 생성 로직을 공통 코드로 처리하여 중복 코드를 줄임
 */
public enum  SocialType {
    FACEBOOK("facebook"),
    GOOGLE("google"),
    KAKAO("kakao");

    private final String ROLE_PREFIX = "ROLE_";
    private String name;

    SocialType(String name) {
        this.name = name;
    }

    /**
     * ROLE_* 형식으로 소셜 미디어의 권한명 생성
     * @return
     */
    public String getRoleType() {
        return ROLE_PREFIX + name.toUpperCase();
    }

    public String getValue() {
        return name;
    }

    public boolean isEquals(String authority) {
        return this.getRoleType().equals(authority);
    }
}
