package com.example.cns.auth.presentation;

import com.example.cns.auth.dto.request.LoginRequest;
import com.example.cns.auth.dto.request.PasswordResetRequest;
import com.example.cns.auth.dto.request.SignUpRequest;
import com.example.cns.auth.dto.response.AuthTokens;
import com.example.cns.auth.dto.response.NicknameCheckResponse;
import com.example.cns.auth.dto.response.NicknameInquiryResponse;
import com.example.cns.auth.service.MemberAuthService;
import com.example.cns.common.exception.ExceptionResponse;
import com.example.cns.common.exception.MemberVerificationRequest;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Parameter(name = "nickname", description = "사용하고자 하는 아이디", in = ParameterIn.PATH, required = true)
    @ApiResponse(responseCode = "200", description = "중복 아이디 존재(true)/존재하지 않음(false)",
            content = @Content(schema = @Schema(implementation = NicknameCheckResponse.class)))
    @PreAuthorize("isAnonymous()")
    @GetMapping("/register/check/{nickname}")
    public ResponseEntity<NicknameCheckResponse> checkNickname(@PathVariable(name = "nickname") String nickname) {
        return ResponseEntity.ok(new NicknameCheckResponse(memberAuthService.hasDuplicateNickname(nickname)));
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
        AuthTokens refreshTokens = memberAuthService.refreshTokens(refreshToken);

        return getResponseEntity(refreshTokens);
    }

    @Operation(summary = "아이디 찾기", description = "이메일을 입력받고 해당 이메일로 가입된 아이디를 반환한다.")
    @Parameter(name = "email", description = "회원가입 시 사용한 이메일")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "회원의 아이디",
                            content = @Content(schema = @Schema(implementation = NicknameInquiryResponse.class))),
                    @ApiResponse(responseCode = "400", description = "해당 이메일로 가입한 아이디가 없다.",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
                    )
            })
    @PreAuthorize("isAnonymous()")
    @GetMapping("/nickname-inquiry")
    public ResponseEntity<?> findNickname(@RequestParam(name = "email") String email) {
        return ResponseEntity.ok(memberAuthService.findNickname(email));
    }

    @Operation(summary = "이름, 이메일 검증", description = "이름과 아이디를 입력 받고 회원 데이터와 일치한 지 검증한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "검증 성공"),
                    @ApiResponse(responseCode = "400", description = "검증 실패",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
                    )
            })
    @PreAuthorize("isAnonymous()")
    @GetMapping("/password-inquiry/verification")
    public ResponseEntity<?> verifyMember(@RequestBody MemberVerificationRequest request) {
        // 이름, 이메일 검사
        memberAuthService.verifyMember(request);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 초기화", description = "새로운 비밀번호로 초기화한다.")
    @ApiResponse(responseCode = "200", description = "비밀번호 초기화 성공")
    @PreAuthorize("isAnonymous()")
    @PutMapping("/password-inquiry")
    public ResponseEntity resetPassword(@RequestBody PasswordResetRequest request) {
        memberAuthService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그아웃")
    public ResponseEntity logout() {
        // todo 로그아웃 구현
        //memberAuthService.logout();
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
