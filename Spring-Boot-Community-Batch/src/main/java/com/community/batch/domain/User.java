package com.community.batch.domain;

import com.community.batch.domain.enums.Grade;
import com.community.batch.domain.enums.SocialType;
import com.community.batch.domain.enums.UserStatus;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by freejava1191@gmail.com on 2019-09-13
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@Getter
// 객체의 동등성을 비교하는 Equals()와 HashCode() 메서드를 구현하는 어노테이션
// 비교할 필드 값으로 유니크한 값인 idx와 email을 설정
@EqualsAndHashCode(of = {"idx", "email"})
@NoArgsConstructor
@Entity
@Table
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column
    private String name;

    @Column
    private String password;

    @Column
    private String email;

    @Column
    private String principle;

    @Column
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    // UserStatus Enum 필드
    @Column
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    // 회원 등급을 나타내는 Gradle Enum 필드
    @Column
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Column
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime updatedDate;

    @Builder
    public User(String name, String password, String email, String principle, SocialType socialType, UserStatus status, Grade grade, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.principle = principle;
        this.socialType = socialType;
        this.status = status;
        this.grade = grade;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    /**
     * User가 휴면회원으로 판정된 경우 status 필드값을 휴면으로 전환하는 메서드
     * @return
     */
    public User setInactive() {
        status = UserStatus.INACTIVE;
        return this;
    }
}
