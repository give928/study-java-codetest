# 백기선님 더 자바, 애플리케이션을 테스트하는 다양한 방법 스터디

## JUnit 5

### 소개

자바 개발자가 가장 많이 사용하는 테스팅 프레임워크

Java 8 이상

대체제: TEstNG, Spock, ...

JUnit Platform: 테스트를 실행해주는 런처 제공. TestEngine API 제공

Jupiter: TestEngine API 구현체로 JUnit 5를 제공

Vintage: JUnit 4와 3을 지원하는 TestEngine 구현체

- @Test, @Disabled
- @BeforeAll, @AfterAll, @BeforeEach, @AfterEach
- @DisplayName, @DisplayNameGeneration
- Assertion
    - assertEquals, assertNotNull, assertTrue, assertAll, assertThrows, assertTimeout 등
    - AssertJ, Hemcrest, Truth 등의 라이브러리를 사용할 수도 있다.

테스트 간의 의존성을 없애기 위해 테스트 인스턴스는 메서드마다 새로 생성되는 것을 기본 전략으로 한다.

@TestInstance(TestInstance.Lifecycle.PER_CLASS) 로 클래스에 인스턴스를 지정해서 동일한 인스턴스로 테스트를 수행할 수 있다. @BeforeAll, @AfterAll 메서드도 static일 필요가 없어진다.

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) 메서드 순서 애너테이션으로 테스트 메서드 순서를 설정하고 테스트 메서드에 @Order(n) 으로 설정하면 원하는 순서로 테스트할 수 있다.

- /src/test/resources/junit-platform.properties

```java
# 테스트 인스턴스 라이프사이클 설정
junit.jupiter.testinstance.lifecycle.default=per_class
# 확장팩 자동 감지 기능
junit.jupiter.extensions.autodetection.enabled=true
# @Disabled 무시하고 실행하기
junit.jupiter.conditions.deactivate=org.juni.*DisabledCondition
#junit.jupiter.conditions.deactivate=org.juni.*DisabledOnOs
#junit.jupiter.conditions.deactivate=org.juni.*DisabledOnJre
# 테스트 이름 표기 전략 설정
junit.jupiter.displayname.generator.default=org.junit.jupiter.api.DisplayNameGenerator$ReplaceUnderscores
```

- 확장 기능

  JUnit4의 확장 모델은 @RunWith(Runner) TestRule, MethodRule.

  JUnit5의 확장 모델은 Extension

    - @ExtendWith(xxxExtension.class) - 선언적 등록(기본 생성자로 인스턴스를 만들어서 확장성이 떨어진다.)
    - @RegisterExtension - 프로그래밍 등록
    - ServiceLoader 이용 - 자동 등록 자바

## Mockito

### Mockito 소개

Mock: 진짜 객체와 비슷하게 동작하지만 프로그래머가 직접 그 객체의 행동을 관리하는 객체

Mockito: Mock 객체를 쉽게 만들고 관리하고 검증할 수 있는 방법을 제공

[https://site.mockito.org/](https://site.mockito.org/)

대체제: EasyMock, JMock

단위 테스트에 대한 고찰 - [https://martinfowler.com/bliki/UnitTest.html](https://martinfowler.com/bliki/UnitTest.html)
컨트롤러를 호출하면 뒤에 있는 서비스, 레파지토리, 도메인 등이 하나의 행동(행위)으로 묶어서 연관된 객체들을 같이 테스트 되는 것이 맞는지, 전부 개별로 테스트를 해야 하는지..

주로 테스트 환경이 구축되어 있지 않은 외부 서비스를 Mocking

### Mockito 시작하기

spring-boot-starter-test 에 Mockito 가 포함되어 있다.

스프링 부트를 쓰지 않는다면 org.mockito:mockito-core, org.mockito:mockito-junit—jupiter 의존성을 추가해준다.

### Mock 객체 만들기

- Mockito.mock() 메서드로 생성

    ```java
    MemberService memberService = Mockito.mock(MemberService.class);
    ```

- @Mock 애너테이션으로 만드는 방법
    - JUnit5 extension으로 MockitoExtension을 사용해야 한다.
    - 필드
    - 메서드 매개변수

    ```java
    @ExtendWith(MockitoExtension.class)
    class MemberServiceTest{
        @Mock
        MemberRepository memberRepository;
    ```

    ```java
    @ExtendWith(MockitoExtension.class)
    class MemberServiceTest{
        @Test
        void test(@Mock MemberRepository memberRepository) {
            MemberService memberService = new MemberService(memberRepository);
        }
    ```


### Mock 객체 Stubbing

- 모든 Mock 객체의 행동
    - null을 리턴한다. Optional 타입은 Optional.empty를 리턴한다.
    - primitive 타입은 기본 primitive 값
    - 컬렉션은 비어있는 컬렉션
    - void 메서드는 예외를 던지지 않고 아무런 일도 발생하지 않는다.
- Mock 객체를 조작해서
    - 특정한 매개변수를 받은 경우 특정한 값을 리턴하거나 예외를 던지도록 만들 수 있다.
    - void 메서드 특정 매개변수를 받거나 호출된 경우 예외를 발생 시킬 수 있다.
    - 메서드가 동일한 매개변수로 여러번 호출될때 각기 다르게 행동하도록 조작할 수 있다.

### Mockito BDD 스타일 API

BDD(Behavior-Driven Development): 애플리케이션이 어떻게 “행동"해야 하는지에 대한 공통된 이해를 구성하는 방법으로, TDD에서 창안했다.

행동에 대한 스팩

- Title
- Narrative
    - As a / I wat / so that
- Acceptance criteria
    - Given / When / Then

Mockito는 BddMockito라는 클래스를 통해 BDD 스타일의 API를 제공한다.

## 도커와 테스트

### TestContainers

테스트에서 도커 컨테이너를 사용할 수 있는 라이브러리
[https://www.testcontainers.org/](https://www.testcontainers.org/)

- 테스트 실행 시 DB를 설정하거나 별도의 프로그램 또는 스크립트를 실행할 필요 없다.
- 보다 production에 가까운 테스트를 만들 수 있다.
- 테스트가 느려진다.

데이터베이스마다 전략(isolation, propagation 등)이 다른데 스프링은 데이터베이스의 기본 전략을 따른다. 로컬, 개발, 운영 등 환경에 따라 데이터베이스가 다르다면 차이점이 있을 수 있고 조기에 문제를 찾지 못할 수 있다.

```yaml
# Testcontainers JUnit 5 지원 모듈
testImplementation 'org.testcontainers:junit-jupiter:1.16.3'

# postgresql 모듈 - https://www.testcontainers.org/modules/databases/
testImplementation 'org.testcontainers:postgresql:1.16.3'
```

```yaml
/src/test/resources/application-test.yaml

spring:
  datasource:
    url: jdbc:tc:postgresql:///study
    username: study
    password: study
    driver-class-name: org.testcontainers.jdbc.ContainerDatabaseDriver
  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: true
    show-sql: true
```

```yaml
/src/test/resource/docker-compose.yaml

version: "3"

services:
  study-db:
    image: postgres
    ports:
      - 5432
    environment:
      POSTGRES_PASSWORD: study
      POSTGRES_USER: study
      POSTGRES_DB: study
```

```java
import com.give928.javatest.domain.Member;
import com.give928.javatest.domain.Study;
import com.give928.javatest.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@SpringBootTest
@ExtendWith({MockitoExtension.class})
@ActiveProfiles("test")
@Testcontainers
@Slf4j
@ContextConfiguration(initializers = StudyTest.ContainerPropertyInitializer.class)
class StudyTest {
    private static final DockerImageName POSTGRES_ALPINE = DockerImageName.parse("postgres");
    //    PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgresql"); // 모든 테스트마다 컨테이너를 새로 만들어서 느리다.
//    @Container
//    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer(POSTGRES_ALPINE).withDatabaseName("study");
//    @Container
//    static GenericContainer postgreSQLContainer = new GenericContainer(POSTGRES_ALPINE)
//            .withExposedPorts(5432)
//            .withEnv("POSTGRES_DB", "study")
//            .withEnv("POSTGRES_USER", "study")
//            .withEnv("POSTGRES_PASSWORD", "study");
    @ClassRule
    static DockerComposeContainer postgreSQLContainer;/* =
            new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
                    .withExposedService("study-db", 5432)
                    .withLocalCompose(true);*/

    static {
        postgreSQLContainer = new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
                .withExposedService("study-db", 5432)
                .withLocalCompose(true);
        //Mapped port can only be obtained after container is started.
        postgreSQLContainer.start();
    }

    @Mock
    private MemberService memberService;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private Environment environment;
    @Value("${container.port}")
    private int port;

    @Test
    void createNewStudy() {
        System.out.println("============================");
        System.out.println("port = " + port);
        System.out.println("============================");

        // Given
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("keesun@email.com");

        Study study = new Study(10, "테스트");

        given(memberService.findById(1L)).willReturn(Optional.of(member));

        // When
        studyService.createNewStudy(1L, study);

        // Then
        assertEquals(1L, study.getOwnerId());
        then(memberService).should(times(1)).notify(study);
        then(memberService).shouldHaveNoMoreInteractions();
    }

    @DisplayName("다른 사용자가 볼 수 있도록 스터디를 공개한다.")
    @Test
    void openStudy() {
        // Given
        StudyService studyService = new StudyService(memberService, studyRepository);
        Study study = new Study(10, "더 자바, 테스트");
        assertNull(study.getOpenedDateTime());

        // When
        studyService.openStudy(study);

        // Then
        assertEquals(StudyStatus.OPENED, study.getStatus());
        assertNotNull(study.getOpenedDateTime());
        then(memberService).should().notify(study);
    }

    static class ContainerPropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of("container.port=" + postgreSQLContainer.getServicePort("study-db", 5432))
                    .applyTo(context.getEnvironment());
        }
    }
}
```

## 성능 테스트

### ab

**[아파치 웹서버 성능검사 도구](https://httpd.apache.org/docs/2.4/ko/programs/ab.html)**

```bash
$ ab -n 1000 -c 10 http://localhost:8080/study/1                                                                                            13:33:04
This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 100 requests
Completed 200 requests
Completed 300 requests
Completed 400 requests
Completed 500 requests
Completed 600 requests
Completed 700 requests
Completed 800 requests
Completed 900 requests
Completed 1000 requests
Finished 1000 requests

Server Software:        
Server Hostname:        localhost
Server Port:            8080

Document Path:          /study/1
Document Length:        92 bytes

Concurrency Level:      10
Time taken for tests:   0.400 seconds
Complete requests:      1000
Failed requests:        0
Total transferred:      197000 bytes
HTML transferred:       92000 bytes
Requests per second:    2497.44 [#/sec] (mean)
Time per request:       4.004 [ms] (mean)
Time per request:       0.400 [ms] (mean, across all concurrent requests)
Transfer rate:          480.46 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.1      0       1
Processing:     2    4   1.7      3      15
Waiting:        2    4   1.7      3      14
Total:          2    4   1.8      3      15

Percentage of the requests served within a certain time (ms)
  50%      3
  66%      4
  75%      5
  80%      5
  90%      6
  95%      7
  98%      9
  99%     12
 100%     15 (longest request)
```

보통 99%로 성능 측정을 한다.

### JMeter

https://jmeter.apache.org/

성능 측정 및 부하(load) 테스트 기능을 제공하는 오픈 소스 자바 애플리케이션

- 주요 개념
    - Thread Group: 한 스레드 당 유저 한명
    - Sampler: 어떤 유저가 해야 하는 액션
    - Listener: 응답을 받았을 할 일(리포팅, 검증, 그래프 그리기 등)
    - Configuration: sampler 또는 Listnenr가 사용할 설정 값(쿠키, JDBC 커넥션 등)
    - Assertion: 응답이 성공적인지 확인하는 방법(응답 코드, 본문 내용 등)
- 대체제
    - Gatling - [https://gatling.io](https://gatling.io/)
    - nGrinder - 네이버가 오픈 소스 확장해서 만듬
- 테스트
    - Thread Group 만들기
        - Number of Threads: 스레드 개수, 10
        - Ramp-up period: 스레드 개수를 만드는데 소요할 시간, 1
        - Loop Count: 요청 반복 개수, 1
    - Sampler 만들기
        - HTTP Request 샘플러
            - 요청을 보낼 호스트, 포트, URI, 요청 본문 등을 설정
        - 여러 샘플러를 순차적으로 등록하는 것도 가능하다.
    - Listener 만들기
        - View Result Tree
        - View Results in Table
        - Summary Report
        - Aggregate Report
        - Response Time Graph
        - Graph Results
        - ...
    - Assertion 만들기
        - 응답 코드 확인
        - 응답 본문 확인
    - CLI 사용하기
        - jmeter -n -t 설정파일 -l 리포트파일
        - jmeter -n -t StudySampleAggregateReport.jmx
- BlazeMeter
    - 크롬 액션을 녹화해서 jmeter 탬플릿으로 저장해서 테스트. 크롬 확장팩.

## 운영 이슈 테스트

### Chaos Monkey

카오스 엔지니어링 툴

- 프로덕션 환경, 특히 분산 시스템 환경에서 불확실성을 파악하고 해결 방안을 모색하는데 사용하는 툴

운영 환경 불확실성의 예

- 네트워크 지연
- 서버 장애
- 디스크 오작동
- 메모리 누수
- ...

카오스 멍키 스프링 부트

- 스프링 부트 애플리케이션에 카오스 멍키를 손쉽게 적용해 볼 수 있는 툴
- 즉, 스프링 부트 애플리케이션을 망가트릴 수 있는 툴

의존성 추가

```yaml
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'de.codecentric:chaos-monkey-spring-boot:2.5.4'
```

카오스 멍키 활성화

- spring.profiles.active=chaos-monkey

```yaml
spring:
  profiles:
    active: chaos-monkey
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
    show-sql: true
  datasource:
    url: jdbc:postgresql://localhost:5432/study
    username: study
    password: study
#    url: jdbc:mysql://localhost:3306/test
#    username: test
#    password: test1234

management:
  endpoint:
    chaosmonkey:
      enabled: true
  endpoints:
    web:
      exposure:
        include:
          - health
          - info
          - chaosmonkey
```

### 카오스 멍키 스프링 부트로 응답 지연

응답 지연 이슈 재현 방법

1. Repository Watcher 활성화 - chaos.monkey.watcher.repository=true
2. 카오스 멍키 활성화 - http post localhost:8080/actuator/chaosmonkey/enable
3. 카오스 멍키 활성화 확인 - http localhost:8080/actuator/chaosmonkey/status
4. 카오스 멍치 와처 확인 - http localhost:8080/actuator/chaosmonkey/watchers
5. 카오스 멍키 지연 공격 설정 - http POST [localhost:8080/actuator/chaosmonkey/assaults](http://localhost:8080/actuator/chaosmonkey/assaults) level=3 latencyRangeStart=2000 latencyRangeEnd=5000 latencyActive=true
   level=3 3번 요청 중 1번 적용(2번은 정상, 1번은 이상), level=10 10번 중 1번 적용

설정 확인 - http [localhost:8080/actuator/chaosmonkey/assaults](http://localhost:8080/actuator/chaosmonkey/assaults)

설정 참고 - [https://codecentric.github.io/chaos-monkey-spring-boot/latest/#_customize_watcher](https://codecentric.github.io/chaos-monkey-spring-boot/latest/#_customize_watcher)

### 에러 발생 재현

http POST [localhost:8080/actuator/chaosmonkey/assaults](http://localhost:8080/actuator/chaosmonkey/assaults) level=3 latencyActive=false exceptionsActive=true exception.type=java.lang.RuntimeException

## 아키텍처 테스트

### Archunit

[https://www.archunit.org/](https://www.archunit.org/)

애플리케이션의 아키텍처를 테스트 할 수 있는 오픈 소스 라이브러리로 패키지, 클래스, 레이어, 슬라이스간의 의존성을 확인할 수 있는 기능을 제공한다.

아키텍처 테스트 유즈 케이스

- A 라는 패키지가 B(또는 C, D) 패키지에서만 사용되고 있는지 확인 가능
- *Service라는 이름의 클래스들이 *Controller 또는 *Service라는 이름의 클래스에서만 참조하고 있는지 확인
- *Service라는 이름의 클래스들이 ..service.. 라는 패키지에 들어있는지 확인
- A라는 애너테이션을 선언한 메서드만 특정 패키지 또는 특정 애너테이션을 가진 클래스를 호출하고 있는지 확인
- 특정한 스타일의 아키텍처를 따르고 있는지 확인

패키지 또는 클래스간에 서큘러 디팬던시는 없는게 좋다. 코드를 분석하기 어렵다.

```yaml
testImplementation 'com.tngtech.archunit:archunit-junit5-engine:0.23.1'
```

주요 사용법

1. 특정 패키지에 해당하는 클래스를 (바이트코드를 통해) 읽어들이고
2. 확인할 규칙을 정의하고
3. 읽어들인 클래스들이 그 규칙을 잘 따르는지 확인한다.

```java
@Test
public void services_should_only_be_accessed_by_controllers() {
    JavaClasses importedClasses = new ClassFileImporter().importPackages("com.mycompany.myapp");

    ArchRule myrule = classes().that().resideInAPackage("..service..")
            .shoude().onlyBeAccessed().byAnyPackage("..controller..", "..service..");

    myRule.check(importedClasses);
}
```

JUnit5 확장팩 제공

- @AnalyzeClasses: 클래스를 읽어들여서 확인할 패키지 설정
- @ArchTest: 확인할 규칙 정의

[https://www.archunit.org/userguide/html/000_Index.html#_what_to_check](https://www.archunit.org/userguide/html/000_Index.html#_what_to_check)

## 기타

- Selenium WebDrive
  웹 브라우저 기반 자동화된 테스트 작성에 사용할 수 있는 툴
- DBUnit
  데이터베이스에 데이터를 CVS, Excel 등으로 넣어주는 툴
- REST Assured
  REST API 테스트 라이브러리
- Cucumber
  BDD를 지원하는 테스트 라이브러리
