[JPA 블로그 포스팅 주소](https://velog.io/@wish17/%EC%BD%94%EB%93%9C%EC%8A%A4%ED%85%8C%EC%9D%B4%EC%B8%A0-%EB%B0%B1%EC%97%94%EB%93%9C-%EB%B6%80%ED%8A%B8%EC%BA%A0%ED%94%84-49%EC%9D%BC%EC%B0%A8-Spring-MVC-JPA-%EA%B8%B0%EB%B0%98-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%95%A1%EC%84%B8%EC%8A%A4-%EA%B3%84%EC%B8%B5)

# [Spring MVC] JPA 기반 데이터 액세스 계층

## JPA(Java Persistence API)

### 개념정리

> JPA(Java Persistence API)
- Java 진영에서 사용하는 ORM(Object-Relational Mapping) 기술의 표준 사양(또는 명세, Specification)

> [영속성 컨텍스트(Persistence Context)](https://gmlwjd9405.github.io/2019/08/06/persistence-context.html)
- Entity를 영구 저장하는 환경
[![](https://velog.velcdn.com/images/wish17/post/78b9678d-9c29-4d7b-ac64-3a870a82f45f/image.png)](https://velog.io/@neptunes032/JPA-%EC%98%81%EC%86%8D%EC%84%B1-%EC%BB%A8%ED%85%8D%EC%8A%A4%ED%8A%B8%EB%9E%80)
[![](https://velog.velcdn.com/images/wish17/post/99f33910-43fb-49b3-8981-955566a1970e/image.png)](https://siyoon210.tistory.com/138)

### 사용기능

``EntityManager`` 클래스
- 엔티티를 관리하는 다양한 기능(메서드)를 갖고 있는 클래스.
- JPA의 영속성 컨텍스트는 ``EntityManager`` 클래스에 의해서 관리 됨.
- 여러 스레드가 동시에 접근하게 되면 동시성 문제(두 스레드가 값을 동시에 바꾸다 오류가 나는 등의 문제)가 발생하지 않도록 하나의 ``EntityManager``클래스로 엔티티를 관리하면 안된다.
(즉, 상황에 따라 ``EntityManagerFactory``를 이용해 계속 만들어줘야 함)
- ``persist()`` 메서드 = 영속성 컨텍스트에 엔티티 객체 정보를 저장하는 메서드(DB에 저장하는 것 x)
    - 1차 캐쉬에 엔티티 객체가 저장되고, 쿼리문(쓰기 지연 SQL) 저장소에 INSERT 쿼리 형태로 등록 된다.
    - ![](https://velog.velcdn.com/images/wish17/post/8f0393f9-8305-425a-bfe2-590429bfeecd/image.png)
- ``find(조회 할 엔티티 클래스의 타입, 조회 할 엔티티 클래스의 식별자 값)`` 메서드 = 영속성 컨텍스트에 저장된 객체를 가져오는 메서드
    - 만약 1차 캐시에 찾으려는 객체가 없으면 DB 테이블에 SELECT 쿼리를 전송해서 조회한다.
    - ![](https://velog.velcdn.com/images/wish17/post/cf897d4f-6612-4af3-8ce4-014e3c365d7f/image.png)

- ``flush()`` 메서드 = 영속성 컨텍스트의 변경 사항을 테이블에 반영할 수 있다.
    - ``commit()``을 호출하면 내부적으로 ``flush()``가 호출된다.


``EntityTransaction`` 클래스
- ``commit()`` 메서드 = Persistence Context에 있는 객체를 DB에 저장하는 메서드
    -  DB저장 후에도 Persistence Context에 있는 1차 캐시에는 객체 정보가 남음
    - 쓰기 지연 SQL 저장소(쿼리문 저장소)에는 객체 정보가 남지 않음
    - ``commit()``을 호출하면 내부적으로 ``flush()``가 호출된다.

[``EntityManagerFactory`` 클래스](https://perfectacle.github.io/2018/01/14/jpa-entity-manager-factory/)
- 엔티티를 관리하는 ``EntityManager``를 찍어내 만드는 클래스
- ``createEntityManager()``메서드 = ``EntityManager`` 객체 생성하는 메서드

***

### 1차캐시 + DB 테이블에 저장하는 방법

[연습 풀 코드 GitHub 주소](https://github.com/wish9/JPA-Practice/commit/7ae5466068169ce64472be6db2a6df66463410d4)

``Transaction`` 클래스을 이용한다.

1. Transaction을 시작하기 위해서 ``begin()`` 메서드를 먼저 호출해야 한다.
    
2. ``persist()`` 메서드를 이용해 엔티티 객체를 Persistence Context에 저장
    
3.  ``commit()`` 메서드를 사용해 Persistence Context에 저장되어 있는 객체를 DB 테이블에 저장
	- DB저장 후 1차캐시에는 객체가 남아있지만 쿼리 저장소에서는 삭제 됨
    
***

### DB에 저장한 객체 수정(UPDATE)

[수정연습 풀 코드 GitHub 주소](https://github.com/wish9/JPA-Practice/commit/0b7bc903a2cffcd2927f762c621acb4ce5bb4766)

위와 같이 ``begin()`` -> ``persist()`` -> ``commit()``순으로 DB 테이블에 저장해 둔 정보를 수정하는 방법.

1. ``find()``메서드를 이용해 수정하려는 데이터 조회
2. setter 메서드로 정보 변경
3. 다시 ``commit()``

***

### DB에 저장한 객체 삭제(DELETE)

[삭제연습 풀 코드 GitHub 주소](https://github.com/wish9/JPA-Practice/commit/ff9dda40a53b6345fa5c7874805ec551a75098a4)

위와 같이 ``begin()`` -> ``persist()`` -> ``commit()``순으로 DB 테이블에 저장해 둔 정보를 삭제하는 방법.

1. ``find()``메서드를 사용해 삭제하려는 데이터 조회
2. ``remove()`` 메서드를 사용해 1차 캐시에 있는 엔티티를 제거를 요청
3. ``commit()`` 메서드를 사용해 1차 캐시에 있는 엔티티를 제거하고, 쓰기 지연 SQL 저장소에 등록된 DELETE 쿼리 실행

***

## JPA 엔티티(Entity) 매핑과 연관 관계 매핑


### 엔티티와 테이블 간의 매핑

``@Entity``
- JPA 관리 대상 엔티티가 되게 함
- ``@Entity(name = "entityName")``과 같이 이름 지정 가능
- 이름 기본값 = 클래스명
- ``@Id`` 애너테이션을 필수로 추가해야 함

``@Table(name = "tableName")``
- 엔티티와 매핑할 테이블을 지정
- 주로 테이블명을 클래스명과 다르게 지정하는데 사용 (옵션 o 필수 x)
- 이름 기본값 = 클래스명

***

### 기본키 매핑

기본키 직접 할당 전략
- ``@Id`` 애너테이션만 추가

기본키 자동 생성

- IDENTITY = 기본키 생성을 데이터베이스에 위임하는 방법
    - ``@Id`` +  ``@GeneratedValue(strategy = GenerationType.IDENTITY)``
    - **``commit``을 안해도 ``persist``만 해도 DB에 반영된다.**

- SEQUENCE = 데이터베이스에서 제공하는 시퀀스를 사용해서 기본키를 생성하는 방법
    - ``@Id`` +  ``@GeneratedValue(strategy = GenerationType.SEQUENCE)``
    - ``persist`` + ``commit``을 해야 DB에 반영 된다.

- TABLE = 별도의 키 생성 테이블을 사용하는 방법
    - 거의 사용X

- AUTO = JPA가 데이터베이스의 Dialect에 따라서 적절한 방법을 자동으로 선택해서 기본키 생성
    - ``@Id`` +  ``@GeneratedValue(strategy = GenerationType.AUTO)``

***

### 필드(멤버 변수 )와 컬럼 간의 매핑

[매핑연습 풀 코드 GitHub 주소](https://github.com/wish9/JPA-Practice/commit/fd03f356c16be9ea3fdfc5568710f9628d21ddbf)

``@Column(nullable = false, updatable = false, unique = true, length = 10) ``
- 필드와 컬럼을 매핑해주는 애너테이션
- nullable = null 값을 허용여부
    - 기본값 = true
- updatable = 컬럼 데이터 수정 가능여부
    - 기본값 = true
- unique = 고유값으로 설정할지에 대한 여부
    - 기본값 = false
- length = 컬럼에 저장할 수 있는 문자 길이를 지정
    - 디폴트값 = 255


>그냥 ``@Column``만 쓰면 기본값으로 설정되니 주의
``@Column``을 생략하면 nullable=false가 된다.

``@Transient``
- 테이블 컬럼과 매핑하지 않겠다는 의미
- DB에 저장되지 않고, 조회할 때 역시 매핑되지 않는다.
- 주로 임시 데이터를 메모리에서 사용하기위한 용도로 사용

``@Enumerated``
- enum 타입과 매핑할 때 사용하는 애너테이션
- 두가지 타입이 있음
    - ``@Enumerated(EnumType.ORDINAL)`` = enum의 순서를 나타내는 숫자를 테이블에 저장
    	- 기존에 정의되어 있는 enum 사이에 새로운 enum 하나가 추가하면  enum에 정의되어 있는 순서가 일치하지 않게 되는 문제가 발생
    - ``@Enumerated(EnumType.STRING)`` = enum의 이름을 테이블에 저장
    
***    

### 엔티티와 테이블 매핑 권장 사용 방법

- 클래스 이름 중복 등의 특별한 이유가 없다면 @Entity와 @Id 애너테이션만 추가하자
    - ``@Table`` 애너테이션으로 테이블명 지정 가능

- 기본키 생성은 DB에서 지원해주는 AUTO_INCREMENT 또는 SEQUENCE를
이용할 수 있도록  IDENTITY 또는 SEQUENCE 방식을 사용하자

- ``@Column`` 정보를 명시적으로 모두 지정하자. (유지보수 가시성 좋아짐)

- 엔티티 클래스 필드 타입이 Java의 원시타입일 경우, ``@Column`` 애너테이션을 생략하지 말고, 최소한 nullable=false 설정은 하자

- ``@Enumerated`` 애너테이션을 사용할 때 ``EnumType.STRING``으로 쓰자.

***

## 엔티티 간의 연관 관계 매핑

[풀코드 GitHub주소](https://github.com/codestates-seb/be-homework-jpa/pull/373/commits) (외부인 조회 불가)


### joincolum과 mappedby의 차이

``joincolum``

- joincolum을 사용한 엔티티가 관계의 주인인 것이다. 
- 단방향 연결을 위해 사용. (``mappedby``에 의한 추가관계가 이루어지면 양방향이 될 수 있음)
- 참조한 colum을 갖고있다.
- 연관관계의 주인인 entity에서 사용하는 것

``mappedby``

- 참조된 엔티티를 참조하기 위해 사용한다.
- 양방향 관계로 만드는데 사용
- 자기 자신인 entity 객체를 참조한 엔티티와의 연관관계를 맺기 위해 사용하는 것
- 참조한 객체의 colum을 갖고있지 않다.
- 연관관계의 주인이 아닌 entity에서 사용하는 것

연관관계의 주인이라 함은 참조한 객체의 colum을 갖고 있는지의 여부로 판단하면 된다.

### 테이블관점의 스키마, 객체관점의 스키마

#### 테이블 관점의 스키마

- ![](https://velog.velcdn.com/images/wish17/post/d24f5c0a-4595-4b26-8a2f-c1b794d67cd2/image.png)

#### 객체관점의 스키마

- ![](https://velog.velcdn.com/images/wish17/post/da7e5879-ffe9-4415-8fc3-f4332df45aaf/image.png)

연관관계는 객체참조로 이루어진다.
따라서 연관관계에 대한 객체 참조구조는 테이블 관점의 스키마에서 직관적으로 알기 어렵다.

 테이블 괌점의 스키마를 머릿속에 그리고 코드를 구현한다고 하면 각 변수들을 설정함과 동시에 연관관계에 대한 생각을 일일히 판단하며 코드를 구현해야하기 때문에 실수를 할 수 있는 확률이 올라간다고 생각한다.
 
따라서 객체관점으로 코딩할 구조를 정리해 그려보고 코드를 구현하는게 좋을 것 같다.
