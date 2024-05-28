package com.example.cns.projectPost.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.projectPost.dto.request.ProjectPostOpinionRequest;
import com.example.cns.projectPost.dto.request.ProjectPostRequest;
import com.example.cns.projectPost.dto.response.ProjectPostResponse;
import com.example.cns.projectPost.service.ProjectPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "프로젝트 게시글 API", description = "프로젝트 게시글과 관련된 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
public class ProjectPostController {

    private final ProjectPostService projectPostService;

    /*
    프로젝트 게시글 조회
     */
    @Operation(summary = "프로젝트 게시글 조회 api", description = "프로젝트내의 게시글을 최신순으로 조회한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "projectId", description = "프로젝트 인덱스값"),
                    @Parameter(name = "cursorValue", description = "커서값")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글이 존재한다면, 최신순으로 게시글을 반환한다.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectPostResponse.class))))
    })
    @GetMapping("/{projectId}/post")
    public ResponseEntity getProjectPosts(@Auth Long memberId, @PathVariable Long projectId, @RequestParam(name = "cursorValue", required = false) Long cursorValue) {
        List<ProjectPostResponse> projectPosts = projectPostService.getProjectPosts(memberId, projectId, cursorValue);
        return ResponseEntity.ok(projectPosts);
    }

    /*
    프로젝트 게시글 작성
     */
    @Operation(summary = "프로젝트 게시글 작성 api", description = "사용자로부터 받은 내용으로 부터 게시글을 작성한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "projectId", description = "프로젝트 인덱스값"),
                    @Parameter(name = "projectPostRequest", description = "프로젝트 게시글 작성 요청 DTO")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작성에 성공하면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping("/{projectId}/post")
    public ResponseEntity createProjectPost(@Auth Long memberId, @PathVariable Long projectId, @RequestBody ProjectPostRequest projectPostRequest) {
        projectPostService.createProjectPost(memberId, projectId, projectPostRequest);
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 게시글 수정
     */
    @Operation(summary = "프로젝트 게시글 수정 api", description = "사용자로부터 수정된 게시글 정보를 이용해 내용을 수정한다. 단 작성자만 가능하다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "projectId", description = "프로젝트 인덱스값"),
                    @Parameter(name = "postId", description = "게시글 인덱스값"),
                    @Parameter(name = "projectPostRequest", description = "프로젝트 게시글 수정 요청 DTO")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정에 성공하면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PatchMapping("/{projectId}/post/{postId}")
    public ResponseEntity patchProjectPost(@Auth Long memberId, @PathVariable Long projectId, @PathVariable Long postId, @RequestBody ProjectPostRequest projectPostRequest) {
        projectPostService.patchProjectPost(memberId, projectId, postId, projectPostRequest);
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 게시글 삭제
     */
    @Operation(summary = "프로젝트 게시글 삭제 api", description = "게시글을 삭제한다. 단 작성자만 가능하다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "projectId", description = "프로젝트 인덱스값"),
                    @Parameter(name = "postId", description = "게시글 인덱스값")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제에 성공하면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @DeleteMapping("/{projectId}/post/{postId}")
    public ResponseEntity deleteProjectPost(@Auth Long memberId, @PathVariable Long projectId, @PathVariable Long postId) {
        projectPostService.deleteProjectPost(memberId, projectId, postId);
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 게시글 의견 추가
     */
    @Operation(summary = "프로젝트 게시글 의견 추가 api", description = "프로젝트 게시글에 의견을 남긴다. 단, 게시글에 한 종류의 의견밖에 못 남긴다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "projectId", description = "프로젝트 인덱스값"),
                    @Parameter(name = "postId", description = "게시글 인덱스값"),
                    @Parameter(name = "projectPostOpinionRequest", description = "프로젝트 게시글 의견 추가 요청 DTO")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글에 의견을 남기면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping("/{projectId}/post/{postId}/opinion")
    public ResponseEntity addProjectPostOpinion(@Auth Long memberId, @PathVariable Long projectId, @PathVariable Long postId, @RequestBody ProjectPostOpinionRequest projectPostOpinionRequest) {
        projectPostService.addProjectPostOpinion(memberId, projectId, postId, projectPostOpinionRequest);
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 게시글 의견 취소
     */
    @Operation(summary = "프로젝트 게시글 의견 취소 api", description = "해당 게시글에 남긴 의견을 취소한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "projectId", description = "프로젝트 인덱스값"),
                    @Parameter(name = "postId", description = "게시글 인덱스값")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "취소가 되면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @DeleteMapping("/{projectId}/post/{postId}/opinion")
    public ResponseEntity deleteProjectPostOpinion(@Auth Long memberId, @PathVariable Long projectId, @PathVariable Long postId) {
        projectPostService.deleteProjectPostOpinion(memberId, projectId, postId);
        return ResponseEntity.ok().build();
    }
}
