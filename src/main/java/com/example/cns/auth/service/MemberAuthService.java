package com.example.cns.auth.service;

import com.example.cns.auth.domain.RefreshToken;
import com.example.cns.auth.dto.request.LoginRequest;
import com.example.cns.auth.dto.request.SignUpRequest;
import com.example.cns.auth.dto.response.AuthTokens;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.security.exception.AuthException;
import com.example.cns.common.security.jwt.dto.JwtUserInfo;
import com.example.cns.common.security.jwt.provider.JwtProvider;
import com.example.cns.company.domain.Company;
import com.example.cns.company.service.CompanySearchService;
import com.example.cns.member.domain.Member;
import com.example.cns.member.service.MemberService;
import com.example.cns.member.type.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberAuthService {
    private final MemberService memberService;
    private final CompanySearchService companySearchService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public void register(SignUpRequest dto) {
        verifyMember(dto.username(), dto.email());
        Member requestMember = dto.toEntity(passwordEncoder);

        if (requestMember.getRole() == RoleType.EMPLOYEE) {
            Company company = companySearchService.findByCompanyName(dto.companyName());
            verifyEmail(company.getEmail(), dto.email());
            requestMember.enrollCompany(company);
        }

        memberService.saveMember(requestMember);
    }

    public AuthTokens login(LoginRequest dto) {
        Member member = memberService.findMemberByUserName(dto.username());

        if (passwordEncoder.matches(dto.password(), member.getPassword())) {
            JwtUserInfo userInfo = new JwtUserInfo(member.getId(), member.getRole());

            return jwtProvider.generateLoginToken(userInfo);
        }
        throw new AuthException(ExceptionCode.INVALID_PASSWORD);
    }

    public boolean checkDuplicateUsername(String username) {
        return memberService.isExistByUsername(username);
    }

    public AuthTokens refresh(final String refreshTokenReq) {
        if (!jwtProvider.isTokenExpired(refreshTokenReq)) {
            RefreshToken refreshToken = refreshTokenService.findById(refreshTokenReq);

            return jwtProvider.generateLoginToken(refreshToken.getUserInfo());
        }
        throw new AuthException(ExceptionCode.EXPIRED_TOKEN);
    }

    private void verifyMember(String username, String email) {
        if (memberService.isExistByEmail(email))
            throw new AuthException(ExceptionCode.DUPLICATE_EMAIL_EXISTS);
        if (memberService.isExistByUsername(username))
            throw new AuthException(ExceptionCode.DUPLICATE_USERNAME_EXISTS);
    }

    private void verifyEmail(String companyEmail, String userEmail) {
        String domain = userEmail.split("@")[1];
        if (!companyEmail.equals(domain))
            throw new AuthException(ExceptionCode.EMAIL_FORMAT_INVALID);
    }

}
