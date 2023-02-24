package com.codestates.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, updatable = false, unique = true) // 필드와 컬럼을 매핑해주는 애너테이션
    private String email;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 13, nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();   // 시간, 날짜 필드
    //LocalDateTime 타입은 컬럼의 TIMESTAMP 타입과 매핑된다.

    @Column(nullable = false, name = "LAST_MODIFIED_AT") // 필드명과 다른 이름으로 컬럼 생성
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @Transient // 테이블 컬럼과 매핑하지 않겠다는 의미
    private String age;

    public Member(String email) {
        this.email = email;
    }

    public Member(String email, String name, String phone) {
        this.email = email;
        this.name = name;
        this.phone = phone;
    }
}