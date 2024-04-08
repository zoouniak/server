package com.example.cns.auth.service;

import com.example.cns.auth.domain.RefreshToken;
import com.example.cns.auth.dto.request.LoginRequest;
import com.example.cns.auth.dto.request.PasswordResetRequest;
import com.example.cns.auth.dto.request.SignUpRequest;
import com.example.cns.auth.dto.response.AuthTokens;
import com.example.cns.auth.dto.response.NicknameInquiryResponse;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.exception.MemberVerificationRequest;
import com.example.cns.common.security.exception.AuthException;
import com.example.cns.common.security.jwt.dto.JwtMemberInfo;
import com.example.cns.common.security.jwt.provider.JwtProvider;
import com.example.cns.company.domain.Company;
import com.example.cns.company.service.CompanySearchService;
import com.example.cns.member.domain.Member;
import com.example.cns.member.service.MemberSearchService;
import com.example.cns.member.type.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAuthService {
    private final MemberSearchService memberSearchService;
    private final CompanySearchService companySearchService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public void register(SignUpRequest request) {
        checkMemberInput(request.nickname(), request.email());
        Member requestMember = request.toEntity(passwordEncoder);

        if (requestMember.getRole() == RoleType.EMPLOYEE) {
            Company company = companySearchService.findByCompanyName(request.companyName());
            verifyEmail(company.getEmail(), request.email());
            requestMember.enrollCompany(company);
        }

        memberSearchService.saveMember(requestMember);
    }

    public AuthTokens login(LoginRequest request) {
        Member member = memberSearchService.findMemberByNickName(request.nickname());

        if (passwordEncoder.matches(request.password(), member.getPassword())) {
            JwtMemberInfo memberInfo = new JwtMemberInfo(member.getId(), member.getRole());

            return jwtProvider.generateLoginToken(memberInfo);
        }
        throw new AuthException(ExceptionCode.INVALID_PASSWORD);
    }

    public boolean hasDuplicateNickname(String nickname) {
        return memberSearchService.isExistByNickname(nickname);
    }

    public AuthTokens refreshTokens(final String refreshTokenReq) {
        if (!jwtProvider.isTokenExpired(refreshTokenReq)) {
            RefreshToken refreshToken = refreshTokenService.findById(refreshTokenReq);

            return jwtProvider.generateLoginToken(refreshToken.getUserInfo());
        }
        throw new AuthException(ExceptionCode.EXPIRED_TOKEN);
    }

    public NicknameInquiryResponse findNickname(String email) {
        Member member = memberSearchService.findMemberByEmail(email);
        return new NicknameInquiryResponse(member.getNickname());
    }

    public void verifyMember(MemberVerificationRequest request) {
        Member member = memberSearchService.findMemberByNickName(request.nickname());
        if (!isEqual(member.getFirstName(), request.firstName()) || !isEqual(member.getLastName(), request.lastName()))
            throw new AuthException(ExceptionCode.INCORRECT_INFO);
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        String newPassword = request.toEncodePassword(passwordEncoder);

        Member findMember = memberSearchService.findMemberByEmail(request.email());
        findMember.resetPassword(newPassword);
    }

    private void checkMemberInput(String inputNickname, String inputEmail) {
        if (memberSearchService.isExistByEmail(inputEmail))
            throw new AuthException(ExceptionCode.DUPLICATE_EMAIL_EXISTS);
        if (memberSearchService.isExistByNickname(inputNickname))
            throw new AuthException(ExceptionCode.DUPLICATE_USERNAME_EXISTS);
    }

    private void verifyEmail(String companyEmail, String email) {
        String domain = email.split("@")[1];
        if (!companyEmail.equals(domain))
            throw new AuthException(ExceptionCode.INVALID_EMAIL_FORMAT);
    }

    private boolean isEqual(String expect, String input) {
        return expect.equals(input);
    }
}
