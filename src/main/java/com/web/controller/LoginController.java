package com.web.controller;

import com.web.domain.User;
import com.web.domain.enums.SocialType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by freejava1191@gmail.com on 2019-09-01
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@Controller
public class LoginController {

    @GetMapping("/login")
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
    @GetMapping(value = "/{facebook|google|kakao}/complete")
    public String loginComplete(@SocialUser User user) { // 간단한 방법으로 인증된 User 객체를 가져옴
        return "redirect:/board/list";
    }
}
