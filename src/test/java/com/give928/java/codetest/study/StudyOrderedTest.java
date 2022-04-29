package com.give928.java.codetest.study;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

@Slf4j
//@TestInstance(TestInstance.Lifecycle.PER_CLASS) // properties 파일에 설정
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudyOrderedTest {
    private int index = 1;

    @Test
    @Order(3)
    void test_1() {
        System.out.println("StudyOrderedTest.test_1: " + index++);
    }

    @Test
    @Order(2)
    void test_2() {
        System.out.println("StudyOrderedTest.test_2: " + index++);
    }

    @Test
    @Order(1)
    void test_3() {
        System.out.println("StudyOrderedTest.test_3: " + index++);
    }

    @BeforeAll
    void beforeAll() {
        log.debug("StudyOrderedTest.beforeAll");
    }

    @AfterAll
    void afterAll() {
        log.debug("StudyOrderedTest.afterAll");
    }

    @BeforeEach
    void beforeEach() {
        log.debug("StudyOrderedTest.beforeEach");
    }

    @AfterEach
    void afterEach() {
        log.debug("StudyOrderedTest.afterEach");
    }
}
