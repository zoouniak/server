package com.example.cns.member.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.feed.post.dto.response.FileResponse;
import com.example.cns.feed.post.dto.response.PostResponse;
import com.example.cns.member.dto.request.MemberFileRequest;
import com.example.cns.member.dto.request.MemberProfilePatchRequest;
import com.example.cns.member.dto.response.MemberProfileResponse;
import com.example.cns.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;
    /*
    특정 회원 정보 조회
    개인 or 다른 상대방
     */
    @GetMapping("/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getMemberProfile(@PathVariable Long memberId){
        MemberProfileResponse member = memberService.getMemberProfile(memberId);
        return ResponseEntity.ok(member);
    }

    /*
    프로필 수정
     */
    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity patchMemberProfile(@Auth Long memberId, @RequestBody @Valid MemberProfilePatchRequest memberProfilePatchRequest){
        memberService.patchMemberProfile(memberId,memberProfilePatchRequest);
        return ResponseEntity.ok().build();
    }

    /*
    이력서 조회
    개인 or 다른 상대방
     */
    @GetMapping("/{memberId}/resume")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getMemberResume(@PathVariable Long memberId){
        FileResponse resume = memberService.getMemberResume(memberId);
        return ResponseEntity.ok(resume);
    }

    /*
    이력서 업로드
     */
    @PostMapping("/resume")
    public ResponseEntity postMemberResume(@Auth Long memberId,MemberFileRequest memberFileRequest){
        memberService.saveResume(memberId,memberFileRequest);
        return ResponseEntity.ok().build();
    }

    /*
    이력서 삭제
     */
    @DeleteMapping("/resume")
    public ResponseEntity deleteMemberResume(@Auth Long memberId){
        memberService.deleteResume(memberId);
        return ResponseEntity.ok().build();
    }


    /*
    회원 프로필 업로드
     */
    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity uploadMemberProfileImage(@Auth Long memberId, MemberFileRequest memberFileRequest){
        memberService.saveProfile(memberId, memberFileRequest);
        return ResponseEntity.ok().build();
    }

    /*
    회원 프로필 삭제
     */
    @DeleteMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity deleteMemberProfileImage(@Auth Long memberId){
        memberService.deleteProfile(memberId);
        return ResponseEntity.ok().build();
    }

    /*
    회원 프로필 조회
     */
    @GetMapping("/{memberId}/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getMemberProfileImage(@Auth Long id,@PathVariable Long memberId){
        FileResponse image = memberService.getMemberProfileImage(memberId);
        return ResponseEntity.ok(image);
    }

    /*
    회원 회사 및 직무 수정
    프로젝트랑 연관시켜야 함
     */
    @PutMapping("/company")
    public ResponseEntity putMemberCompanyInfo(@Auth Long id){
        return ResponseEntity.ok().build();
    }

    /*
    해당 회원이 좋아요 누른 글/ 최신순
     */
    @GetMapping("/{memberId}/post/liked")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getMemberLikedPost(@PathVariable Long memberId, @RequestParam(name = "cursorValue", required = false) Long cursorValue){
        List<PostResponse> likedPost = memberService.getMemberLikedPost(memberId, cursorValue);
        return ResponseEntity.ok(likedPost);
    }

}
