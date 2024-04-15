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
public class MemberSearchService {
    private final MemberRepository memberRepository;

    /*
     * 사용자를 저장한다.
     * Params: member
     * Returns: saved Member entity
     */
    @Transactional
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    /*
     * 아이디로 사용자를 찾는다.
     * Params: nickname
     * Returns: find Member entity
     * Throws: BusinessException - if Member is null
     */
    @Transactional(readOnly = true)
    public Member findMemberByNickName(String nickname) {
        return memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    /*
     * pk로 사용자를 찾는다.
     * Params: memberId
     * Returns: find Member entity
     * Throws: BusinessException - if Member is null
     */
    @Transactional(readOnly = true)
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    /*
     * 이메일로 사용자를 찾는다.
     * Params: email
     * Returns: find Member entity
     * Throws: BusinessException - if Member is null
     */
    @Transactional(readOnly = true)
    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    /*
     * 사용자의 role을 찾는다
     * Params: memberId
     * Returns: RoleType
     */
    @Transactional(readOnly = true)
    public RoleType findRole(Long memberId) {
        return findMemberById(memberId).getRole();
    }

    /*
     * 이메일을 사용하고 있는 사용자가 존재하는 지 확인한다.
     * Params: email
     * Returns: boolean
     */
    @Transactional(readOnly = true)
    public boolean isExistByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    /*
     * 닉네임을 사용하고있는 사용자가 존재하는 지 확인한다.
     * Params: nickname
     * Returns: boolean
     */
    @Transactional(readOnly = true)
    public boolean isExistByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }
}
