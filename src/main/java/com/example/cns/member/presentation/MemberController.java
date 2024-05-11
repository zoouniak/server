package com.example.cns.member.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.member.dto.response.MemberSearchResponse;
import com.example.cns.member.service.MemberSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/member")
@Tag(name = "사용자 api", description = "사용자와 관련된 api")
@RequiredArgsConstructor
public class MemberController {
    private final MemberSearchService memberSearchService;

    @Operation(summary = "회원 검색", description = "파라미터를 입력 받고 파라미터로 시작하는 닉네임을 가진 회원을 반환한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "검색 데이터",
                            content = @Content(schema = @Schema(implementation = MemberSearchResponse.class))),
                    @ApiResponse(responseCode = "204", description = "검색 데이터가 없는 경우"
                    )
            })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search-by-nickname")
    public ResponseEntity<List<MemberSearchResponse>> searchMember(@Auth(required = false) Long memberId, @RequestParam(name = "param") String param) {
        List<MemberSearchResponse> responses = memberSearchService.findMemberContainsNicknameParam(param);
        if (responses.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "회원 검색", description = "파라미터를 입력 받고 파라미터로 시작하는 닉네임을 가진 (사용자가 속한)회사 내 회원을 반환한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "검색 데이터",
                            content = @Content(schema = @Schema(implementation = MemberSearchResponse.class))),
                    @ApiResponse(responseCode = "204", description = "검색 데이터가 없는 경우"
                    )
            })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/only-company/search-by-nickname")
    public ResponseEntity<List<MemberSearchResponse>> searchCompanyMember(@Auth(required = false) Long memberId, @RequestParam(name = "param") String param) {
        List<MemberSearchResponse> responses = memberSearchService.findMemberContainsNicknameParamInSameCompany(memberId, param);
        if (responses.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(responses);
    }

}
