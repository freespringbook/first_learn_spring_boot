package com.web.controller;

import com.web.annotation.SocialUser;
import com.web.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by freejava1191@gmail.com on 2019-09-01
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@Controller
public class LoginController {

    @GetMapping({"","/login","/logout"})
    public String login() {
        return "login";
    }

    /**
     * 인증된 User 정보를 세션에 저장해주는 기능 생성
     * 인증이 성공적으로 처리된 이후에 리다이렉트되는 경로
     * 허용하는 요청의 URL 매핑을 /facebook/complete,/google/complete,/kakao/complete로 제한함
     * @param user
     * @return
     */
    // @GetMapping(value = "/{facebook|google|kakao}/complete")
    @GetMapping(value = "/loginSuccess") // 로그인 성공 URL 잠시 변경
    public String loginComplete(@SocialUser User user) { // 간단한 방법으로 인증된 User 객체를 가져옴
        return "redirect:/board/list";
    }
}
