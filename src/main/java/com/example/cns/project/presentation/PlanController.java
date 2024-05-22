package com.example.cns.project.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.project.dto.request.PlanCreateRequest;
import com.example.cns.project.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/plan")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    @GetMapping("/list")
    public ResponseEntity getPlanList(@RequestParam(name = "projectId") Long projectId) {
        return ResponseEntity.ok(planService.getPlanListByProject(projectId));
    }

    @GetMapping("/{planId}")
    public ResponseEntity getPlanDetail(@PathVariable(name = "planId") Long planId) {
        return ResponseEntity.ok(planService.getPlanDetails(planId));
    }

    @PostMapping("/create/{projectId}")
    public ResponseEntity createPlan(@PathVariable(name = "projectId") Long projectId, @Valid PlanCreateRequest planCreateRequest) {
        planService.createPlan(projectId, planCreateRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{planId}")
    public ResponseEntity editPlan(@PathVariable(name = "planId") Long planId, PlanCreateRequest planEditRequest) {
        planService.editPlan(planId, planEditRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity deletePlan(@Auth Long memberId, @PathVariable(name = "planId") Long planId) {
        planService.deletePlan(planId);
        return ResponseEntity.ok().build();
    }
}
