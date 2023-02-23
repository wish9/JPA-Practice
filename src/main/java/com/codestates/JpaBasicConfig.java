package com.codestates;

import com.codestates.member.Member;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

@Configuration
public class JpaBasicConfig {
    private EntityManager em;


    @Bean
    public CommandLineRunner testJpaBasicRunner(EntityManagerFactory emFactory) { // EntityManagerFactory클래스를 이용해 DI 받기
        this.em = emFactory.createEntityManager();

        return args -> {
            example01(); // Member 엔티티 클래스의 객체를 JPA의 영속성 컨텍스트에 저장
        };
    }

    private void example01() {
        Member member = new Member("hgd@gmail.com");

        em.persist(member);

        Member resultMember = em.find(Member.class, 1L);
        System.out.println("Id: " + resultMember.getMemberId() + ", email: " +
                resultMember.getEmail());
    }
}
