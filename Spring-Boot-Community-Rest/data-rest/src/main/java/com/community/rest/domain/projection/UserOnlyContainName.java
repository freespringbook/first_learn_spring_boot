package com.community.rest.domain.projection;

import com.community.rest.domain.User;
import org.springframework.data.rest.core.config.Projection;

/**
 * Created by freejava1191@gmail.com on 2019-09-07
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
// name - 해당 프로젝션을 사용하기 위한 키값을 지정
// types - 해당 프로젝션이 어떤 도메인에 바인딩 될지 나타냄
@Projection(name = "getOnlyName", types = { User.class})
public interface UserOnlyContainName {
    String getName();
}
