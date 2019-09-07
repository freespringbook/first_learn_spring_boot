package com.community.rest.event;

import com.community.rest.domain.Board;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

/**
 * Created by freejava1191@gmail.com on 2019-09-07
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 *
 * 게시글 생성 시 생성 날짜, 수정 시 수정 날짜를 서버에서 생성하도록 설정하는 이벤트 핸들러
 */
@RepositoryEventHandler
public class BoardEventHandler {

    /**
     * 게시글의 생성 날짜를 현재 시간으로 할당
     * @param board
     */
    @HandleBeforeCreate
    public void beforeCreateBoard(Board board) {
        board.setCreatedDateNow();
    }

    /**
     * 게시글의 수정 날짜를 현재 시간으로 할당
     * @param board
     */
    @HandleBeforeSave
    public void beforeSaveBoard(Board board) {
        board.setUpdatedDateNow();
    }
}
