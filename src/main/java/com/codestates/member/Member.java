package com.codestates.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Member {
    @Id
    @GeneratedValue // 식별자 지정할 때 사용
    private Long memberId;

    private String email;

    public Member(String email) {
        this.email = email;
    }
}