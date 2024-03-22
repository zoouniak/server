package com.example.cns.auth.service;

import com.example.cns.auth.domain.AuthCode;
import com.example.cns.auth.domain.repository.AuthCodeRepository;
import com.example.cns.auth.dto.EmailAuthReq;
import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MailAuthService {
    private final String sender;
    private final String subject;
    private final AuthCodeRepository authCodeRepository;
    private final JavaMailSender mailSender;

    public MailAuthService(@Value("${spring.mail.username}") String sender,
                           @Value("${spring.mail.subject}") String subject,
                           AuthCodeRepository authCodeRepository,
                           JavaMailSender mailSender) {
        this.sender = sender;
        this.subject = subject;
        this.authCodeRepository = authCodeRepository;
        this.mailSender = mailSender;
    }

    public void sendAuthMail(String email) {
        String authCode = generateAuthCode();
        authCodeRepository.save(new AuthCode(email, authCode));

        // todo 메소드 분리
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(sender);
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(authCode, true);
        };

        mailSender.send(messagePreparator);
    }

    public void confirmAuthCode(EmailAuthReq dto) {
        AuthCode authCode = authCodeRepository.findById(dto.email())
                .orElseThrow(() -> new BusinessException(ExceptionCode.INVALID_EMAIL));

        if (!dto.authCode().equals(authCode.getAuthCode()))
            throw new BusinessException(ExceptionCode.INCORRECT_AUTHENTICATION_NUMBER);
    }

    private String generateAuthCode() {
        StringBuilder codeBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10);
            codeBuilder.append(digit);
        }
        return codeBuilder.toString();
    }
}
