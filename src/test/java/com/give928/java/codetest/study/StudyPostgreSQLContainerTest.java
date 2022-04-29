package com.give928.java.codetest.study;

import com.give928.java.codetest.domain.Member;
import com.give928.java.codetest.domain.Study;
import com.give928.java.codetest.domain.StudyStatus;
import com.give928.java.codetest.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
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
class StudyPostgreSQLContainerTest {
    private static final DockerImageName DATABASE_IMAGE = DockerImageName.parse("postgres");

//    PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer(DATABASE_IMAGE); // 모든 테스트마다 컨테이너를 새로 만들어서 느리다.

    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer(DATABASE_IMAGE)
            .withDatabaseName("codetest");

    @Mock
    private MemberService memberService;
    @Autowired
    private StudyRepository studyRepository;

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setUp() {
        studyRepository.deleteAll();
    }

    @AfterAll
    static void afterAll() {
        postgreSQLContainer.stop();
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
