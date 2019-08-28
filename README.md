# 처음 배우는 스프링 부트2
## 4.3 커뮤니티 게시판 구현하기
##### 개발 순서
1. 프로젝트 의존성 구성
2. 스프링 부트 웹 스타터 살펴보기
3. 도메인 매핑하기
4. 도메인 테스트하기
5. CommandLineRunner를 사용하여 DB에 데이터 넣기
6. 게시글 리스트 기능 만들기
7. 타임리프 자바 8 날짜 포맷 라이브러리 추가하기
8. 페이징 처리하기
9. 작성 폼 만들기

### 1. 프로젝트 의존성 구성
필요한 build.gradle 구성

### 2. 스프링 부트 웹 스타터 살펴보기
- `spring-boot-starter`: 스프링 부트를 시작하는 기본적인 설정이 담겨 있는 스타터
- `spring-boot-starter-tomcat`: 내장 톰캣을 사용하기 위한 스타터
- `hibernate-validator`: 어노테이션 기반의 표준화된 제약 조건 및 유효성 검사 규칙을 표현하는 라이브러리
- `spring-boot-starter-json`: jackson 라이브러리를 지원해주는 스타터
  JSON 데이터형의 파싱, 데이터 바인딩 함수 등을 제공
- `spring-web`: HTTP Integration, Servlet filters, Spring HTTP invoker 및 HTTP 코어를 포함시킨 라이브러리
- `spring-webmvc`: request를 전달하는 MVC로 디자인된 DispatcherServlet 기반의 라이브러리

### 3. 도메인 매핑하기
```java
@GeneratedValue(strategy = GenerationType.IDENTITY)
```
기본 키가 자동으로 할당되도록 설정하는 어노테이션  
기본키 할당 전략을 선택할 수 있는데, 키 생성을 데이터베이스에 위임하는 IDENTITY 전략을 사용
> 스프링 부트 1.x는 기본 키 할당 전략이 IDENTITY 지만 2.x부터는 TABLE로 변경됨
> 따라서 명확히 IDENTITY 로 명시하여 사용하지 않으면 한 테이블에서만 시퀀스가 관리되는 현상이 발생하게 됨

```java
@Enumerated(EnumType.STRING)
```
Enum 타입 매핑용 어노테이션  
`@Enumerated` 어노테이션을 이용해 자바 enum형과 데이터베이스 데이터 변환을 지원함  
실제로 자바 enum 형이지만 데이터베이스의 String형으로 변환하여 저장하겠다고 선언한 것

```java
@OneToOne(fetch = FetchType.LAZY)
```
도메인 Board와 Board가 필드 값으로 갖고 있는 User 도메인을 1:1 관계로 설정하는 어노테이션  
실제로 DB에 저장될 때는 User 객체가 저장되는 것이 아니라 User의 PK인 user_idx 값이 저장됨  
fetch는 eager와 lazy 두 종류가 있는데 전자는 처음 Board 도메인을 조회할 때 즉시 관련 User 객체를 함께 조회한다는 뜻이고  
후자는 User 객체를 조회하는 시점이 아닌 객체가 실제로 사용될 때 조회한다는 뜻

### 4. 도메인 테스트하기
##### 컨텍스트
빈의 생성과 관계 설정 같은 제어를 담당하는 IOC 객체를 빈 팩토리라 부르며 이러한 빈 팩토리를 더 확장한 개념이 애플리케이션 컨텍스트이다

### 5. CommandLineRunner를 사용하여 DB에 데이터 넣기
**CommandLineRunner**는 애플리케이션 구동 후 특정 코드를 실행시키고 싶을 때 직접 구현하는 인터페이스  
애플리케이션 구동 시 테스트 데이터를 함께 생성하여 데모 프로젝트를 실행/테스트하고 싶을 때 편리함  
여러 **CommandLineRunner**를 구현하여 같은 애플리케이션 컨텍스트이 빈에 사용할 수 있음

### 6. 게시글 리스트 기능 만들기
타임리프를 사용하여 게시글 리스트 기능 만들기
> 서버 사이드 템플릿이란 미리 정의된 HTML에 데이터를 반영하여 뷰를 만드는 작업을 서버에서 진행하고 클라이언트에 전달하는 방식  
흔히 사용하는 JSP, 타임리프 등이 서버 사이드 템플릿 엔진이며 스프링 부트 2.0 에서 지원하는 템플릿 엔진은 타임리프, 프리마커, 무스타치, 그루비 템플릿등이 있음

### 7. 타임리프 자바 8 날짜 포맷 라이브러리 추가하기
**temporals**를 사용할 수 있게 해주는 `thymeleaf-extras-java8time` 의존성은 `spring-boot-stater-thymeleaf` 스타터에 포함되어 있음
#### `thymeleaf-extra-java8time` 라이브러리 주요 날짜 포맷팅 함수
##### 단일 값을 날짜 값으로 변환해주는 `format()`함수
`${#temporals.format(temporal, 'yyyy/MM/dd HH:mm')}`
##### Array 타입을 변환해주는 `arrayFormat()` 함수 
`${#temporals.arrayFormat(temporalsArray, 'yyyy/MM/dd HH:mm')}`
##### List 타입을 변환해주는 `listFormat()` 함수
`${#temporals.listFormat(temporalsList, 'yyyy/MM/dd HH:mm')}`
##### Set 타입을 변환해주는 `setFormat()` 함수
`${#temporals.setFormat(temporalsSet, 'yyyy/MM/dd HH:mm')}`

### 8. 페이징 처리하기 
#### 페이징 객체를 사용해서 뷰 쪽에 구현할 기능
- 맨 처음으로 이동 버튼
- 이전 페이지로 이동 버튼(첫 페이지면 미노출)
- 10페이지 단위로 이동 버튼
- 다음 페이지로 이동 버튼(마지막 페이지면 미노출)
- 맨 마지막 페이지로 이동 버튼

### 9. 작성 폼 만들기
`${...?}` 처럼 구문 뒤에 '?'를 붙여서 null 체크를 추가해 값이 null인 경우에는 빈값이 출력됨