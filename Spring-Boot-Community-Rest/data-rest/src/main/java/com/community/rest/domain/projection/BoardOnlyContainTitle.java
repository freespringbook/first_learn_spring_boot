package com.community.rest.domain.projection;

import com.community.rest.domain.Board;
import org.springframework.data.rest.core.config.Projection;

/**
 * Created by freejava1191@gmail.com on 2019-09-07
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 *
 * Board의 제목만 표시하는 프로젝션 생성
 */
@Projection(name = "getOnlyTitle", types = { Board.class })
public interface BoardOnlyContainTitle {
    String getTitle();
}