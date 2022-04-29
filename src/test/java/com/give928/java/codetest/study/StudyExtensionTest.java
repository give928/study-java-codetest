package com.give928.java.codetest.study;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;

@Slf4j
//@ExtendWith(FindSlowTestExtension.class) // 확장 기능 선언적 등록 - 기본 생성자로 인스턴스를 만들어서 확장성이 떨어진다.
class StudyExtensionTest {
    // 확장 기능 프로그래밍 등록
    @RegisterExtension
    static FindSlowTestExtension findSlowTestExtension = new FindSlowTestExtension(1000L);

    @Test
    void test_1() {
        System.out.println("StudyExtensionTest.test_1");
    }

    @SlowTest
    void test_2() throws InterruptedException {
        Thread.sleep(1005L);
        System.out.println("StudyExtensionTest.test_2");
    }

    @Test
    void test_3() throws InterruptedException {
        Thread.sleep(1005L);
        System.out.println("StudyExtensionTest.test_3");
    }

    @BeforeAll
    static void beforeAll() {
        log.debug("StudyExtensionTest.beforeAll");
    }

    @AfterAll
    static void afterAll() {
        log.debug("StudyExtensionTest.afterAll");
    }

    @BeforeEach
    void beforeEach() {
        log.debug("StudyExtensionTest.beforeEach");
    }

    @AfterEach
    void afterEach() {
        log.debug("StudyExtensionTest.afterEach");
    }
}
