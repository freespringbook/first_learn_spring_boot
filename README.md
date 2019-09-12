# 처음 배우는 스프링 부트2
##  7. 스프링 부트 배치
스프링 배치는 백엔드의 배치 처리 기능을 구현하는 데 사용하는 프레임워크  
스프링 부트 배치는 스프링 배치의 설정 요소들을 간편화시켜 스프링 배치를 빠르게 설정하는데 도움을 줌

## 7.1 배경지식
스프링 부트 배치는 스프링 본부인 Pivotal과 컨설팅 회사인 Accenture가 공동으로 개발했음  

### 1. 배치 처리에 스프링 부트 배치를 써야 하는 이유
##### 스프링 부트 배치의 장점
- 대용량 데이터 처리에 최적화되어 고성능을 발휘함
- 효과적인 로깅 통계 처리, 트랜잭션 관리 등 재사용이 가능한 필수 기능을 지원함
- 수동으로 처리하지 않도록 자동화되어 있음
- 예외사항과 비정상 동작에 대한 방어 기능이 있음
- 스프링 부트 배치의 반복되는 작업 프로세스를 이해하면 비즈니스 로직에 집중할 수 있음

### 2. 스프링 부트 배치 2.0
스프링 부트 배치 2.0은 최신 버전인 스프링 배치 4.0을 기반으로 함
##### 스프링 배치 4.0의 세 가지 특징
- 기본적으로 자바 8 이상에서 동작함
- 자바 8은 함수형 인터페이스와 람다를 지원해 한층 더 편리한 개발이 가능함
- 스프링 프레임워크 5로 진화하면서 새롭게 재배열된 의존성 트리를 지원함
- ItemReaders, ItemProcessors, ItemWriters에 대한 빌더를 제공함

### 3. 스프링 부트 배치의 주의사항
스프링 부트 배치는 스프링 배치를 간편하게 상요할 수 있게 래핑한 프로젝트임
##### 스프링 부트 배치와 스프링 배치의 주의사항
1. 가능하면 단순화해서 복잡한 구조와 로직을 피해야 함
2. 데이터를 직접 사용하는 작업이 빈번하게 일어나므로 데이터 무결성을 유지하는 유효성 검사 등의 방어책이 있어야 함
3. 배치 처리 시 시스템 I/O 사용을 최소화해야함  
  잦은 I/O로 데이터베이스 커넥션과 네트워크 비용이 커지면 성능에 영향을 줄 수 있기 때문임  
  따라서 가능하면 한번에 데이터를 조회하여 메모리에 저장해두고 처리를 한 다음 그 결과를 한번에 데이터베이스에 저장하는 것이 좋음
4. 일반적으로 같은 서비스에 사용되는 웹, API, 배치, 기타 프로젝트들은 서로 영향을 줌 
   따라서 배치 처리가 진행되는 동안 다른 프로젝트 요소에 영향을 주는 경우가 없는지 주의를 기울여야 함
5. 스프링 부트 배치는 스케쥴러를 제공하지 않음  
   배치 처리 기능만 제공하며 스케줄링 기능은 스프링에서 제공하는 쿼츠 프레임워크(Quartz Framework), IBM 티볼리(Tivoli) 스케줄러, BMC 컨트롤-M(Control-M) 등을 이용해야 함  
   리눅스 crontab 명령은 가장 간단히 사용할 수 있지만 이는 추천하지 않음  
   crontab의 경우 각 서버마다 따로 스케줄링을 관리해야 하며 무엇보다 클러스터링 기능이 제공되지 않음  
   반면에 쿼츠와 같은 스케줄링 프레임워크를 사용한다면 클러스터링뿐만 아니라 다양한 스케줄링 기능, 실행 이력 관리 등 여러 이점을 얻을 수 있음

## 7.2 스프링 부트 배치 이해하기
##### 배치의 일반적인 3단계 시나리오
1. 읽기(read): 데이터 저장소(일반적으로 데이터베이스)에서 특정 데이터 레코드를 읽음
2. 처리(processing): 원하는 방식으로 데이터를 가공/처리함
3. 쓰기(write): 수정된 데이터를 다시 저장소(데이터베이스)에 저장함

![배치 관련 객체 관계도](/images/batch_object_relationship.png)

Job 이라는 하나의 큰 일감(Job)에 여러 단계(Step)를 두고, 각 단계를 배치의 기본 흐름대로 구현함

### 1. Job
Job은 배치 처리 과정을 하나의 단위로 만들어 표현한 객체  
전체 배치 처리에 있어 항상 최상단 게층에 있음

스프링 배치에서 Job 객체는 여러 Step 인스턴스를 포함하는 컨테이너

여러 빌더를 통합하는 처리 공장인 `JobBuilderFactory`로 원하는 Job을 손쉽게 만들 수 있음  
`JobBuilderFactory`의 `get()` 메서드로 `JobBuilder`를 생성하고 이를 이용함

##### JobBuilderFactory 클래스
```java
public class JobBuilderFactory {
  private JobRepository jobRepository;

  public JobBuilderFactory(JobRepository jobRepository) {
    this.jobRepository = jobRepository;
  }

  public JobBuilder get(String name) {
    JobBuilder builder = new JobBuilder(name).repository(jobRepository);
    return builder;
  }
}
```
`JobBuilderFactory`는 `JobBuilder`를 생성하는 역할만 수행함

##### JobBuilder의 메서드
```java
/**
 * Step을 추가해서 가장 기본이 되는 SimpleJobBuilder를 생성함
 */
public SimpleJobBuilder start(Step step) {
  return new SimpleJobBuilder(this).start(step);
}

/**
 * Flow를 실행할 JobFlowBuilder를 생성함
 */
public JobFlowBuilder start(Flow flow) {
  return new FlowJobBuilder(this).start(flow);
}

/**
 * Step을 실행할 JobFlowBuilder를 생성함
 */
public JobFlowBuilder flow(Step step) {
  return new FlowJobBuilder(this).start(Step);
}
```
`JobBuilder`는 직접적으로 `Job`을 생성하는 거시 아니라 별도의 구체적인 빌더를 생성하여 반환함  
`Job`을 생성하기 위한 `Step` 또는 `Flow`를 파라미터로 받아 구체적인 빌더를 생성함  
`Job`은 `Step` 또는 `Flow` 인스턴스의 컨테이너 역할을 하기 떄문에 생성하기 전에 인스턴스를 전달받음

##### SimpleJobBuilder를 활용한 Job 생성 예제 코드
```java
@Autowired
private JobBuilderFactory jobBuilderFactory;

@Bean
public Job simpleJob() {
  return jobBuilderFactory.get("simpleJob")
      .start(simpleStep());
      .build();
}
```

#### JobInstance
`JobInstance`는 배치에서 `Job`이 실행될 때 하나의 `Job` 실행 단위
각각의 `JobInstance`는 하나의 `JobExecution`(`JobInstance`에 대한 한 번의 실행을 나타내는 객체)

`Job` 실행이 실패하면 `JobInstance`이 끝난 것으로 간주하지 않고 다시 실행함  
`JobInstance`는 성공과 실패한 `JobExecution` 여러 개를 가질 수 있음

#### JobExecution
`JobExecution`은 `JobInstance`에 대한 한 번의 실행을 나타내는 객체  
`Job`을 실행할 때 같은 `JobInstance`를 사용하여 각기 다른 `JobExecution`을 생성함
`JobExecution` 인터페이스를 보면 `Job` 실행에 대한 정보를 담고 있는 도메인 객체라는 것을 알 수 있음

`JobExecution`은 `JobInstance`, 배치 실행 상태, 시작 시간, 끝난 시간, 실패했을 때의 메시지 등의 정보를 담고 있음

##### JobExecution 인터페이스
```java
public class JobExecution extends Entity {

	private final JobParameters jobParameters;
	private JobInstance jobInstance;
	private volatile Collection<StepExecution> stepExecutions = Collections.synchronizedSet(new LinkedHashSet<>());
	private volatile BatchStatus status = BatchStatus.STARTING;
	private volatile Date startTime = null;
	private volatile Date createTime = new Date(System.currentTimeMillis());
	private volatile Date endTime = null;
	private volatile Date lastUpdated = null;
	private volatile ExitStatus exitStatus = ExitStatus.UNKNOWN;
	private volatile ExecutionContext executionContext = new ExecutionContext();
	private transient volatile List<Throwable> failureExceptions = new CopyOnWriteArrayList<>();
	private final String jobConfigurationName;
}
```
- **jobParameters**: `Job` 실행에 필요한 매개변수 데이터
- **jobInstance**: `Job` 실행의 단위가 되는 객체
- **stepExecutions**: **StepExecution**을 여러 개 가질 수 있는 Collection 타입
- **status**: `Job`의 실행 상태를 나타내는 필드
  상탯값은 `COMPLETED`, `STARTING`, `STARTED`, `STOPPING`, `STOPPED`, `FAILED`, `ABANDONED`, `UNKNOWN` 등이 있으며 기본값은 `STARTING`
- **startTime**: `Job`이 실행된 시간 `null`이면 시작하지 않았다는 것을 나타냄
- **createTime**: `JobExecution`이 생성된 시간
- **endTime**: `JobExecution`이 끝난 시간
- **lastUpdated**: 마지막으로 수정된 시간
- **exitStatus**: `Job` 실행 결과에 대한 상태를 나타냄
  상탯값은 `UNKNOWN`, `EXECUTING`, `COMPLETED`, `NOOP`, `FAILED`, `STOPPED` 등이 있으며 기본값은 `UNKNOWN`
- **executionContext**: `Job` 실행 사이에 유지해야 하는 사용자 데이터가 들어 있음
- **failureExecutions**: `Job` 실행 중 발생한 예외를 **List** 타입으로 저장함
- **jobConfigurationName**: `Job` 설정 이름을 나타냄

#### JobParameters
`JobParameters`는 `Job`이 실행될 때 필요한 파라미터들을 **Map** 타입으로 저장하는 객체

`JobParameters`는 `JobInstance`를 구분하는 기준이 되기도 함  
`JobInstance`와 `JobParameters`는 **1:1** 관계임  
파라미터의 타입으로는 `String`, `Long`, `Date`, `Double`을 사용할 수 있음

### 2. Step
`Step`은 실질적인 배치 처리를 정의하고 제어하는 데 필요한 모든 정보가 들어 있는 도메인 객체

`Job`을 처리하는 실질적인 단위  
모든 `Job`에는 1개 이상의 `Step`이 있어야 함

#### StepExecution
`Step` 실행 정보를 담는 객체  
각각의 `Step`이 실행될 때마다 `StepExecution`이 생성됨

##### StepExecution 클래스
```java
public class StepExecution extends Entity {

	private final JobExecution jobExecution;
	private final String stepName;
	private volatile BatchStatus status = BatchStatus.STARTING;
	private volatile int readCount = 0;
	private volatile int writeCount = 0;
	private volatile int commitCount = 0;
	private volatile int rollbackCount = 0;
	private volatile int readSkipCount = 0;
	private volatile int processSkipCount = 0;
	private volatile int writeSkipCount = 0;
	private volatile Date startTime = new Date(System.currentTimeMillis());
	private volatile Date endTime = null;
	private volatile Date lastUpdated = null;
	private volatile ExecutionContext executionContext = new ExecutionContext();
	private volatile ExitStatus exitStatus = ExitStatus.EXECUTING;
	private volatile boolean terminateOnly;
	private volatile int filterCount;
	private transient volatile List<Throwable> failureExceptions = new CopyOnWriteArrayList<Throwable>();
}
```
- **jobExecution**: 현재의 `JobExecution` 정보를 담고 있는 필드
- **stepName**: `Step`의 이름을 가지고 있는 필드
- **status**: `Step`의 실행 상태를 나타내는 필드
  상탯값은 `COMPLETED`, `STARTING`, `STARTED`, `STOPPING`, `STOPPED`, `FAILED`, `ABANDONED`, `UNKNOWN` 등이 있으며 기본값은 `STARTING`
- **readCount**: 성공적으로 읽은 레코드 수
- **writeCount**: 성공적으로 쓴 레코드 수
- **commitCount**: `Step`의 실행에 대한 커밋된 트랜잭션 수
- **rollbackCount**: `Step`의 실행에 대해 롤백된 트랜잭션 수
- **readSkipCount**: 읽기에 실패해 건너뛴 레코드 수
- **processSkipCount**: 프로세스가 실패해 건너뛴 레코드 수
- **writeSkipCount**: 쓰기에 실패해 건너뛴 레코드 수
- **startTime**: `Step`이 실행된 시간 `null`이면 시작하지 않았다는 것을 나타냄
- **endTime**: `Step`의 실행 성공 여부와 관련 없이 `Step`이 끝난 시간
- **lastUpdated**: 마지막으로 수정된 시간
- **executionContext**: `Step` 실행 사이에 유지해야 하는 사용자 데이터가 들어 있음
- **exitStatus**: `Step` 실행 결과에 대한 상태를 나타냄
  상탯값은 `UNKNOWN`, `EXECUTING`, `COMPLETED`, `NOOP`, `FAILED`, `STOPPED` 등이 있으며 기본값은 `UNKNOWN`
- **terminateOnly**: `Job` 실행 중지 여부
- **filterCount**: 실행에서 필터링된 레코드 수
- **failureExeptions**: `Step` 실행 중 발생한 예외를 **List** 타입으로 지정

### 3. JobRepository
`JobRepository`는 배치 처리 정볼르 담고 있는 매커니즘  
어떤 `Job`이 실행되었으며 몇 번 실행되었고 언제 끝났는지 등 배치 처리에 대한 메타데이터를 저장

`JobRepository`는 `Step`의 실행 정보를 담고 있는 `StepExecution`도 저장소에 저장하며 전체 메타데이터를 저장/관리하는 역할을 수행

### 4. JobLauncher
`JobLauncher`는 `Job`, `JobParameters`와 함께 배치를 실행하는 인터페이스

##### JobLauncher 인터페이스
```java
public interface JobLauncher {
	public JobExecution run(Job job, JobParameters jobParameters) throws JobExecutionAlreadyRunningException,
			JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException;
}
```
매개변수가 이전과 동일하면서 이전에 `JobExecution`이 중단된 적이 있다면 동일한 `JobExecution`을 반환

### 5. ItemReader
ItemReader는 Step의 대상이 되는 배치 데이터를 읽어오는 인터페이스  
FILE, XML, DB등 여러 타입의 데이터를 읽어올 수 있음

##### ItemReader 인터페이스
```java
public interface ItemReader<T> {
	T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException;
}
```
`ItemReader`에서 `read()` 메서드의 반환 타입을 제네릭으로 구현했기 때문에 직접 타입을 지정할 수 있다

### 6. ItemProcessor
ItemProcessor는 ItemReader로 읽어온 배치 데이터를 변환하는 역할을 수행함

##### ItemProcessordmf 따로 제공하는 이유
- 비즈니스 로직을 분리하기 위함  
  `ItemWriter`는 저장만 수행하고 `ItemProcessor`는 로직 처리만 수행해 역할을 명확하게 분리
- 읽어온 배치 데이터와 쓰여질 데이터의 타입이 다를 경우에 대응하기 위함  
  명확한 인풋과 아웃풋을 `ItemProcessor`로 구현

##### ItemProcessor 인터페이스
```java
public interface ItemProcessor<I, O> {
	O process(I item) throws Exception;
}
```

### 7. ItemWriter
`ItemWriter`는 배치 데이터를 저장함  
일반적으로 DB나 파일에 저장함

##### ItemWriter 인터페이스
```java
public interface ItemWriter<T> {
	void write(List<? extends T> items) throws Exception;
}
```

## 7.3 스프링 부트 휴면회원 배치 설계하기
커뮤니티에 가입한 회원중 1년이 지나도록 상태 변화가 없는 회원을 휴면회원으로 전환하는 배치 개발

![전체 배치 프로세스](/images/all_batch_process.png)
1. H2 DB에 저장된 데이터 중 1년간 업데이트되지 않은 사용자를 찾는 로직을 ItemReader로 구현
2. 대상 사용자 데이터의 상탯값을 휴면회원으로 전환하는 프로세스를 ItemProcessor에 구현
3. 상탯값이 변한 휴면회원을 실제로 DB에 저장하는 ItemWriter를 구현

## 7.4 스프링 부트 배치 설정하기
배치 프로젝트 생성 'Spring-Boot-Community-Bathch'

1. build.gradle 의존성 설정
2. UserStatus, SocialType Enum
3. Grade Enum
4. User 클래스