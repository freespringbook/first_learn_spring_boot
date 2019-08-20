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

### `@ConfigurationProperties`의 유연한 바인딩
프로퍼티값을 객체에 바인딩할 경우 필드를 낙타 표기법으로 선언하고 프로퍼티의 키는 다양한 형식  
(낙타 표기법, 케밥 표기법, 언더바 표기법 등)으로 선언하여 바인딩할 수 있는 것을 말함
- fruit.colorname
- fruit.colorName
- fruit.color-name
- fruit.color_name
> 기존 버전에서는 프로퍼티 명에 낙타 표기법, 언더바 표기법, 대문자 등을 모두 지원했지만  
스프링 부트 2.0부터는 소문자나 케밥 표기법만 지원함

## 2.5 자동 환 설정 이해하기
`@SpringBootApplication`은 자동 설정뿐만 아니라 부트 실행에 있어서 필수적인 어노테이션

### 1. 자동 환경 설정 어노테이션
스프링 부트는 관련 의존성을 스타터라는 묶음으로 제공하며 수동 설정을 지

#### SpringBootApplication 소스 코드
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
    ...
}
```
1. `@SpringBootConfiguration`: 스프링 부트의 설정을 나타내는 어노테이션  
스프링의 `@Configuration`을 대체하며 스프링 부트 전용으로 사용  
예를 들어 스프링 부트의 테스트 어노테이션(`@SpringBootTest`)을 사용할 때 찾기 알고리즘을 사용하여  
계속 `@SpringBootConfiguration` 어노테이션을 찾기 때문에 스프링 부트에서는 필수 어노테이션 중 하나

2. `@EnableAutoConfiguration`: 자동 설정의 핵심 어노테이션  
클래스 경로에 지정된 내용을 기반으로 영리하게 설정 자동화를 수행  
특별한 설정값을 추가하지 않으면 기본값으로 작동

3. `@ComponentScan`: 특정 패키지 경로를 기반으로 `@Configuration`에서 사용할 `@Component` 설정 클래스를 찾음
`@ComponentScan`의 basePackages 프로퍼티값에 별도의 경로를 설정하지 않으면 `@ComponentScan`이 위치한  
패키지가 루트 경로(BasePackage)로 설정됨

`@SpringBootApplication` 어노테이션은 `@SpringBootConfiguration` + `@EnableAutoConfiguration` +  `@ComponentScan`  
어노테이션의 조합
이 중 `@EnableAutoConfiguration`이 자동 환경 설정의 핵심 어노테이션

### 2. `@EnableAutoConfiguration` 살펴보기
#### @EnableAutoConfiguration 어노테이션 코드
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
    ...
}
```
`@Import(AutoConfigurationImportSelector.class)` 자동 설정을 지원해주는 어노테이션

#### 빈의 등록과 자동 설정에 필요한 파일
- META-INF/spring.factories: 자동 설정 타깃 클래스 목록  
즉, 이곳에 선언되어 있는 클래스 들이 `@EnableAutoConfiguration` 사용 시 자동 설정 타깃 이 됨
- META-INF/spring-configuration-metadata.json: 자동 설정에 사용할 프로퍼티 저으이 파일  
미리 구현되어 있는 자동 설정에 프로퍼티만 주입시켜주면 됨  
따라서 별도의 환경 설정은 필요 없음
- org/springframework/boot/autoconfigure: 미리 구현해놓은 자동 설정 리스트  
이름은 '{특정 설정의 이름}AutoConfiguration' 형식으로 지정되어 있으며 모두 자바 설정 방식을 따름

위 파일 모두 `spring-boot-autoconfiguration`에 미리 정의되어 있으며 지정된 프로퍼티값을 사용하여  
설정 클래스 내부의 값들을 변경할 수 있음

#### H2 자동 설정
`spring.factories`에서 자동 설정 대상에 해당되는지 확인
`spring-configuration-metadata.json`에서 주요 프로퍼티값과 설정 가능한 타입 확인
```properties
   {
      "name": "spring.h2.console.path",
      "type": "java.lang.String",
      "description": "Path at which the console is available.",
      "sourceType": "org.springframework.boot.autoconfigure.h2.H2ConsoleProperties",
      "defaultValue": "\/h2-console"
    },
``` 

H2 경로의 기본값은 /h2-console 이고 String 형인 것을 확인

##### application.yml에서 H2 path 변경
```properties
spring:
    h2:
        console:
            path: /h2-test
```

#### 스프링 프로퍼티 문서
쉽게 프로퍼티 값을 확인할 수 있음  
https://docs.spring.io/spring-boot/docs/current/reference/html/  
'A. Common application properties' 카테고리에서 확인  

### 3. 자동 설정 어노테이션 살펴보기
##### 자동 설정을 위한 조건 어노테이션
| 조건 어노테이션                   | 적용 조건                                                    |
| --------------------------------- | ------------------------------------------------------------ |
| `@ConditionalOnBean`              | 해당하는 빈(Bean) 클래스나 이름이 미리 빈 팩토리에 포함되어 있을 경우 |
| `@ConditionalOnClass`             | 해당하는 클래스가 클래스 경로에 있을 경우                    |
| `@ConditionalOnCloudPlatform`     | 해당하는 클라우드 플랫폼이 활용 상태일 경우                  |
| `@ConditionalOnExpression`        | SpEL에 의존하는 조건일 경우                                  |
| `@ConditionalOnJava`              | JVM 버전이 일치하는 경우                                     |
| `@ConditionalOnJndi`              | JNDI가 사용가능하고 특정 위치에 있는 경우                    |
| `@ConditionalOnMissingBean`       | 해당하는 빈 클래스나 이름이 미리 빈 팩토리에 포함되지 않은 경우 |
| `@ConditionalOnMissingClass`      | 해당하는 클래스가 클래스 경로에 없을 경우                    |
| `@ConditionalOnNotWebApplication` | 웹 애플리케이션이 아닌 경우                                  |
| `@ConditionalOnProperty`          | 특정한 프로퍼티가 지정한 값을 갖는 경우                      |
| `@ConditionalOnResource`          | 특정한 리소스가 클래스 경로에 있는 경우                      |
| `@ConditionalOnSingleCandidate`   | 지정한 빈 클래스가 이미 빈 팩토리에 포함되어 있고 단일 후보자로 지정 가능한 경우 |
| `@ConditionalOnWebApplication`    | 웹 애플리케이션인 경우                                       |

#### 자동 설정을 위한 순서 어노테이션
| 순서 어노테이션        | 설명                                                         |
| ---------------------- | ------------------------------------------------------------ |
| `@AutoConfigureAfter`  | 지정한 특정 자동 설정 클래스들이 적용된 이후에 해당 자동 설정 적용 |
| `@AutoConfigureBefore` | 지정한 특정 자동 설정 클래스들이 적용되기 이전에 해당 자동 설정 적용 |
| `@AutoConfigureOrder`  | 자동 설정 순서 지정을 위한 스프링 프레임워크의 `@Order` 변형 어노테이션  기존의 설정 클래스에는 영향을 주지 않고 자동 설정 클래스들 간의 순서만 지정 |

#### H2ConsoleAutoConfiguration 어노테이션
아래의 세가지 조건에 부합할 때 H2ConsoleAutoConfiguration 클래스가 적용됨
```java
// 웹 애플리케이션일 때
@ConditionalOnWebApplication(type = Type.SERVLET)
// WebServlet.class가 클래스 경로에 있을 때
@ConditionalOnClass(WebServlet.class)
// spring.h2.console.enabled 값이 true 일 때
@ConditionalOnProperty(prefix = "spring.h2.console", name = "enabled", havingValue = "true", matchIfMissing = false)
``` 

### 4. H2 Console 자동 설정 적용하기

#### H2 의존성 추가
```groovy
compile('com.h2database:h2')
```

#### 빈을 등록해 H2 콘솔 사용하기
```java
@Configuration
public class DataSourceConfig {

    @Bean
    ServletRegistrationBean h2servletRegistration(){
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebdavServlet());
        registrationBean.addUrlMappings("/console/*");
        return registrationBean;
    }
}
```

#### H2 Console 프로퍼티
```properties
# H2 Web Console (H2ConsoleProperties)
spring.h2.console.enabled=false
spring.h2.console.path=/h2-console
spring.h2.console.settings.trace=false
spring.h2.console.web-allow-others=false
```

#### spring.h2.console.enabled를 true로 변경
```yaml
# H2 메모리 DB를 사용하기 위한 설정
datasource:
  url: jdbc:h2:mem:testdb
  
spring:
  h2:
    console:
      enabled: true
```

#### H2 런타임 의존성으로 변경
H2 메모리 데이터베이스는 보통 테스트용으로만 쓰임  
런타임 시점에만 의존하도록 변경
```groovy
runtime('com.h2database:h2')
```