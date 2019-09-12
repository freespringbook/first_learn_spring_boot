package com.community.batch.domain.enums;

import lombok.AllArgsConstructor;

/**
 * Created by freejava1191@gmail.com on 2019-09-13
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@AllArgsConstructor
public enum SocialType {
    FACEBOOK("facebook"),
    GOOGLE("google"),
    KAKAO("kakao");

    private final static String ROLE_PREFIX = "ROLE_";
    private String name;

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
