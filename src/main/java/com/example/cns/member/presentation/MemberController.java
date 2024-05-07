package com.example.cns.member.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.member.dto.response.MemberSearchResponse;
import com.example.cns.member.service.MemberSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberSearchService memberSearchService;

    @GetMapping("/search-by-nickname")
    public ResponseEntity<List<MemberSearchResponse>> searchMember(@Auth(required = false) Long memberId, @RequestParam(name = "param") String param, @RequestParam(name = "onlyCompany", defaultValue = "false") boolean onlyCompany) {
        if (onlyCompany) {
            return ResponseEntity.ok(memberSearchService.findMemberContainsNicknameParamInSameCompany(memberId, param));
        } else
            return ResponseEntity.ok(memberSearchService.findMemberContainsNicknameParam(param));
    }
}
