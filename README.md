# 처음 배우는 스프링 부트2
## 3.1 @SpringBootTest
`@SpringBootTest`는 통합 테스트를 제공하는 기본적인 스프링 부트 테스트 어노테이션

`@SpringBootTest`의 프로퍼티 중 value와 properties를 함께 사용하면 에러가 발생

- value: 테스트가 실행되기 전에 적용할 프로퍼티를 주입시킬 수 있음. 즉, 기존의 프로퍼티를 오버라이드 함
- properties: 테스트가 실행되기 전에 {key=value} 형식으로 프로퍼티를 추가할 수 있음
- classes: 애플리케이션 컨텍스트에 로드할 클래스를 지정할 수 있음. 따로 지정하지 않으면 `@SpringBootConfiguration`을 찾아서 로드함
- webEnvironment: 애플리케이션이 실행될 때의 웹 환경을 설정할 수 있음. 기본값은 Mock 서블릿을 로드하여 구동되며 예제에서는 랜덤 포트값을 주어 구동시킴

#### `@SpringBootTest를 사용할 때 몇 가지 추가적인 팁
- 프로파일 환경(개발, QA, 운영 환경)마다 다른 데이터소스(DataSoruce)를 갖는다면 어떻게 해야 할까?  
이 경우 `@ActiveProfiles("local")`과 같은 방식으로 **원하는 프로파일 환경값을 부여** 하면 됨
- 테스트에서 `@Transactional`을 사용하면 테스트를 마치고 나서 수정된 **데이터가 롤백됨**  
다만 테스트 서버의 다른 스레드에서 실행 중이면 webEnvironment의 RANDOM_PORT나 DEFINED_PORT를 사용하여 테스트를 수행해도 트랜잭션이 롤백되지 않음
- `@SpringBootTest`는 기본적으로 검색 알고리즘을 사용하여 `@SpringBootApplication`이나 `@SpringBootConfiguration`어노테이션을 찾음  
스프링 부트 테스트이기 때문에 해당 어노테이션 중 하나는 필수

## 3.2 @WebMvcTest
MVC를 위한 테스트  
웹에서 테스트하기 힘든 컨트롤러를 테스트하는데 적합  
웹상에서 요청과 응답에 대해 테스트  
뿐만 아니라 시큐리티 혹은 필터까지 자동으로 테스트하며 수동으로 추가/삭제 가능

`@Controller`, `@ControllerAdvice`, `@JsonComponent`와 Filter, WebMvcConfigurer, HandlerMethodArgumentResolver만 로드됨

`@Service` 어노테이션은 `@WebMvcTest`의 적용 대상이 아님
BookService 인터페이스를 구현한 구현체는 없지만 `@MockBean`을 적극적으로 활용하여 컨트롤러 내부의 의존성 요소인  
BookService를 가짜로 객체로 대체하였음

`@MockBean`을 사용하여 가짜 객체를 만들고 given()을 사용하여 getBookList() 메서드의 실행에 대한 반한값을 미리 정의하여  
MockMvc를 사용하면 해당 URL의 상탯값, 반환값에 대한 테스트를 수행할 수 있음

## 3.3 @DataJpaTest
`@DataJpaTest` 어노테이션은 JPA 관련 테스트 설정만 로드  
데이터소스의 설정이 정상적인지, JPA를 사용하여 데이터를 제대로 생성, 수정, 삭제하는지 등의 테스트가 가능함

`@AutoConfigureTestDatabase` 어노테이션의 기본 설정 값인 Replace.Any를 사용하면 기본적으로 내장된 데이터소스를 사용  
예제에서와 같이 Replace.NONE으로 설정하면 `@ActiveProfiles`에 설정한 프로파일 환경값에 따라 데이터소스가 적용됨

application.yml에서 프로퍼티 설정을 `spring.test.database.replace: NONE`으로 변경 됨

`@DataJpaTest`는 JPA 테스트가 끝날 때마다 자동으로 테스트에 사용한 데이터를 롤백함

`spring.test.database.connection: H2`와 같이 프로퍼티를 설정하는 방법과  
`@AutoConfigureTestDatabase(connection = H2)` 어노테이션으로 설정하는 방법  
connection의 옵션으로 H2, Derby, HSQL 등의 테스트 데이터베이스 종류를 선택할 수 있음

테스트용 TestEntityManager를 사용하면 persist, flush, find 등과 같은 기본적인 JPA 테스트를 할 수 있음