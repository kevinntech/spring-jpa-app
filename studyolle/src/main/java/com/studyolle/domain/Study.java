package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Study{

    @Id @GeneratedValue
    private Long id;

    // 스터디 관리자 - 관리자가 여러 명일 수 있다고 본다.
    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    // 회원
    @ManyToMany
    private Set<Account> members = new HashSet<>();

    // URL 경로
    @Column(unique = true)
    private String path;

    // 스터디 제목
    private String title;

    // 짧은 소개
    private String shortDescription;

    // 긴 소개 (전체 본문)
    // Study 정보를 조회할 때, Lob에 해당하는 것은 즉시 조회한다. (기본 값이므로 생략 가능)
    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;

    // 인원 모집 중인지 여부
    private boolean recruiting;

    // 공개 여부
    private boolean published;

    // 종료 여부
    private boolean closed;

    // 배너 사용 여부
    private boolean useBanner;

    // 연관관계 편의 메소드
    public void addManager(Account account) {
        this.managers.add(account);
    }
}