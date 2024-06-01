package com.example.cns.member.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.feed.post.dto.response.FileResponse;
import com.example.cns.feed.post.dto.response.PostMember;
import com.example.cns.feed.post.dto.response.PostResponse;
import com.example.cns.member.dto.request.MemberCompanyPatchRequest;
import com.example.cns.member.dto.request.MemberFileRequest;
import com.example.cns.member.dto.request.MemberProfilePatchRequest;
import com.example.cns.member.dto.response.MemberProfileResponse;
import com.example.cns.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "회원 프로필 API", description = "회원 프로필과 관련된 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    /*
    특정 회원 정보 조회
    개인 or 다른 상대방
     */
    @Operation(summary = "특정 회원 조회 api", description = "사용자로 부터 해당 회원의 id값을 받아와 정보를 조회한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "사용자 id"),
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장된 회원의 정보를 반환한다.",
                    content = @Content(schema = @Schema(implementation = MemberProfileResponse.class)))
    })
    @GetMapping("/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getMemberProfile(@PathVariable Long memberId) {
        MemberProfileResponse member = memberService.getMemberProfile(memberId);
        return ResponseEntity.ok(member);
    }

    /*
    프로필 수정
     */
    @Operation(summary = "회원 정보 수정 api", description = "사용자로 부터 한줄소개, 생일을 받아와 정보를 수정한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "memberProfilePatchRequest", description = "회원 정보 수정 요청 DTO")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정에 성공하면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity patchMemberProfile(@Auth Long memberId, @RequestBody @Valid MemberProfilePatchRequest memberProfilePatchRequest) {
        memberService.patchMemberProfile(memberId, memberProfilePatchRequest);
        return ResponseEntity.ok().build();
    }

    /*
    이력서 조회
    개인 or 다른 상대방
     */
    @Operation(summary = "회원 이력서 조회 api", description = "특정 사용자의 이력서를 반환한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "사용자 id")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이력서가 존재하면 이력서를 반환한다.",
                    content = @Content(schema = @Schema(implementation = FileResponse.class)))
    })
    @GetMapping("/{memberId}/resume")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getMemberResume(@PathVariable Long memberId) {
        FileResponse resume = memberService.getMemberResume(memberId);
        return ResponseEntity.ok(resume);
    }

    /*
    이력서 업로드
     */
    @Operation(summary = "회원 이력서 업로드 api", description = "사용자로부터 이력서 PDF를 받아 업로드를 한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "memberFileRequest", description = "회원 이력서 업로드 요청 DTO")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이력서 업로드에 성공하면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping("/resume")
    public ResponseEntity postMemberResume(@Auth Long memberId, MemberFileRequest memberFileRequest) {
        memberService.saveResume(memberId, memberFileRequest);
        return ResponseEntity.ok().build();
    }

    /*
    이력서 삭제
     */
    @Operation(summary = "회원 이력서 삭제 api", description = "사용자로부터 삭제 요청을 받으면 이력서를 삭제한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제에 성공하면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @DeleteMapping("/resume")
    public ResponseEntity deleteMemberResume(@Auth Long memberId) {
        memberService.deleteResume(memberId);
        return ResponseEntity.ok().build();
    }


    /*
    회원 프로필 업로드
     */
    @Operation(summary = "회원 프로필 사진 업로드 api", description = "사용자로부터 프로필 사진을 받으면 업로드를 한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "memberFileRequest", description = "회원 프로필 사진 업로드 요청 DTO")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 성공하면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity uploadMemberProfileImage(@Auth Long memberId, MemberFileRequest memberFileRequest) {
        PostMember postMember = memberService.saveProfile(memberId, memberFileRequest);
        return ResponseEntity.ok(postMember);
    }

    /*
    회원 프로필 삭제
     */
    @Operation(summary = "회원 프로필 사진 삭제 api", description = "사용자가 프로필 사진 삭제 요청을 하면 삭제한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제에 성공하면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @DeleteMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity deleteMemberProfileImage(@Auth Long memberId) {
        memberService.deleteProfile(memberId);
        return ResponseEntity.ok().build();
    }

    /*
    회원 프로필 조회
     */
    @Operation(summary = "회원 프로필 사진 조회 api", description = "특정 사용자의 프로필 사진을 조회한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "사용자 id"),
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 사용자의 프로필 사진을 조회한다.",
                    content = @Content(schema = @Schema(implementation = FileResponse.class)))
    })
    @GetMapping("/{memberId}/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getMemberProfileImage(@PathVariable Long memberId) {
        FileResponse image = memberService.getMemberProfileImage(memberId);
        return ResponseEntity.ok(image);
    }

    /*
    회원 회사 및 직무 수정
    프로젝트랑 연관시켜야 함
     */
    @Operation(summary = "회원 회사/직무 수정 api", description = "사용자로부터 회사명, 직무를 입력받아 수정한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "memberCompanyPatchRequest", description = "회원 회사/직무 수정 요청 DTO")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정에 성공하면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PutMapping("/company")
    public ResponseEntity patchMemberCompanyInfo(@Auth Long memberId, @RequestBody MemberCompanyPatchRequest memberCompanyPatchRequest) {
        memberService.patchMemberCompany(memberId, memberCompanyPatchRequest);
        return ResponseEntity.ok().build();
    }

    /*
    회원 회사 및 직무 삭제
     */
    @Operation(summary = "회원 회사/직무 삭제 api", description = "사용자가 삭제요청을 하면 회사/직무를 삭제한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제에 성공하면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @DeleteMapping("/company")
    public ResponseEntity deleteMemberCompanyInfo(@Auth Long memberId) {
        memberService.deleteMemberCompany(memberId);
        return ResponseEntity.ok().build();
    }

    /*
    해당 회원이 좋아요 누른 글/ 최신순
     */
    @Operation(summary = "회원이 좋아요 누른 글 조회 api", description = "해당 회원이 좋아요 누른 글을 조회한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "사용자 id"),
                    @Parameter(name = "cursorValue", description = "무한 스크롤 커서")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글이 존재하면 이를 반환한다.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponse.class))))
    })
    @GetMapping("/{memberId}/post/liked")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getMemberLikedPost(@PathVariable Long memberId, @RequestParam(name = "cursorValue", required = false) Long cursorValue) {
        List<PostResponse> likedPost = memberService.getMemberLikedPost(memberId, cursorValue);
        return ResponseEntity.ok(likedPost);
    }

    /*
    필터 조회
     */
    @Operation(summary = "회원과 관련된 게시글 조회 api", description = "사용자가 제공하는 필터값에 따른 게시글을 반환한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "사용자 id"),
                    @Parameter(name = "filter", description = "필터의 종류",example = "['newest','oldest','period','like']"),
                    @Parameter(name = "start",description = "[period] 시작 날짜"),
                    @Parameter(name = "end", description = "[period] 끝나는 날짜"),
                    @Parameter(name = "cursorValue", description = "무한 스크롤 커서"),
                    @Parameter(name = "likeCnt", description = "[like] 좋아요 개수")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "필터에 맞는 게시글을 조회한다.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponse.class))))
    })
    @GetMapping("/{memberId}/post")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getMemberPostWithFilter(@PathVariable Long memberId,
                                                  @RequestParam(name = "filter", required = false) String filterType,
                                                  @RequestParam(name = "start", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                                  @RequestParam(name = "end", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                                                  @RequestParam(name = "cursorValue", required = false) Long cursorValue,
                                                  @RequestParam(name = "likeCnt" ,required = false) Long likeCnt) {
        List<PostResponse> responses = memberService.getMemberPostWithFilter(memberId, filterType, start, end, cursorValue, likeCnt);
        return ResponseEntity.ok(responses);
    }

}
