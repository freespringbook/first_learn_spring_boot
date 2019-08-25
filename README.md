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