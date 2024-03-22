package com.example.cns.member.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.member.type.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public boolean isExistByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean isExistByUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public Member findByUserName(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    public RoleType findRole(Long memberId) {
        return findById(memberId).getRole();
    }

}
