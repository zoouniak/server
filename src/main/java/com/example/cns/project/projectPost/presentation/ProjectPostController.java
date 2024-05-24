package com.example.cns.project.projectPost.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.project.projectPost.dto.request.ProjectPostOpinionRequest;
import com.example.cns.project.projectPost.dto.request.ProjectPostRequest;
import com.example.cns.project.projectPost.dto.response.ProjectPostResponse;
import com.example.cns.project.projectPost.service.ProjectPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
public class ProjectPostController {

    private final ProjectPostService projectPostService;

    /*
    프로젝트 게시글 조회
     */
    @GetMapping("/{projectId}/post")
    public ResponseEntity getProjectPosts(@Auth Long memberId, @PathVariable Long projectId,@RequestParam(name = "cursorValue", required = false) Long cursorValue){
        List<ProjectPostResponse> projectPosts = projectPostService.getProjectPosts(memberId, projectId, cursorValue);
        return ResponseEntity.ok(projectPosts);
    }

    /*
    프로젝트 게시글 작성
     */
    @PostMapping("/{projectId}/post")
    public ResponseEntity createProjectPost(@Auth Long memberId, @PathVariable Long projectId, @RequestBody ProjectPostRequest projectPostRequest){
        projectPostService.createProjectPost(memberId,projectId,projectPostRequest);
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 게시글 수정
     */
    @PatchMapping("/{projectId}/post/{postId}")
    public ResponseEntity patchProjectPost(@Auth Long memberId,@PathVariable Long projectId,@PathVariable Long postId, @RequestBody ProjectPostRequest projectPostRequest){
        projectPostService.patchProjectPost(memberId,projectId,postId,projectPostRequest);
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 게시글 삭제
     */
    @DeleteMapping("/{projectId}/post/{postId}")
    public ResponseEntity deleteProjectPost(@Auth Long memberId, @PathVariable Long projectId,@PathVariable Long postId){
        projectPostService.deleteProjectPost(memberId,projectId,postId);
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 게시글 의견 추가
     */
    @PostMapping("/{projectId}/post/{postId}/opinion")
    public ResponseEntity addProjectPostOpinion(@Auth Long memberId, @PathVariable Long projectId, @PathVariable Long postId, @RequestBody ProjectPostOpinionRequest projectPostOpinionRequest){
        projectPostService.addProjectPostOpinion(memberId, projectId, postId, projectPostOpinionRequest);
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 게시글 의견 취소
     */
    @DeleteMapping("/{projectId}/post/{postId}/opinion")
    public ResponseEntity deleteProjectPostOpinion(@Auth Long memberId, @PathVariable Long projectId, @PathVariable Long postId){
        projectPostService.deleteProjectPostOpinion(memberId, projectId, postId);
        return ResponseEntity.ok().build();
    }
}
