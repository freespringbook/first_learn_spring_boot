package com.demo.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by freejava1191@gmail.com on 2019-08-20
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@Data
@Component
@ConfigurationProperties("fruit-data")
public class FruitProperties {
    private String colorName;
}
