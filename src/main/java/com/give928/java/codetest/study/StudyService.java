package com.give928.java.codetest.study;

import com.give928.java.codetest.member.MemberService;
import com.give928.java.codetest.domain.Member;
import com.give928.java.codetest.domain.Study;

import java.util.Optional;

public class StudyService {
    private final MemberService memberService;
    private final StudyRepository repository;

    public StudyService(MemberService memberService, StudyRepository repository) {
        assert memberService != null;
        assert repository != null;
        this.memberService = memberService;
        this.repository = repository;
    }

    public Study createNewStudyAndNotifyAll(Long memberId, Study study) {
        Optional<Member> member = memberService.findById(memberId);
        Study newstudy = saveStudy(memberId, study, member);
        memberService.notify(member.get());
        return newstudy;
    }

    private Study saveStudy(Long memberId, Study study, Optional<Member> member) {
        if (member.isPresent()) {
            study.setOwnerId(memberId);
        } else {
            throw new IllegalArgumentException("Member doesn't exist for id: '" + memberId + "'");
        }
        Study newstudy = repository.save(study);
        memberService.notify(newstudy);
        return newstudy;
    }

    public Study createNewStudy(Long memberId, Study study) {
        Optional<Member> member = memberService.findById(memberId);
        return saveStudy(memberId, study, member);
    }

    public Study openStudy(Study study) {
        study.open();
        Study openedStudy = repository.save(study);
        memberService.notify(openedStudy);
        return openedStudy;
    }
}
