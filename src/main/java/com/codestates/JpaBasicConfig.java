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
    private EntityTransaction tx;


    @Bean
    public CommandLineRunner testJpaBasicRunner(EntityManagerFactory emFactory) { // EntityManagerFactory클래스를 이용해 DI 받기
        this.em = emFactory.createEntityManager();
        this.tx = em.getTransaction();

        return args -> {
            example02(); // Member 엔티티 클래스의 객체를 JPA의 영속성 컨텍스트에 저장
        };
    }

    private void example01() {
        Member member = new Member("hgd@gmail.com");

        em.persist(member);

        Member resultMember = em.find(Member.class, 1L);
        System.out.println("Id: " + resultMember.getMemberId() + ", email: " +
                resultMember.getEmail());
    }

    private void example02() {
        tx.begin(); // Transaction을 시작하기 위해서 tx.begin() 메서드를 먼저 호출해야 함
        Member member = new Member("hgd@gmail.com");

        em.persist(member); // member 객체를 영속성 컨텍스트에 저장

        tx.commit(); // tx.commit()을 호출하는 시점에 영속성 컨텍스트에 저장되어 있는 member 객체를 데이터베이스의 테이블에 저장

        Member resultMember1 = em.find(Member.class, 1L);

        System.out.println("Id: " + resultMember1.getMemberId() + ", email: " + resultMember1.getEmail());

        Member resultMember2 = em.find(Member.class, 2L);

        System.out.println(resultMember2 == null);

    }
}
