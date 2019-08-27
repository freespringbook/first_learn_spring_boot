package com.web.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by freejava1191@gmail.com on 2019-08-27
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@Getter
@AllArgsConstructor
public enum BoardType {
    notice("공지사항"),
    free("자유계시판");

    private String value;
}
