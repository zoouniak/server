package com.example.cns.project.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.project.dto.request.PlanCreateRequest;
import com.example.cns.project.dto.response.PlanDetailResponse;
import com.example.cns.project.dto.response.PlanListResponse;
import com.example.cns.project.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "일정 API")
@RestController
@RequestMapping("/api/v1/plan")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    @Operation(summary = "일정 목록을 조회하는 api", description = "프로젝트에 속한 모든 일정을 반환한다")
    @Parameter(name = "projectId", description = "프로젝트 인덱스값")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 리스트",
                    content = @Content(schema = @Schema(implementation = PlanListResponse.class)))
    })
    @GetMapping("/list")
    public ResponseEntity getPlanList(@RequestParam(name = "projectId") Long projectId) {
        return ResponseEntity.ok(planService.getPlanListByProject(projectId));
    }

    @Operation(summary = "일정의 상세정보를 조회하는 api", description = "일정 번호를 입력 받고 일정의 상세 정보를 반환한다.")
    @Parameter(name = "planId", description = "일정 번호")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 상세정보",
                    content = @Content(schema = @Schema(implementation = PlanDetailResponse.class)))
    })
    @GetMapping("/{planId}")
    public ResponseEntity getPlanDetail(@PathVariable(name = "planId") Long planId) {
        return ResponseEntity.ok(planService.getPlanDetails(planId));
    }

    @Operation(summary = "일정을 생성하는 api", description = "사용자로부터 일정 정보를 받아와 일정을 생성한다.")
    @Parameters(
            value = {
                    @Parameter(name = "projectId", description = "프로젝트 번호"),
                    @Parameter(name = "PlanCreateRequest", description = "프로젝트 생성 요청 DTO", required = true)
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 저장 성공",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @PostMapping("/create/{projectId}")
    public ResponseEntity createPlan(@PathVariable(name = "projectId") Long projectId, @Valid @RequestBody PlanCreateRequest planCreateRequest) {
        planService.createPlan(projectId, planCreateRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "일정을 수정하는 api", description = "사용자로부터 수정된 일정 정보를 받아와 일정을 수정한다.")
    @Parameters(
            value = {
                    @Parameter(name = "planId", description = "일정 번호"),
                    @Parameter(name = "PlanCreateRequest", description = "일정 수정 DTO")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 수정 성공.",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @PatchMapping("/{planId}")
    public ResponseEntity editPlan(@PathVariable(name = "planId") Long planId, @RequestBody @Valid PlanCreateRequest planEditRequest) {
        planService.editPlan(planId, planEditRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "일정을 삭제하는 api", description = "일정을 삭제한다. 단 일정은 프로젝트 관리자만 삭제할 수 있다.")
    @Parameter(name = "planId", description = "일정 번호")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 삭제 완료.",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @DeleteMapping("/{planId}")
    public ResponseEntity deletePlan(@Auth Long memberId, @PathVariable(name = "planId") Long planId) {
        planService.validateManager(memberId, planId);
        planService.deletePlan(planId);
        return ResponseEntity.ok().build();
    }
}
