package com.web.controller;

import com.web.domain.Board;
import com.web.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by freejava1191@gmail.com on 2019-08-28
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@Controller
@RequestMapping("/board") //API URI 경로를 '/board'로 정의
public class BoardController {

    // 의존성 주입
    @Autowired
    BoardService boardService;

    /**
     * @RequestParam 어노테이션을 사용하여 idx 파라미터를 필수로 받음
     * 만약 바인딩할 값이 없으면 기본값 '0'으로 설정됨
     * findBoardByIdx(idx)로 조회 시 idx 값을 '0'으로 조회하면 board 값은 null 값으로 반환됨
     * @param idx
     * @param model
     * @return
     */
    @GetMapping({"", "/"}) //매핑 경로를 중괄호를 사용하여 여러 개 받을 수 있음
    public String board(@RequestParam(value = "idx", defaultValue = "0") Long idx, Model model) {
        model.addAttribute("board", boardService.findBoardByIdx(idx));
        return "/board/form";
    }

    /**
     * @PageableDefault 어노테이션의 파라미터인 size, sort, direction 등을 사용하여 페이징 처리에 대한 규약을 정의할 수 있음
     * @param pageable
     * @param model
     * @return
     */
    @GetMapping("/list")
    public String list(@PageableDefault Pageable pageable, Model model) {
        model.addAttribute("boardList", boardService.findBoardList(pageable));
        return "/board/list"; //src/resources/templates를 기준으로 데이터를 바인딩할 타깃의 뷰 경로를 지정
    }
}
