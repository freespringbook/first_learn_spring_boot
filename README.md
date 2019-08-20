# 처음 배우는 스프링 부트2

## 2.3 그레이들 설치 및 빌드하기
메이븐 설정 파일인 `pom.xml`은 XML 기반으로 작성되어 있어 동적인 행위에 제약이 있음  

그레이들은 Ant로 부터 기본적인 빌드 도구의 기능을  
메이븐으로부터 의존 라이브러리 관리 기능을 차용함  

멀티 프로젝트 구성 시에는 메이븐처럼 상속 구조가 아닌 설정 주입방식을 사용하여  
훨씬 유연하게 빌드 환경을 구성할 수 있음

### 1. 그레이들 래퍼
스프링 이니셜라이저로 생성한 프로젝트는 Gradle Wrapper 설정이 그레이들 관련 빌드 설정을 자동으로 해줌
- gradlew: 리눅스 및 맥OS용 셸 스크립트
- gradlew.bat: 윈도우용 배치 스크립트
- gradle/wrapper/gradle-wrapper.jar: Wrapper JAR
- gradle/wrapper/gradle-wrapper.properties: 그레이들 설정 정보 프로퍼티 파일(버전 정보 등)

#### 그레이들 버전 변경
`gradle-wrapper.properties`에서 `distributionUrl`을 원하는 그레이들 버전으로 수정

셸 스크립트로 버전 변경
```bash
$ ./gradlew wrapper --gradle-version 5.4.1
```

버전 확인
```bash
$ ./gradlew -v
```

### 2. 그레이들 멀티 프로젝트 구성하기
그레이들 멀티 프로젝트를 활용하면 여러 프로젝트를 마치 하나의 프로젝트처럼 사용할 수 있음  
일반적으로 이 기능은 공통 코드를 하나의 프로젝트로 분리하고 이를 재사용할 때 유용

1. `settings.gradle` 파일은 그레이들 설정 파일  
특정한 명령 규칙에 따라 그레이들에서 자동으로 인식하여 설정됨
루트 프로젝트 demo로 설정  
```properties
rootProject.name = 'demo'
```

2. demo-web 모듈 생성  
루트 경로에서 New -> Module 를 선택  
좌측 카테고리에서 Gradle를 선택한 뒤 Java를 선택하고 Next

3. Add as module to 에서 community 프로젝트를 선택한 다음  
 ArtifactId에 demo-web을 입력하고 Next 버턴을 눌러 모듈 생성
 
4. 생성된 demo-web 모듈에는 build.gradle 파일만 존재  
기본 패키지 경로를 수동으로 생성(얼티밋 버전은 자동으로 생성됨)
- src/main/java: 자바 소스 경로
- src/text/java: 스프링 부트의 테스트 코드 경로
- src/main/resources/static: static 한 파일(css, image, js 등)의 디폴트 경로
- src/main/resources/templates: thymeleaf, freemarker 및 기타 서버 사이드 템플릿 파일의 경로

intelliJ 에서 모듈 생성 기능을 사용하면 settings.gradle에 자동으로 생성된 모듈명이 include 됨

demo-web 모듈 생성 시 `settings.gradle`에 include 코드 자동 생성
```properties
rootProject.name = 'demo'
include 'demo-web'
```

5. demo-web 모-듈과 같은 방식으로 demo-domain 모듈도 생성

## 2.4 환경 프로퍼티 파일 설정하기
`application.properties`에서 서버 포트 설정
```properties
server.port: 80
```

`application.yml`을 통한 서버 포트 설정 변경
```properties
server:
    port: 80
```
> 만약 application.properties와 application.yml 파일이 둘 다 생성되어 있다면 application.yml만 오버라이드되어 적용됨

### 1. 프로파일에 따른 환경 구성 분리
`application.yml`에서 프로파일별 설정 구분
```properties
server:
  port: 80
---
spring:
  profiles: local
server:
  port: 8080
---
spring:
  profiles: dev
server:
  port: 8081
---
spring:
  profiles: real
server:
  port: 8082
```

jar파일로 실행시 간단한 명령으로 프로파일 설정하여 실행
```bash
$ java -jar ... -D spring.profiles.active=dev
```

#### IntelliJ 에서 프로파일 설정
Edit Configurations - Run/Debug Configuration - Active Profiles 설정

### 2. YAML 파일 매핑하기
`@Value`와 `@ConfigurationProperties`

| 기능            | @Value | @ConfigurationProperties |
| --------------- | :----: | :----------------------: |
| 유연한 바인딩   |   X    |            O             |
| 메타데이터 지원 |   X    |            O             |
| SpEL 평가       |   O    |            X             |

> 클래스, 메서드, 필드등 프로그램 요소에 정보를 제공하는 기법 타깃 요소를 제어/관리/명시하는 등의 다양한 기능을 할 수 있다

- 유연한 바인딩: 프로퍼티값을 객체에 바인딩할 경우 필드를 낙타 표기법(Camel Case)로 선언하고  
프로퍼티의 키는 다양한 형식(낙타 표기법, 케밥 표기법(Kebab Case), 언더바 표기법(Underscore) 등)  
으로 선언하여 바인딩할 수 있다  
- 메타데이터 지원: 프로퍼티의 키에 대한 정보를 메타데이터 파일(JSON)로 제공한다  
키의 이름, 타입, 설명, 디폴트값 등 키 사용에 앞서 힌트가 되는 정보를 얻을 수 있다
- SpEL(Spring Expression Language, 스프링 표현 언어) 평가: SpEL은 런타임에 객체 참조에 대해  
질의하고 조작하는 기능을 지원하는 언어  
특히 메서드 호출 및 기본 문자열 템플릿 기능을 제공  
`@Value`만 사용 가능

### `@Value` 살펴보기
프로퍼티의 키를 사용하여 특정한 값을 호출  
키를 정확히 입력해야 하며 값이 없을 경우에 대해 예외 처리를 해주어야 함

`application.yml`에 test 프로퍼티 추가하기
```properties
property:
  test:
    name: property depth test
propertyTest: test
propertyTestList: a,b,c
```

#### `@Value`의 매핑 방식
- `@Value("${property.test.name}")`: 깊이가 존재하는 키값에 대해 '.'로 구분하여 해당 값을 매핑
- `@Value("${propertyTest}")`: 단일 키값을 매핑
- `@Value("${noKey:default value}")`: YAML 파일에 키값이 존재하지 않으면 디폴트값이 매핑되도록 설정
- `@Value("${propertyTestList}")`: 여러 값을 나열할 때는 배열형으로 매핑
- `@Value("#{'${propertyTestList}'.split(',')}")`: SpEL을 사용하여 ','를 기준으로 List에 매핑

주로 단일 필드값을 가져오는 데 사용

### `@ConfigurationProperties` 살펴보기
`@ConfigurationProperties`는 기본적으로 prefix를 사용하여 값을 바인딩한다

`application.yml`이 아닌 다른 이름의 YAML 파일을 따로 생성해서 관리할때
```java
@ConfigurationProperties(prefix = "fruit")
```

`@ConfigurationProperties`는 기본 컬렉션 타입뿐만 아니라 POJO 타입 매핑도 제공
Map 타입 자료구조보다 POJO 타입이 더 직관적이고 더 명확하게 객체를 구성할 수 있기 때문에 좋음
