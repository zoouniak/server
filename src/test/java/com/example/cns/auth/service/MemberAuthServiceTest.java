package com.example.cns.auth.service;

import com.example.cns.auth.domain.RefreshToken;
import com.example.cns.auth.dto.request.LoginRequest;
import com.example.cns.auth.dto.request.SignUpRequest;
import com.example.cns.auth.dto.response.AuthTokens;
import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.security.exception.AuthException;
import com.example.cns.common.security.jwt.dto.JwtUserInfo;
import com.example.cns.common.security.jwt.provider.JwtProvider;
import com.example.cns.company.domain.Company;
import com.example.cns.company.service.CompanySearchService;
import com.example.cns.member.domain.Member;
import com.example.cns.member.service.MemberService;
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
    private MemberService memberService;
    @Mock
    private CompanySearchService companySearchService;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Spy
    private PasswordEncoder passwordEncoder;


    @Test
    void 회원가입이_가능하다() {
        // 준비
        SignUpRequest req = createSignUpReq("test1", "test1@cns.com", "cns");
        when(memberService.isExistByUsername(req.username())).thenReturn(false);
        when(memberService.isExistByEmail(req.email())).thenReturn(false);
        when(companySearchService.findByCompanyName(req.companyName())).thenReturn(new Company(1L, "cns", "cns.com"));

        // 실행
        sut.register(req);

        // 검증
        verify(memberService).saveMember(any());
    }

    @Test
    void 회사_이메일이_아니면_회원가입이_불가능하다() {
        SignUpRequest req = createSignUpReq("test1", "test1@naver.com", "cns");
        when(memberService.isExistByUsername(req.username())).thenReturn(false);
        when(memberService.isExistByEmail(req.email())).thenReturn(false);
        when(companySearchService.findByCompanyName(req.companyName())).thenReturn(new Company(1L, "cns", "cns.com"));

        Assertions.assertThrows(AuthException.class,
                () -> sut.register(req));
    }

    @Test
    void 중복된_이메일이_존재할_경우_회원가입이_불가능하다() {
        SignUpRequest req = createSignUpReq("test1", "test1@naver.com", "cns");
        when(memberService.isExistByEmail(req.email())).thenReturn(true);

        Assertions.assertThrows(AuthException.class,
                () -> sut.register(req));
    }

    @Test
    void 중복된_아이디가_존재할_경우_회원가입이_불가능하다() {
        SignUpRequest req = createSignUpReq("test1", "test1@naver.com", "cns");
        when(memberService.isExistByUsername(req.username())).thenReturn(true);
        when(memberService.isExistByEmail(req.email())).thenReturn(false);

        Assertions.assertThrows(AuthException.class,
                () -> sut.register(req));
    }

    private SignUpRequest createSignUpReq(String username, String email, String companyName) {
        return new SignUpRequest(
                "성",
                "이름",
                username,
                "password",
                email,
                "position",
                companyName,
                LocalDate.now()
        );
    }

    @Test
    void 로그인_할_수_있다() {
        LoginRequest req = createLoginRequest("test", "testpassword");
        Member member = new Member(1L, "test", "testpassword", "email", "first", "second", LocalDate.now(), RoleType.EMPLOYEE, "");
        when(memberService.findMemberByUserName(req.username())).thenReturn(member);
        when(passwordEncoder.matches(req.password(), member.getPassword())).thenReturn(true);
        when(jwtProvider.generateLoginToken(new JwtUserInfo(member.getId(), member.getRole()))).thenReturn(new AuthTokens("access", "refresh"));

        AuthTokens login = sut.login(req);

        Assertions.assertEquals(login.accessToken(), "access");
        Assertions.assertEquals(login.refreshToken(), "refresh");
    }

    @Test
    void 존재하지않은_아이디로_로그인_할_수_없다() {
        LoginRequest req = createLoginRequest("test", "testpassword");
        when(memberService.findMemberByUserName("test")).thenThrow(new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));

        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> sut.login(req));

        Assertions.assertEquals(ExceptionCode.MEMBER_NOT_FOUND, exception.getExceptionCode());
    }

    @Test
    void 잘못된_비밀번호로_로그인_할_수_없다() {
        LoginRequest req = createLoginRequest("test", "testpassword");
        Member member = new Member(1L, "test", "testpassword", "email", "first", "second", LocalDate.now(), RoleType.EMPLOYEE, "");
        when(memberService.findMemberByUserName(req.username())).thenReturn(member);
        when(passwordEncoder.matches(req.password(), member.getPassword())).thenReturn(false);

        AuthException exception = Assertions.assertThrows(AuthException.class,
                () -> sut.login(req));

        Assertions.assertEquals(ExceptionCode.INVALID_PASSWORD, exception.getExceptionCode());
    }

    private LoginRequest createLoginRequest(String username, String password) {
        return new LoginRequest(
                username, password
        );
    }

    @Test
    void 중복된_아이디가_존재하면_참을_반환한다() {
        when(memberService.isExistByUsername("duplicate")).thenReturn(true);

        boolean isDuplicated = sut.checkDuplicateUsername("duplicate");

        Assertions.assertTrue(isDuplicated);
    }

    @Test
    void 중복된_아이디가_존재하지_않으면_거짓을_반환한다() {
        when(memberService.isExistByUsername("duplicate")).thenReturn(false);

        boolean isDuplicated = sut.checkDuplicateUsername("duplicate");

        Assertions.assertFalse(isDuplicated);
    }

    @Test
    void 리프레시_토큰이_유효하다면_토큰을_재발급한다() {
        when(jwtProvider.isTokenExpired("refreshToken")).thenReturn(false);
        RefreshToken refreshToken = new RefreshToken("refreshToken", new JwtUserInfo(1L, RoleType.EMPLOYEE));
        when(refreshTokenService.findById("refreshToken")).thenReturn(refreshToken);
        when(jwtProvider.generateLoginToken(refreshToken.getUserInfo())).thenReturn(new AuthTokens("access", "refresh"));
        ;

        AuthTokens refreshTokens = sut.refresh("refreshToken");

        Assertions.assertEquals(refreshTokens.accessToken(), "access");
        Assertions.assertEquals(refreshTokens.refreshToken(), "refresh");
    }

    @Test
    void 리프레시_토큰이_유효하지_않으면_재발급_할_수_없다() {
        when(jwtProvider.isTokenExpired("refreshToken")).thenReturn(true);

        AuthException exception = Assertions.assertThrows(AuthException.class, () -> sut.refresh("refreshToken"));

        Assertions.assertEquals(ExceptionCode.EXPIRED_TOKEN, exception.getExceptionCode());
    }


}