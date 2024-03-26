package com.example.cns.auth.presentation;

import com.example.cns.auth.dto.AuthTokens;
import com.example.cns.auth.dto.DuplicateCheckResponse;
import com.example.cns.auth.dto.LoginRequest;
import com.example.cns.auth.dto.SignUpRequest;
import com.example.cns.auth.service.MemberAuthService;
import com.example.cns.common.exception.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저 관리 API", description = "사용자 인증과 관련된 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final MemberAuthService memberAuthService;

    @Operation(summary = "회원가입", description = "사용자 정보를 입력 받고 유효하다면 회원가입 성공 여부를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "회원가입 실패",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity signUp(
            @RequestBody @Valid SignUpRequest dto
    ) {
        memberAuthService.register(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "아이디 중복 확인", description = "아이디를 입력 받고 아이디 중복 여부를 반환한다.")
    @Parameter(name = "username", description = "사용하고자 하는 아이디", in = ParameterIn.PATH, required = true)
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "중복 아이디 존재(true)/존재하지 않음(false)",
                            content = @Content(schema = @Schema(implementation = DuplicateCheckResponse.class)))
            }
    )
    @GetMapping("/check-duplicate/{username}")
    public ResponseEntity<DuplicateCheckResponse> checkDuplicateId(@PathVariable(name = "username") String username) {
        boolean isExist = memberAuthService.checkDuplicateUsername(username);
        return ResponseEntity.ok(new DuplicateCheckResponse(isExist));
    }


    @Operation(summary = "로그인", description = "사용자 이름, 패스워드를 입력 받고 유효하다면 액세스 토큰과 리프레시 토큰을 반환한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공", headers = {
                            @Header(name = "Set-Cookie", description = "refresh-token"),
                            @Header(name = "Authorization", description = "access-token")
                    }),
                    @ApiResponse(responseCode = "400", description = "실패(존재하지 않는 사용자, 비밀번호 불일치)",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            })
    @PostMapping("/login")
    public ResponseEntity signIn(@RequestBody @Valid LoginRequest dto) {
        AuthTokens authTokens = memberAuthService.login(dto);

        return getResponseEntity(authTokens);
    }

    @Operation(summary = "토큰 갱신", description = "쿠키 내 리프레시 토큰을 전달 받고 유효하다면 액세스 토큰과 리프레시 토큰을 반환한다.")
    @Parameter(name = "refreshToken", description = "리프레시 토큰", in = ParameterIn.HEADER, required = true)
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", headers = {
                            @Header(name = "Set-Cookie", description = "refresh-token"),
                            @Header(name = "Authorization", description = "access-token")
                    }),
                    @ApiResponse(responseCode = "400", description = "실패(refresh token invalid)",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            })
    @PostMapping("/refresh")
    public ResponseEntity renew(@CookieValue(name = "refreshToken") String refreshToken
    ) {
        AuthTokens refreshTokens = memberAuthService.refresh(refreshToken);
        return getResponseEntity(refreshTokens);
    }


    @Operation(summary = "로그아웃")
    public ResponseEntity logout() {
        return null;
    }

    private ResponseEntity<Object> getResponseEntity(AuthTokens authTokens) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", authTokens.refreshToken())
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(60 * 60 * 24 * 30)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header("Authorization", authTokens.accessToken())
                .build();
    }
}
