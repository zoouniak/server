package com.example.cns.auth.service;

import com.example.cns.auth.domain.AuthCode;
import com.example.cns.auth.dto.request.EmailAuthRequest;
import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

@Service
public class MailAuthService {
    private final String sender;
    private final String subject;
    private final AuthCodeService authCodeService;
    private final JavaMailSender mailSender;

    public MailAuthService(@Value("${spring.mail.username}") String sender,
                           @Value("${spring.mail.subject}") String subject,
                           AuthCodeService authCodeService,
                           JavaMailSender mailSender) {
        this.sender = sender;
        this.subject = subject;
        this.authCodeService = authCodeService;
        this.mailSender = mailSender;
    }

    public void sendAuthMail(String email) {
        String authCode = generateAuthCode();
        authCodeService.saveAuthCode(new AuthCode(email, authCode));

        sendAuthMail(email, authCode);
    }

    public void confirmAuthCode(EmailAuthRequest dto) {
        AuthCode authCode = authCodeService.findByEmail(dto.email());

        if (!dto.authCode().equals(authCode.getAuthCode()))
            throw new BusinessException(ExceptionCode.INCORRECT_AUTHENTICATION_NUMBER);
    }

    private void sendAuthMail(String email, String authCode) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(sender);
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(getHtmlContent(authCode), true);
        };

        mailSender.send(messagePreparator);
    }

    private String getHtmlContent(String authCode) {
        try {
            Path path = Paths.get(new ClassPathResource("templates/auth-email.html").getURI());
            String content = new String(Files.readAllBytes(path));
            return content.replace("{{authCode}}", authCode);
        } catch (Exception e) {
            throw new BusinessException(ExceptionCode.FAIL_SEND_EMAIL);
        }
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
