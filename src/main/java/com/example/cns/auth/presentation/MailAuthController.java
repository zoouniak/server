package com.example.cns.auth.presentation;

import com.example.cns.auth.dto.request.EmailAuthRequest;
import com.example.cns.auth.service.MailAuthService;
import com.example.cns.common.exception.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Parameter(name = "email", description = "이메일", in = ParameterIn.PATH, required = true)
    @ApiResponse(
            responseCode = "200", description = "이메일 전송 성공"
    )
    @GetMapping("/request/{email}")
    public ResponseEntity<?> mailSend(
            @PathVariable(name = "email") String email
    ) {
        mailAuthService.sendAuthMail(email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "인증번호 확인", description = "인증번호를 입력 받고 일치 여부를 반환한다")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "이메일 인증 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 이메일",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = ExceptionResponse.class)
                                    ))
                    ),
                    @ApiResponse(responseCode = "403", description = "인증번호 불일치",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = ExceptionResponse.class)
                                    ))
                    )
            }
    )
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmAuthCode(@RequestBody @Valid EmailAuthRequest dto) {
        mailAuthService.confirmAuthCode(dto);
        return ResponseEntity.ok().build();
    }
}
