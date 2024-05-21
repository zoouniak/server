package com.example.cns.project.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.project.dto.request.ProjectInviteRequest;
import com.example.cns.project.dto.request.ProjectRequest;
import com.example.cns.project.dto.response.ProjectResponse;
import com.example.cns.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProjectController {

    private final ProjectService projectService;

    /*
    프로젝트 생성
     */
    @PostMapping("/project")
    public ResponseEntity createProject(@Auth Long memberId, @RequestBody ProjectRequest projectRequest){
        Long response = projectService.createProject(memberId, projectRequest);
        return ResponseEntity.ok(response);
    }

    /*
    프로젝트 수정
     */
    @PatchMapping("/project/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity patchProject(@PathVariable Long projectId,@RequestBody ProjectRequest projectRequest){
        projectService.patchProject(projectId,projectRequest);
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 삭제
    프로젝트 삭제시 일정,게시글 전부다 삭제해야함
     */
    @DeleteMapping("/project/{projectId}")
    public ResponseEntity deleteProject(@PathVariable Long projectId){
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 나가기
     */
    @DeleteMapping("/project/{projectId}/exit")
    public ResponseEntity exitProject(@Auth Long memberId, @PathVariable Long projectId){
        projectService.exitProject(memberId,projectId);
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 전체 목록 조회
     */
    @GetMapping("/project/list")
    public ResponseEntity getAllProject(@Auth Long memberId){
        return ResponseEntity.ok(projectService.getProjectListByMemberId(memberId));
    }

    /*
    특정 프로젝트 조회
     */
    @GetMapping("/project/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getSpecificProject(@PathVariable Long projectId){
        return ResponseEntity.ok(projectService.getSpecificProject(projectId));
    }

    /*
    프로젝트 초대
     */
    @PostMapping("/project/{projectId}/invite")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity inviteMemberToProject(@PathVariable Long projectId, @RequestBody ProjectInviteRequest projectInviteRequest){
        projectService.inviteProject(projectId, projectInviteRequest);
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 참여자 조회
     */
    @GetMapping("/project/{projectId}/participant")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getProjectParticipant(@PathVariable Long projectId){
        return ResponseEntity.ok(projectService.getProjectParticipant(projectId));
    }
}
