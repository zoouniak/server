package com.example.cns.auth.service;

import com.example.cns.auth.domain.AuthCode;
import com.example.cns.auth.dto.request.EmailAuthRequest;
import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailAuthServiceTest {
    @InjectMocks
    private MailAuthService sut;

    @Mock
    private AuthCodeService authCodeService;

    @Test
    void 인증번호를_확인한다() {
        EmailAuthRequest req = generateEmailAuthRequest("email", "111111");
        AuthCode authCode = new AuthCode("email", "111111");
        when(authCodeService.findByEmail(req.email())).thenReturn(authCode);

        sut.confirmAuthCode(req);

        verify(authCodeService).findByEmail(req.email());
    }

    @Test
    void 인증번호가_일치하지_않으면_에러를_던진다() {
        EmailAuthRequest req = generateEmailAuthRequest("email", "111111");
        AuthCode authCode = new AuthCode("email", "22222");
        when(authCodeService.findByEmail(req.email())).thenReturn(authCode);

        BusinessException exception = assertThrows(BusinessException.class, () -> sut.confirmAuthCode(req));

        Assertions.assertEquals(ExceptionCode.INCORRECT_AUTHENTICATION_NUMBER, exception.getExceptionCode());
        ;
    }

    private EmailAuthRequest generateEmailAuthRequest(String email, String authCode) {
        return new EmailAuthRequest(email, authCode);
    }
}