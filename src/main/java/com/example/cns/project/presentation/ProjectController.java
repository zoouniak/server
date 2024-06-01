package com.example.cns.project.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.project.dto.request.ProjectCreateRequest;
import com.example.cns.project.dto.request.ProjectInviteRequest;
import com.example.cns.project.dto.request.ProjectPatchRequest;
import com.example.cns.project.dto.response.ProjectInfoResponse;
import com.example.cns.project.dto.response.ProjectParticipantInfo;
import com.example.cns.project.dto.response.ProjectResponse;
import com.example.cns.project.service.ProjectService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "프로젝트 API", description = "프로젝트와 관련된 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProjectController {

    private final ProjectService projectService;

    /*
    프로젝트 생성
     */
    @Operation(summary = "프로젝트 생성 api", description = "사용자로부터 프로젝트 정보를 받아와 프로젝트를 생성하고, 프로젝트 인덱스값을 반환한다.")
    @Parameters(
            value = {
                    @Parameter(name = "projectRequest", description = "프로젝트 생성 요청 DTO")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장된 프로젝트의 인덱스를 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping("/project")
    public ResponseEntity createProject(@RequestBody @Valid ProjectCreateRequest projectCreateRequest) {
        projectService.createProject(projectCreateRequest);
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 수정
     */
    @Operation(summary = "프로젝트 수정 api", description = "사용자로부터 수정된 프로젝트 정보를 이용해 프로젝트를 수정한다.")
    @Parameters(
            value = {
                    @Parameter(name = "projectId", description = "프로젝트 인덱스값"),
                    @Parameter(name = "projectRequest", description = "프로젝트 수정 요청 DTO")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정에 성공하면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PatchMapping("/project/{projectId}")
    public ResponseEntity patchProject(@PathVariable Long projectId, @RequestBody @Valid ProjectPatchRequest projectPatchRequest) {
        projectService.patchProject(projectId, projectPatchRequest);
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 삭제
     */
    @Operation(summary = "프로젝트 삭제 api", description = "프로젝트에 대한 모든 정보를 삭제한다.")
    @Parameters(
            value = {
                    @Parameter(name = "projectId", description = "프로젝트 인덱스값"),
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제에 성공하면 200을 반환한다.")
    })
    @DeleteMapping("/project/{projectId}")
    public ResponseEntity deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 나가기
     */
    @Operation(summary = "프로젝트에서 퇴장하는 api", description = "사용자가 프로젝트 퇴장 요청을 보내면, 해당 프로젝트에서 나가게 된다. 단 관리자는 나갈 수 없다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
                    @Parameter(name = "projectId", description = "프로젝트 인덱스값")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트에서 성공적으로 나갈시 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @DeleteMapping("/project/{projectId}/exit")
    public ResponseEntity exitProject(@Auth Long memberId, @PathVariable Long projectId) {
        projectService.exitProject(memberId, projectId);
        return ResponseEntity.ok().build();
    }

    /*
    프로젝트 전체 목록 조회
     */
    @Operation(summary = "프로젝트 목록 조회 api", description = "해당 사용자가 참가하고 있는 프로젝트에 대한 목록을 반환한다.")
    @Parameters(
            value = {
                    @Parameter(name = "memberId", description = "JWT/사용자 id"),
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 사용자가 참가하고 있는 프로젝트 정보를 반환한다.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectInfoResponse.class))))
    })
    @GetMapping("/project/list")
    public ResponseEntity getAllProject(@Auth Long memberId) {
        return ResponseEntity.ok(projectService.getProjectListByMemberId(memberId));
    }

    /*
    특정 프로젝트 조회
     */
    @Operation(summary = "특정 프로젝트를 조회하는 api", description = "사용자에게 특정 프로젝트에 대한 정보를 반환한다.")
    @Parameters(
            value = {
                    @Parameter(name = "projectId", description = "프로젝트 인덱스 값")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 프로젝트의 정보를 반환한다.",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class)))
    })
    @GetMapping("/project/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getSpecificProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getSpecificProject(projectId));
    }

    /*
    프로젝트 참여자 조회
     */
    @Operation(summary = "프로젝트 참여자 조회 api", description = "해당 프로젝트의 참여자를 조회한다.")
    @Parameters(
            value = {
                    @Parameter(name = "projectId", description = "프로젝트 인덱스 값")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 참여자에 대한 정보를 반환한다.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectParticipantInfo.class))))
    })
    @GetMapping("/project/{projectId}/participant")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity getProjectParticipant(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectParticipant(projectId));
    }

    /*
    프로젝트 참여자 수정
     */
    @Operation(summary = "프로젝트 참여자 수정 api", description = "사용자가 참여자를 수정하면 프로젝트 참여자를 수정한다.")
    @Parameters(
            value = {
                    @Parameter(name = "projectId", description = "프로젝트 인덱스 값"),
                    @Parameter(name = "projectInviteRequest", description = "프로젝트 초대 요청 DTO")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 참여자와 담당자를 수정에 성공하면 200을 반환한다.",
                    content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PatchMapping("/project/{projectId}/invite")
    public ResponseEntity patchProjectParticipant(@PathVariable Long projectId, @RequestBody @Valid ProjectInviteRequest projectInviteRequest) {
        projectService.patchProjectParticipant(projectId, projectInviteRequest);
        return ResponseEntity.ok().build();
    }
}
