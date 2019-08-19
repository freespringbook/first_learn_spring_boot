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

5. demo-web 모듈과 같은 방식으로 demo-domain 모듈도 생성