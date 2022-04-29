package com.give928.java.codetest.study;

import com.give928.java.codetest.domain.Member;
import com.give928.java.codetest.domain.Study;
import com.give928.java.codetest.domain.StudyStatus;
import com.give928.java.codetest.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

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
class StudyGenericContainerTest {
    private static final DockerImageName DATABASE_IMAGE = DockerImageName.parse("postgres");

    @ClassRule
    private static final GenericContainer postgreSQLContainer;

    static {
        postgreSQLContainer = new GenericContainer(DATABASE_IMAGE)
                .withExposedPorts(5432)
                .withLogConsumer(new Slf4jLogConsumer(log))
                .withEnv("POSTGRES_DB", "codetest")
                .withEnv("POSTGRES_USER", "codetest")
                .withEnv("POSTGRES_PASSWORD", "codetest");
    }

    @Mock
    private MemberService memberService;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private Environment environment;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("container.port", () -> postgreSQLContainer.getMappedPort(5432));
    }

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgreSQLContainer.stop();
    }

    @BeforeEach
    void setUp() {
        log.info("postgreSQLContainer.getMappedPort(5432) = {}", postgreSQLContainer.getMappedPort(5432));
        log.info("environment.getProperty(\"container.port\") = {}", environment.getProperty("container.port"));
        System.out.println("================================================================");
        System.out.println(postgreSQLContainer.getLogs());
        System.out.println("================================================================");

        studyRepository.deleteAll();
    }

    @Test
    void createNewStudy() {
        // Given
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("give928@gmail.com");

        Study study = new Study(10, "테스트");

        given(memberService.findById(1L)).willReturn(Optional.of(member));

        // When
        studyService.createNewStudy(1L, study);

        // Then
        assertEquals(1L, study.getOwnerId());
        then(memberService).should(times(1)).notify(study);
        then(memberService).shouldHaveNoMoreInteractions();
    }

    @Test
    void openStudy() {
        // Given
        StudyService studyService = new StudyService(memberService, studyRepository);
        Study study = new Study(10, "더 자바, 테스트");
        assertNull(study.getOpenedDateTime());

        // When
        studyService.openStudy(study);

        // Then
        Assertions.assertEquals(StudyStatus.OPENED, study.getStatus());
        assertNotNull(study.getOpenedDateTime());
        then(memberService).should().notify(study);
    }
}
