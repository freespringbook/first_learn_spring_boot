package com.community.rest.domain;

import com.community.rest.domain.enums.SocialType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by freejava1191@gmail.com on 2019-08-27
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@Getter
@NoArgsConstructor
@Entity
@Table
public class User {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column
    private String name;

    @Column
    @JsonIgnore
    private String password;

    @Column
    private String email;

    /* OAuth2 인증으로 제공받는 키값 */
    @Column
    private String principal;

    /* 어떤 소셜 미디어로 인증받았는지 여부 */
    @Column
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime updatedDate;

    @Builder
    public User(String name, String password, String email, String principal, SocialType socialType, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.principal = principal;
        this.socialType = socialType;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
}
