package com.example.cns.auth.controller;

import com.example.cns.auth.dto.EmailAuthReq;
import com.example.cns.auth.service.MailAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "이메일 인증 API", description = "이메일 인증과 관련된 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email-auth")
public class MailAuthController {
    private final MailAuthService mailAuthService;

    @Operation(summary = "이메일 인증 요청", description = "이메일을 입력 받고 인증번호를 전송한다")
    @Parameter(name = "email", description = "이메일")
    @GetMapping("/request/{email}")
    public ResponseEntity mailSend(
            @PathVariable(name = "email") String email
    ) {
        mailAuthService.sendAuthMail(email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "인증번호 확인", description = "인증번호를 입력 받고 일치 여부를 반환한다")
    @PostMapping("/confirm")
    public ResponseEntity confirmAuthCode(@RequestBody @Valid EmailAuthReq dto) {
        mailAuthService.confirmAuthCode(dto);
        return ResponseEntity.ok().build();
    }
}
