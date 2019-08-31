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
     * @param session
     * @return
     */
    @GetMapping(value = "/{facebook|google|kakao}/complete")
    public String loginComplete(HttpSession session) {
        // SecurityContextHolder에서 인증된 정보를 OAuth2Authentication 형태로 방아옴
        // OAuth2Authentication은 기본적인 인증에 대한 정보뿐만 아니라 OAuth2 인증과 관련된 정보도 함께 제공함
        OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        // 리소스 서버에서 받아온 개인정보를 getDetails()를 사용해 Map 타입으로 받을 수 있음
        Map<String, String> map = (HashMap<String, String>) authentication.getUserAuthentication().getDetails();
        // 세션에 빌더를 사용하여 인증된 User 정보를 User 객체로 변환하여 저장함
        session.setAttribute("user", User.builder()
            .name(map.get("name"))
            .email(map.get("email"))
            .principal(map.get("id"))
            .socialType(SocialType.FACEBOOK)
            .createdDate(LocalDateTime.now())
            .build()
        );
        return "redirect:/board/list";
    }
}
