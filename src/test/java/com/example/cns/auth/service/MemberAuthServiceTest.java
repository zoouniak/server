package com.example.cns.auth.service;

import com.example.cns.auth.domain.RefreshToken;
import com.example.cns.auth.dto.request.LoginRequest;
import com.example.cns.auth.dto.request.PasswordResetRequest;
import com.example.cns.auth.dto.request.SignUpRequest;
import com.example.cns.auth.dto.response.AuthTokens;
import com.example.cns.auth.dto.response.NicknameInquiryResponse;
import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.security.exception.AuthException;
import com.example.cns.common.security.jwt.dto.JwtMemberInfo;
import com.example.cns.common.security.jwt.provider.JwtProvider;
import com.example.cns.company.domain.Company;
import com.example.cns.company.service.CompanySearchService;
import com.example.cns.member.domain.Member;
import com.example.cns.member.service.MemberSearchService;
import com.example.cns.member.type.RoleType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberAuthServiceTest {
    @InjectMocks
    private MemberAuthService sut;
    @Mock
    private MemberSearchService memberService;
    @Mock
    private CompanySearchService companySearchService;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Spy
    private PasswordEncoder passwordEncoder;

    private static Member createMember() {
        return new Member(1L, "test", "testpassword", "email", "first", "second", LocalDate.now(), RoleType.EMPLOYEE, "");
    }

    @Test
    void 회원가입이_가능하다() {
        // 준비
        SignUpRequest req = createSignUpReq("test1@cns.com");
        when(memberService.isExistByNickname(req.nickname())).thenReturn(false);
        when(memberService.isExistByEmail(req.email())).thenReturn(false);
        when(companySearchService.findByCompanyName(req.companyName())).thenReturn(new Company(1L, "cns", "cns.com","field"));

        // 실행
        sut.register(req);

        // 검증
        verify(memberService).saveMember(any());
    }

    @Test
    void 중복된_이메일이_존재할_경우_회원가입이_불가능하다() {
        SignUpRequest req = createSignUpReq("test1@cns.com");
        when(memberService.isExistByEmail(req.email())).thenReturn(true);

        Assertions.assertThrows(AuthException.class,
                () -> sut.register(req));
    }

    @Test
    void 회사_이메일이_아니면_회원가입이_불가능하다() {
        SignUpRequest req = createSignUpReq("test1@naver.com");
        when(memberService.isExistByNickname(req.nickname())).thenReturn(false);
        when(memberService.isExistByEmail(req.email())).thenReturn(false);
        when(companySearchService.findByCompanyName(req.companyName())).thenReturn(new Company(1L, "cns", "cns.com","field"));

        Assertions.assertThrows(AuthException.class,
                () -> sut.register(req));
    }

    @Test
    void 중복된_아이디가_존재할_경우_회원가입이_불가능하다() {
        SignUpRequest req = createSignUpReq("test1@cns.com");
        when(memberService.isExistByNickname(req.nickname())).thenReturn(true);
        when(memberService.isExistByEmail(req.email())).thenReturn(false);

        Assertions.assertThrows(AuthException.class,
                () -> sut.register(req));
    }

    @Test
    void 로그인_할_수_있다() {
        LoginRequest req = createLoginRequest();
        Member member = createMember();
        when(memberService.findMemberByNickName(req.nickname())).thenReturn(member);
        when(passwordEncoder.matches(req.password(), member.getPassword())).thenReturn(true);
        when(jwtProvider.generateLoginToken(new JwtMemberInfo(member.getId(), member.getRole()))).thenReturn(new AuthTokens("access", "refresh"));

        AuthTokens login = sut.login(req).authTokens();

        Assertions.assertEquals(login.accessToken(), "access");
        Assertions.assertEquals(login.refreshToken(), "refresh");
    }

    @Test
    void 존재하지않은_아이디로_로그인_할_수_없다() {
        LoginRequest req = createLoginRequest();
        when(memberService.findMemberByNickName("test")).thenThrow(new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));

        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> sut.login(req));

        Assertions.assertEquals(ExceptionCode.MEMBER_NOT_FOUND, exception.getExceptionCode());
    }

    @Test
    void 잘못된_비밀번호로_로그인_할_수_없다() {
        LoginRequest req = createLoginRequest();
        Member member = createMember();
        when(memberService.findMemberByNickName(req.nickname())).thenReturn(member);
        when(passwordEncoder.matches(req.password(), member.getPassword())).thenReturn(false);

        AuthException exception = Assertions.assertThrows(AuthException.class,
                () -> sut.login(req));

        Assertions.assertEquals(ExceptionCode.INVALID_PASSWORD, exception.getExceptionCode());
    }

    @Test
    void 중복된_아이디가_존재하면_참을_반환한다() {
        when(memberService.isExistByNickname("duplicate")).thenReturn(true);

        boolean isDuplicated = sut.hasDuplicateNickname("duplicate");

        Assertions.assertTrue(isDuplicated);
    }

    @Test
    void 중복된_아이디가_존재하지_않으면_거짓을_반환한다() {
        when(memberService.isExistByNickname("duplicate")).thenReturn(false);

        boolean isDuplicated = sut.hasDuplicateNickname("duplicate");

        Assertions.assertFalse(isDuplicated);
    }

    @Test
    void 리프레시_토큰이_유효하다면_토큰을_재발급한다() {
        when(jwtProvider.isTokenExpired("refreshToken")).thenReturn(false);
        RefreshToken refreshToken = new RefreshToken("refreshToken", new JwtMemberInfo(1L, RoleType.EMPLOYEE));
        when(refreshTokenService.findById("refreshToken")).thenReturn(refreshToken);
        when(jwtProvider.generateLoginToken(refreshToken.getUserInfo())).thenReturn(new AuthTokens("access", "refresh"));
        ;

        AuthTokens refreshTokens = sut.refreshTokens("refreshToken");

        Assertions.assertEquals(refreshTokens.accessToken(), "access");
        Assertions.assertEquals(refreshTokens.refreshToken(), "refresh");
    }

    @Test
    void 리프레시_토큰이_유효하지_않으면_재발급_할_수_없다() {
        when(jwtProvider.isTokenExpired("refreshToken")).thenReturn(true);

        AuthException exception = Assertions.assertThrows(AuthException.class, () -> sut.refreshTokens("refreshToken"));

        Assertions.assertEquals(ExceptionCode.EXPIRED_TOKEN, exception.getExceptionCode());
    }

    @Test
    void 이메일로_아이디를_찾을_수_있다() {
        Member member = createMember();
        when(memberService.findMemberByEmail(member.getEmail())).thenReturn(member);

        NicknameInquiryResponse findNickname = sut.findNickname(member.getEmail());

        Assertions.assertEquals(member.getNickname(), findNickname.nickname());
    }

    @Test
    void 비밀번호를_변경할_수_있다() {
        Member member = createMember();
        PasswordResetRequest request = new PasswordResetRequest(member.getEmail(), "newPassword");
        when(memberService.findMemberByEmail(member.getEmail())).thenReturn(member);
        when(passwordEncoder.encode(request.password())).thenReturn(request.password());

        sut.resetPassword(request);

        Assertions.assertEquals(request.password(), member.getPassword());
    }

    private LoginRequest createLoginRequest() {
        return new LoginRequest(
                "test", "testpassword"
        );
    }

    private SignUpRequest createSignUpReq(String email) {
        return new SignUpRequest(
                "성",
                "이름",
                "test1",
                "password",
                email,
                "position",
                "cns",
                LocalDate.now()
        );
    }
}