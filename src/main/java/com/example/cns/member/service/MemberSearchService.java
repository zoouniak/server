package com.example.cns.member.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.company.domain.Company;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.CustomMemberRepository;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.member.dto.response.MemberSearchResponse;
import com.example.cns.member.dto.response.MemberSearchResponseWithChatParticipation;
import com.example.cns.member.type.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberSearchService {
    private final MemberRepository memberRepository;
    private final CustomMemberRepository customMemberRepository;

    private static List<MemberSearchResponse> getMemberSearchResponses(List<Member> memberList) {
        return memberList.stream()
                .map(member -> MemberSearchResponse.builder()
                        .memberId(member.getId())
                        .nickname(member.getNickname())
                        .url(member.getUrl())
                        .build())
                .collect(Collectors.toList());
    }

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

    /*
     * 파라미터로 시작하는 닉네임을 가진 사용자를 찾는다.
     * Params: nickname
     * Returns: List<MemberSearchResponse>
     */
    @Transactional(readOnly = true)
    public List<MemberSearchResponse> findMemberContainsNickname(String nickname) {
        List<Member> memberList = memberRepository.findAllByNicknameStartsWith(nickname);

        return getMemberSearchResponses(memberList);
    }

    /*
     * 사용자의 회사 내에서 파라미터로 시작하는 닉네임을 가진 사용자를 찾는다.
     * Params: nickname
     * Returns: List<MemberSearchResponse>
     */
    @Transactional(readOnly = true)
    public List<MemberSearchResponse> findMemberContainsNicknameInSameCompany(Long memberId, String nickname) {
        Company company = findMemberById(memberId).getCompany();
        List<Member> memberList = memberRepository.findAllByNicknameStartsWithAndCompany(nickname, company);

        return getMemberSearchResponses(memberList);

    }

    /*
     * 사용자의 회사 내에서 파라미터로 시작하는 닉네임을 가진 사용자를 찾는다
     * 단 채팅방 초대를 위한 api로 해당 채팅방의 참여여부도 함꼐 조회한다
     * Params : {
     *  memberId : 검색을 요청한 사용자 아이디,
     *  roomId: 채팅방 아이디,
     *  nickname : 닉네임
     *  }
     * Returns : List<MemberSearchResponseWithChatParticipation>

     */
    @Transactional(readOnly = true)
    public List<MemberSearchResponseWithChatParticipation> findMemberWithChatParticipationByNickname(Long memberId, Long roomId, String nickname) {
        Company company = findMemberById(memberId).getCompany();
        return customMemberRepository.searchMembersInSameCompanyWithChatParticipation(company.getId(), roomId, nickname);
    }
}
