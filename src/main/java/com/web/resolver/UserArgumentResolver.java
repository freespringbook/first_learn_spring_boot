package com.web.resolver;

import com.web.annotation.SocialUser;
import com.web.domain.User;
import com.web.domain.enums.SocialType;
import com.web.repository.UserRepository;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.web.domain.enums.SocialType.*;

/**
 * Created by freejava1191@gmail.com on 2019-09-01
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 *
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private UserRepository userRepository;

    public UserArgumentResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @SocialUser 어노테이션이 있고 타입이 User인 파라미터만 true를 반환
     * 처음 한 번 체크된 부분은 캐시되어 이후의 동일한 호출 시에는 체크되지 않고 캐시된 결괏값을 바로 반환
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(SocialUser.class) != null && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
        User user = (User) session.getAttribute("user");
        return getUser(user, session);

    }

    /**
     * 인증된 User 객체를 만드는 메인 메서드
     * @param user
     * @param session
     * @return
     */
    private User getUser(User user, HttpSession session) {
        if(user == null) {
            try {
                // 2.0 버전에서는 기존의 OAuth2Authentication이 아닌 엑세스 토큰까지 제공한다는 의미에서 OAuth2AuthenticationToken을 지원함
                // SecurityContextHolder에서 OAuth2AuthenticationToken을 가져옴
                OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
                // 개인정보를 getAttributes() 메서드를 사용해 Map 타입으로 불러옴
                // 기존에는 Map<String, String> 이었다면 이제는 Map<String, Object>를 제공하게끔 변경되었으므로 Map 객체를 사용하는 부분을 모두
                // Map<String, Object> 로 변경함
                Map<String, Object> map = authentication.getPrincipal().getAttributes();
                // 예전에는 getAuthorities() 메서드로 권한을 불러와서 인증된 소셜 미디어가 어디인지 알았다면 이제는
                // getAuthorizedClientRegistrationId() 메서드로 파악할 수 있음
                User convertUser = convertUser(authentication.getAuthorizedClientRegistrationId(), map);
                // 이메일을 사용해 이미 DB에 저장된 사용자라면 바로 User 객체를 반환 그렇지 않으면 저장
                user = userRepository.findByEmail(convertUser.getEmail());
                if (user == null) {
                    user = userRepository.save(convertUser);
                }
                selfRoleIfNotSame(user, authentication, map);
                session.setAttribute("user", user);
            } catch (ClassCastException e) {
                return user;
            }
        }
        return user;
    }

    /**
     * 사용자의 인증된 소셜 미디어 타입에 따라 빌더를 사용하여 User 객체를 만들어 주는 가교 역할을 함
     * 카카오의 경우에는 별도의 메서드 사용함
     * @param authority
     * @param map
     * @return
     */
    private User convertUser(String authority, Map<String, Object> map) {
        if(FACEBOOK.isEquals(authority)) return getModernUser(FACEBOOK, map);
        else if(GOOGLE.isEquals(authority)) return getModernUser(GOOGLE, map);
        else if(KAKAO.isEquals(authority)) return getKaKaoUser(map);
        return null;

    }

    /**
     * 메서드는 페이스북이나 구글과 같이 공통되는 명명규칙을 가진 그룹을 User 객체로 매핑
     * @param socialType
     * @param map
     * @return
     */
    private User getModernUser(SocialType socialType, Map<String, Object> map) {
        return User.builder()
                .name(String.valueOf(map.get("name")))
                .email(String.valueOf(map.get("email")))
                .principal(String.valueOf(map.get("id")))
                .socialType(socialType)
                .createdDate(LocalDateTime.now())
                .build();
    }

    /**
     * (키의 네이밍값이 타 소셜 미디어와 다른) 카카오 회원을 위한 메서드
     * getModernUser() 메서드와 동일하게 User 객체로 매핑
     * @param map
     * @return
     */
    private User getKaKaoUser(Map<String, Object> map) {
        Map<String, String> propertyMap = (HashMap<String, String>) map.get("properties");
        return User.builder()
                .name(propertyMap.get("nickname"))
                .email(String.valueOf(map.get("kaccount_email")))
                .principal(String.valueOf(map.get("id")))
                .socialType(KAKAO)
                .createdDate(LocalDateTime.now())
                .build();
    }

    /**
     * 인증된 authentication이 권한을 갖고 있는지 체크하는 용도로 쓰임
     * 만약 저장된 User 권한이 없으면 SecurityContextHolder를 사용하여 해당 소셜 미디어 타입으로 권한을 저장
     * @param user
     * @param authentication
     * @param map
     */
    private void selfRoleIfNotSame(User user, OAuth2AuthenticationToken authentication, Map<String, Object> map) {
        if(!authentication.getAuthorities().contains(new SimpleGrantedAuthority(user.getSocialType().getRoleType()))) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(map, "N/A", AuthorityUtils.createAuthorityList(user.getSocialType().getRoleType())));
        }
    }

}
