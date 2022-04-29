package com.give928.java.codetest.study;

import com.give928.java.codetest.member.MemberService;
import com.give928.java.codetest.domain.Member;
import com.give928.java.codetest.domain.Study;
import com.give928.java.codetest.domain.StudyStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class StudyServiceTest {
    @Mock
    MemberService memberService;

    @Mock
    StudyRepository studyRepository;

    @Test
    void createStudyService1() {
        MemberService memberService = mock(MemberService.class);
        StudyRepository studyRepository = mock(StudyRepository.class);

        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);
    }

    @Test
    void createStudyService2(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);
    }

    @Test
    void createStudyService3() {
        StudyService studyService = new StudyService(memberService, studyRepository);

        assertNotNull(studyService);
    }

    @Test
    void thenReturn() {
        StudyService studyService = new StudyService(memberService, studyRepository);

        assertNotNull(studyService);

        Member member = Member.builder()
                .id(1L)
                .email("give928@gmail.com")
                .build();

//        when(memberService.findById(1L)).thenReturn(Optional.of(member));
        when(memberService.findById(any())).thenReturn(Optional.of(member));

        Optional<Member> optionalMember = memberService.findById(1L);
        assertEquals("give928@gmail.com", optionalMember.get().getEmail());
    }

    @Test
    void doThrowTest() {
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        doThrow(new IllegalArgumentException()).when(memberService).validate(1L);

        assertThrows(IllegalArgumentException.class, () -> {
            memberService.validate(1L);
        });

        memberService.validate(2L);
    }

    @Test
    void thenReturnThrowEmpty() {
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = Member.builder()
                .id(1L)
                .email("give928@gmail.com")
                .build();

        when(memberService.findById(any()))
                .thenReturn(Optional.of(member))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.empty());

        Optional<Member> optionalMember = memberService.findById(1L);
        assertEquals("give928@gmail.com", optionalMember.get().getEmail());

        assertThrows(RuntimeException.class, () -> {
            memberService.findById(2L);
        });

        assertEquals(Optional.empty(), memberService.findById(3L));
    }

    @Test
    void verifyAndOrder() {
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = Member.builder()
                .id(1L)
                .email("give928@gmail.com")
                .build();

        Study study = new Study(10, "java");

        when(memberService.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.save(study)).thenReturn(study);

        studyService.createNewStudyAndNotifyAll(1L, study);

        assertEquals(member.getId(), study.getOwnerId());

        verify(memberService, times(1)).notify(study);
        verify(memberService, times(1)).notify(member);
        verify(memberService, never()).validate(any());
        then(memberService).shouldHaveNoMoreInteractions();

        InOrder inOrder = inOrder(memberService);
        inOrder.verify(memberService).notify(study);
        inOrder.verify(memberService).notify(member);
    }

    @Test
    void bddStyle() {
        // Given
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = Member.builder()
                .id(1L)
                .email("give928@gmail.com")
                .build();

        Study study = new Study(10, "java");

        given(memberService.findById(1L)).willReturn(Optional.of(member));
        given(studyRepository.save(study)).willReturn(study);

        // When
        studyService.createNewStudyAndNotifyAll(1L, study);

        // Then
        assertEquals(member.getId(), study.getOwnerId());

        then(memberService).should(times(1)).notify(study);
        then(memberService).should(times(1)).notify(member);
        then(memberService).should(never()).validate(any());
        then(memberService).shouldHaveNoMoreInteractions();

        InOrder inOrder = inOrder(memberService);
        then(memberService).should(inOrder).notify(study);
        then(memberService).should(inOrder).notify(member);
    }

    @Test
    void openStudy() {
        // Given
        StudyService studyService = new StudyService(memberService, studyRepository);

        Study study = new Study(10, "java");
        assertNull(study.getOpenedDateTime());

        given(studyRepository.save(study)).willReturn(study);

        // When
        studyService.openStudy(study);

        // Then
        Assertions.assertEquals(StudyStatus.OPENED, study.getStatus());
        assertNotNull(study.getOpenedDateTime());
        then(memberService).should().notify(study);
    }

}
