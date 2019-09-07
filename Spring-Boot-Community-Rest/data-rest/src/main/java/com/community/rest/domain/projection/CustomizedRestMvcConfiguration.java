package com.community.rest.domain.projection;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

/**
 * Created by freejava1191@gmail.com on 2019-09-07
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 *
 * RepositoryRestConfiguration 클래스를 사용하여
 * 단일 참조 시에도 적용이 되도록 프로젝션을 수동으로 등록하는 설
 */
@Configuration
public class CustomizedRestMvcConfiguration implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.getProjectionConfiguration().addProjection(UserOnlyContainName.class);
    }
}
