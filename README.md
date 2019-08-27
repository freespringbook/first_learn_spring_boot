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