package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id") // 무한루프 방지
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    // 이메일과 닉네임은 유일해야 함
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    public String password;

    // 이메일 인증 여부
    private boolean emailVerified;

    // 이메일을 인증할 때, 사용 할 토큰 값을 저장하는 필드를 선언
    private String emailCheckToken;

    // 가입 날짜
    private LocalDateTime joinedAt;

    // 짧은 자기소개
    private String bio;

    // 자신의 블로그, SNS url
    private String url;

    // 직업
    private String occupation;

    // 살고 있는 지역
    private String location;

    // 프로필 이미지
    @Lob @Basic(fetch = EAGER)
    private String profileImage;

    // 스터디 생성 여부를 이메일 또는 웹으로 전달 받을 것인가?
    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb = true;

    // 스터디가 운영하는 모임에 가입 신청 결과를 이메일 또는 웹으로 전달 받을 것인가?
    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb = true;

    // 스터디 수정 여부를 이메일 또는 웹으로 전달 받을 것인가?
    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb = true;

    private LocalDateTime emailCheckTokenGeneratedAt;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true; // 이메일 인증 여부 지정
        this.joinedAt = LocalDateTime.now(); // 가입 날짜 지정
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }

}
