package com.example.cns.auth.service;

import com.example.cns.auth.domain.AuthCode;
import com.example.cns.auth.domain.repository.AuthCodeRepository;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.security.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthCodeService {
    private final AuthCodeRepository authCodeRepository;

    public void saveAuthCode(AuthCode authCode) {
        authCodeRepository.save(authCode);
    }

    public AuthCode findByEmail(String email) {
        return authCodeRepository.findById(email)
                .orElseThrow(() -> new AuthException(ExceptionCode.INVALID_EMAIL));
    }
}
