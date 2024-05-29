package com.example.cns.project.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.project.service.ProjectService;
import com.example.cns.task.dto.request.TaskCreateRequest;
import com.example.cns.task.dto.request.TaskEditRequest;
import com.example.cns.task.dto.request.TaskUpdateStateRequest;
import com.example.cns.task.dto.response.TaskListResponse;
import com.example.cns.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/task")
@Tag(name = "할 일 api", description = "프로젝트 내 할 일과 관련된 api")
public class TaskController {
    private final TaskService taskService;
    private final ProjectService projectService;

    @Operation(summary = "할 일을 생성하는 api", description = "할 일 정보를 입력 받고 할 일을 생성한다.")
    @Parameter(name = "projectId", description = "프로젝트 pk", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정에 성공하면 200을 반환한다."),
            @ApiResponse(responseCode = "400", description = "해당 프로젝트의 참여자가 아닌 경우")
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{projectId}")
    public ResponseEntity createTask(@Auth Long memberId, @PathVariable Long projectId, @Valid @RequestBody TaskCreateRequest request) {
        projectService.validateMemberInProject(memberId, projectId);
        return ResponseEntity.ok(taskService.createTask(memberId, projectId, request));
    }

    @Operation(summary = "할 일을 수정하는 api", description = "할 일 내용을 입력 받고 할 일을 수정한다.")
    @Parameter(name = "taskId", description = "할 일 pk", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정에 성공하면 200을 반환한다."),
            @ApiResponse(responseCode = "400", description = "해당 할 일의 주인이 아닌 경우.")
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PatchMapping("/{taskId}")
    public ResponseEntity editTask(@Auth Long memberId, @PathVariable(name = "taskId") Long taskId, @RequestBody TaskEditRequest request) {
        taskService.ValidateTaskByMember(memberId, taskId);
        taskService.editTask(taskId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "할 일을 삭제하는 api", description = "할 일을 삭제한다.")
    @Parameter(name = "taskId", description = "할 일 pk", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제에 성공하면 200을 반환한다."),
            @ApiResponse(responseCode = "400", description = "해당 할 일의 주인이 아닌 경우.")
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @DeleteMapping("/{taskId}")
    public ResponseEntity deleteTask(@Auth Long memberId, @PathVariable(name = "taskId") Long taskId) {
        taskService.ValidateTaskByMember(memberId, taskId);
        taskService.deleteTask(taskId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "할 일의 상태를 수정하는 api", description = "할 일 상태를 입력 받고 할 일을 수정한다.")
    @Parameter(name = "taskId", description = "할 일 pk", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정에 성공하면 200을 반환한다."),
            @ApiResponse(responseCode = "400", description = "해당 할 일의 주인이 아닌 경우.")
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PatchMapping("/{taskId}/state")
    public ResponseEntity updateTaskState(@Auth Long memberId, @PathVariable(name = "taskId") Long taskId, @RequestBody TaskUpdateStateRequest request) {
        taskService.ValidateTaskByMember(memberId, taskId);
        taskService.updateTaskState(taskId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "나의 할 일을 조회하는 api", description = "프로젝트 번호를 입력받고 해당 프로젝트에서 내 할 일을 조회한다.")
    @Parameter(name = "projectId", description = "프로젝트 pk", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공.",
                    content = @Content(schema = @Schema(implementation = TaskListResponse.class))),
            @ApiResponse(responseCode = "400", description = "해당 프로젝트의 참여자가 아닌 경우")
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("")
    public ResponseEntity getMyTaskByProject(@Auth Long memberId, @RequestParam(name = "projectId") Long projectId) {
        projectService.validateMemberInProject(memberId, projectId);
        return ResponseEntity.ok(taskService.getMyTaskByProject(memberId, projectId));
    }

    @Operation(summary = "프로젝트 참여자의 할 일을 조회하는 api", description = "프로젝트 번호를 입력받고 각 참여자들의 할 일들을 조회한다.")
    @Parameter(name = "projectId", description = "프로젝트 pk", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = TaskListResponse.class))),
            @ApiResponse(responseCode = "400", description = "해당 프로젝트의 참여자가 아닌 경우")
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/all")
    public ResponseEntity getAllTaskByProject(@Auth Long memberId, @RequestParam(name = "projectId") Long projectId) {
        projectService.validateMemberInProject(memberId, projectId);
        return ResponseEntity.ok(taskService.getAllTaskByProject(projectId));
    }
}