package com.codestates;

import com.codestates.member.Member;
import com.codestates.order.Order;
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
            mappingManyToOneBiDirection();
        };
    }

    private void mappingManyToOneBiDirection() {
        tx.begin();
        Member member = new Member("hgd@gmail.com", "Hong Gil Dong",
                "010-1111-1111");
        Order order = new Order();

        member.addOrder(order);
        order.addMember(member);

        em.persist(member);
        em.persist(order);

        tx.commit();

        Member findMember = em.find(Member.class, 1L); // 1차 캐시에서 정보 조회

        // 이제 주문한 회원의 회원 정보를 통해 주문 정보를 가져올 수 있다.
        findMember
                .getOrders()
                .stream()
                .forEach(findOrder -> {
                    System.out.println("findOrder: " +
                            findOrder.getOrderId() + ", "
                            + findOrder.getOrderStatus());
                });
    }

    private void mappingManyToOneUniDirection() {
        tx.begin();
        Member member = new Member("hgd@gmail.com", "Hong Gil Dong",
                "010-1111-1111");

        em.persist(member); // 정보 저장

        Order order = new Order();
        order.addMember(member);     // 외래키 역할을 하는 객체 저장
        em.persist(order);

        tx.commit();

        Order findOrder = em.find(Order.class, 1L);

        System.out.println("findOrder: " + findOrder.getMember().getMemberId() +
                ", " + findOrder.getMember().getEmail());
    }

    private void example01() { // Member 엔티티 클래스의 객체를 JPA의 영속성 컨텍스트에 저장
        Member member = new Member("hgd@gmail.com");

        em.persist(member);

        Member resultMember = em.find(Member.class, 1L);
        System.out.println("Id: " + resultMember.getMemberId() + ", email: " +
                resultMember.getEmail());
    }

    private void example02() { // Member 엔티티 클래스의 객체를 JPA의 영속성 컨텍스트에 있는 1차캐쉬 + DB 테이블에 저장
        tx.begin(); // Transaction을 시작하기 위해서 tx.begin() 메서드를 먼저 호출해야 함
        Member member = new Member("hgd@gmail.com");

        em.persist(member); // member 객체를 영속성 컨텍스트에 저장

        tx.commit(); // tx.commit()을 호출하는 시점에 영속성 컨텍스트에 저장되어 있는 member 객체를 데이터베이스의 테이블에 저장

        Member resultMember1 = em.find(Member.class, 1L);

        System.out.println("Id: " + resultMember1.getMemberId() + ", email: " + resultMember1.getEmail());

        Member resultMember2 = em.find(Member.class, 2L);

        System.out.println(resultMember2 == null);

    }

    private void example03() { // example02()메서드와 동일한 방식
        // 쓰기 지연(쿼리문 저장소)을 통한 영속성 컨텍스트와 테이블에 엔티티 일괄 저장
        tx.begin();

        Member member1 = new Member("hgd1@gmail.com");
        Member member2 = new Member("hgd2@gmail.com");

        em.persist(member1);
        em.persist(member2);


        tx.commit();
    }

    private void example04() { // DB에 저장하고 수정하는 메서드
        tx.begin(); // Transaction을 시작하기 위해서 사용
        em.persist(new Member("hgd1@gmail.com"));    // 객체를 영속성 컨텍스트의 1차 캐시에 저장
        tx.commit();    // 영속성 컨텍스트의 쓰기 지연 SQL 저장소에 등록된 INSERT 쿼리를 실행 (DB 테이블에 저장)


        tx.begin();
        Member member1 = em.find(Member.class, 1L);  // 테이블에 저장된 객체를 영속성 컨텍스트의 1차 캐시에서 조회
        //member1.setEmail("hgd1@yahoo.co.kr");       //  setter 메서드로 정보를 변경
        tx.commit();   // 다시 DB 테이블에 저장
    }

    private void example05() {
        tx.begin();  // Transaction 시작
        em.persist(new Member("hgd1@gmail.com"));  // 객체를 영속성 컨텍스트(Persistence Context)에 저장
        tx.commit();    // DB 테이블에 저장

        tx.begin();
        Member member = em.find(Member.class, 1L);   // 삭제할 정보 조회
        em.remove(member);     // 1차 캐시에 있는 엔티티를 제거를 요청
        tx.commit();     // 1차 캐시에 있는 엔티티를 제거하고 DB에서도 제거 // 1차 캐시에 있는 엔티티를 제거하고, 쓰기 지연 SQL 저장소에 등록된 DELETE 쿼리가 실행

    }

    private void testEmailNotNull() {
        tx.begin();
        em.persist(new Member());
        tx.commit();
    }

    private void testEmailUpdatable() {
        tx.begin();
        em.persist(new Member("hgd@gmail.com"));
        Member member = em.find(Member.class, 1L);
        member.setEmail("hgd@yahoo.co.kr");
        tx.commit();
    }

    private void testEmailUnique() {
        tx.begin();
        em.persist(new Member("hgd@gmail.com"));
        em.persist(new Member("hgd@gmail.com"));
        tx.commit();
    }
}
