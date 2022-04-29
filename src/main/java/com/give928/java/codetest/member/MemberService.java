package com.give928.java.codetest.member;

import com.give928.java.codetest.domain.Member;
import com.give928.java.codetest.domain.Study;

import java.util.Optional;

public interface MemberService {
    Optional<Member> findById(Long memberId) throws MemberNotFoundException;

    void validate(Long memberId) throws InvalidMemberException;

    void notify(Study study);

    void notify(Member member);
}
