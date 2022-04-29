package com.give928.java.codetest.study;

import com.give928.java.codetest.domain.Study;
import com.give928.java.codetest.domain.StudyStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudyJUnitTest {
    @Test
    @DisplayName("스터디 만들기")
    void create_new_study() {
        log.debug("StudyTest.create_new_study");
        Study study = new Study(1);
        assertAll(
                () -> assertNotNull(study),
                () -> assertEquals(StudyStatus.DRAFT, study.getStatus(),
                                   () -> "스터디를 처음 만들면 " + StudyStatus.DRAFT + " 상태다.."),
                () -> assertTrue(study.getLimitCount() > 0, "스터디 최대 참석 가능 인원은 0보다 커야 한다.")
        );
    }

    @Test
    @DisplayName("스터디 만들기 최대 참석 인원이 0보다 작은 경우 예외 발생 😱")
    void throwsCreateStudy() {
        log.debug("StudyTest.throws");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                                                         () -> new Study(-10));
        String message = exception.getMessage();
        assertEquals("limit은 0보다 커야 한다.", exception.getMessage());
    }

    @Test
    @DisplayName("스터디 만들기 타임아웃 종료 대기 😱")
    void timeout() {
        log.debug("StudyTest.timeout");
        assertTimeout(Duration.ofMillis(100), () -> {
            Study study = new Study(10);
//            Thread.sleep(300);
            assertEquals(StudyStatus.DRAFT, study.getStatus(), () -> "스터디를 처음 만들면 " + StudyStatus.DRAFT + " 상태다..");
        });
    }

    @Test
    @DisplayName("스터디 만들기 타임아웃 즉시 종료 😱")
    void timeout_preemptively() {
        log.debug("StudyTest.timeout_preemptively");
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            Study study = new Study(10);
//            Thread.sleep(300);
            assertEquals(StudyStatus.DRAFT, study.getStatus(), () -> "스터디를 처음 만들면 " + StudyStatus.DRAFT + " 상태다..");
        });
        // ThreadLocal - assertTimeoutPreemptively 주의해서 사용
        // 스프링 트랜잭션 설정을 가지고 있는 스레드와 별개의 스레드로 테스트 메서드가 실행되서 롤백이 안될 수 있다.
    }

    @Test
    @DisplayName("스터디 만들기 로컬 환경에서만 테스트1")
    void local1() {
        log.debug("StudyTest.local1");
        String activeProfile = System.getenv("ACTIVE_PROFILE");
        log.debug("activeProfile = " + activeProfile);
        assumeTrue("LOCAL".equalsIgnoreCase(activeProfile)); // 프로파일이 로컬일때만 아래 라인 실행

        Study study = new Study(10);
        assertThat(study.getLimitCount()).isPositive();
    }

    @Test
    @DisplayName("스터디 만들기 로컬 환경에서만 테스트2")
    void local2() {
        String activeProfile = System.getenv("ACTIVE_PROFILE");
        log.debug("activeProfile = " + activeProfile);
        assumingThat("LOCAL".equalsIgnoreCase(activeProfile), () -> {
            log.debug("StudyTest.local2");
            Study study = new Study(10);
            assertThat(study.getLimitCount()).isPositive();
        });
    }

    @Test
    @DisplayName("로컬 환경에서만 테스트3")
    @EnabledIfEnvironmentVariable(named = "ACTIVE_PROFILE", matches = "LOCAL")
    void local3() {
        log.debug("StudyTest.local3");
    }

    @Test
    @DisplayName("맥, 리눅스 테스트")
    @EnabledOnOs({OS.MAC, OS.LINUX})
    void on_mac() {
        log.debug("StudyTest.on_mac");
    }

    @Test
    @DisplayName("맥 제외하고 테스트")
    @DisabledOnOs(OS.MAC)
    void on_windows() {
        log.debug("StudyTest.on_windows");
    }

    @Test
    @DisplayName("Java 8 ~ 11 테스트")
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_9, JRE.JAVA_10, JRE.JAVA_11})
    void on_java_8_to_11() {
        log.debug("StudyTest.on_mac");
    }

    @Test
    @DisplayName("local")
    @Tag("local") // edit configuration - Tags - local 로 실행하면 이 태그가 달린 테스트만 실행
    void tag_local() {
        log.debug("StudyTest.local");
    }

    @Test
    @DisplayName("dev")
    @Tag("dev")
    void tag_dev() {
        log.debug("StudyTest.tag_dev");
    }

    @DisplayName("커스텀 태그(local)")
    @LocalTest
    void custom_tag() {
        log.debug("StudyTest.custom_tag");
    }

    @DisplayName("RepeatedTest")
    @RepeatedTest(value = 3, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    void repeated_test(RepetitionInfo repetitionInfo) {
        log.debug("StudyTest.repeated_test: " + repetitionInfo.getCurrentRepetition() + "/" + repetitionInfo.getTotalRepetitions());
    }

    @DisplayName("ParameterizedTest ValueSource")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(strings = {"테스트1", "테스트2", "테스트3", "테스트4"})
    void value_source_parameterized_test(String message) {
        log.debug("StudyTest.value_source_parameterized_test: " + message);
    }

    @DisplayName("ParameterizedTest ValueSource EmptySource NullSource")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(strings = {"테스트1", "테스트2", "테스트3", "테스트4"})
    @EmptySource
    @NullSource
    void value_empty_null_source_parameterized_test(String message) {
        log.debug("StudyTest.value_empty_null_source_parameterized_test: " + message);
    }

    @DisplayName("ParameterizedTest ValueSource ConvertWith")
    @ParameterizedTest(name = "{index} {displayName} limit={0}")
    @ValueSource(ints = {10, 20, 40})
    void value_source_convert_with_parameterized_test(@ConvertWith(StudyLimitConverter.class) Study study) {
        log.debug("StudyTest.value_source_convert_with_parameterized_test: " + study.getLimitCount());
    }

    static class StudyLimitConverter extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "Can only convert to Study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }

    @DisplayName("ParameterizedTest CsvSource")
    @ParameterizedTest(name = "{index} {displayName} limit={0}, name={1}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void csv_source_parameterized_test(Integer limit, String name) {
        Study study = new Study(limit, name);
        log.debug("StudyTest.csv_source_parameterized_test: " + study);
    }

    @DisplayName("ParameterizedTest CsvSource ArgumentsAccessor")
    @ParameterizedTest(name = "{index} {displayName} limit={0}, name={1}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void csv_source_arguments_accessor_parameterized_test(ArgumentsAccessor argumentsAccessor) {
        Study study = new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        log.debug("StudyTest.csv_source_arguments_accessor_parameterized_test: " + study);
    }

    @DisplayName("ParameterizedTest CsvSource AggregateWith")
    @ParameterizedTest(name = "{index} {displayName} limit={0}, name={1}")
    @CsvSource({"10, '자바 스터디'", "20, 스프링"})
    void csv_source_aggregate_with_parameterized_test(@AggregateWith(StudyAggregator.class) Study study) {
        log.debug("StudyTest.csv_source_aggregate_with_parameterized_test: " + study);
    }

    // public class 만 static inner class 로 구현해야
    static class StudyAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
            return new Study(accessor.getInteger(0), accessor.getString(1));
        }
    }

    @Test
    @DisplayName("비활성")
    @Disabled
    void disabled() {
        log.debug("StudyTest.disabled");
    }

    @BeforeAll
    static void beforeAll() {
        log.debug("StudyTest.beforeAll");
    }

    @AfterAll
    static void afterAll() {
        log.debug("StudyTest.afterAll");
    }

    @BeforeEach
    void beforeEach() {
        log.debug("StudyTest.beforeEach");
    }

    @AfterEach
    void afterEach() {
        log.debug("StudyTest.afterEach");
    }
}
