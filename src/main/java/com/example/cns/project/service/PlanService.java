package com.example.cns.project.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.project.domain.Project;
import com.example.cns.project.domain.repository.ProjectRepository;
import com.example.cns.project.dto.request.PlanCreateRequest;
import com.example.cns.project.dto.request.PlanInviteRequest;
import com.example.cns.project.dto.response.MemberResponse;
import com.example.cns.project.dto.response.PlanCreateResponse;
import com.example.cns.project.dto.response.PlanDetailResponse;
import com.example.cns.project.dto.response.PlanListResponse;
import com.example.cns.project.plan.domain.Plan;
import com.example.cns.project.plan.domain.PlanParticipation;
import com.example.cns.project.plan.domain.repository.PlanParticipationRepository;
import com.example.cns.project.plan.domain.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final ProjectRepository projectRepository;
    private final PlanRepository planRepository;
    private final PlanParticipationRepository planParticipationRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PlanCreateResponse createPlan(Long projectId, PlanCreateRequest request) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new BusinessException(ExceptionCode.PROJECT_NOT_EXIST));
        Plan save = planRepository.save(request.toPlanEntity(project));
        for (Long memberId : request.inviteList()) {
            planParticipationRepository.save(new PlanParticipation(memberId, save.getId()));
        }
        return new PlanCreateResponse(save.getId());
    }

    @Transactional
    public void editPlan(Long planId, PlanCreateRequest planEditRequest) {
        Plan plan = getPlan(planId);
        plan.updatePlan(planEditRequest);
    }

    @Transactional(readOnly = true)
    public List<PlanListResponse> getPlanListByProject(Long projectId) {
        List<Plan> planList = planRepository.getAllByProject(projectId);
        return planList.stream()
                .map(plan -> new PlanListResponse(plan.getPlanName(), plan.getStartedAt(), plan.getEndedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePlan(Long planId) {
        planRepository.deleteById(planId);
    }

    @Transactional(readOnly = true)
    public PlanDetailResponse getPlanDetails(Long planId) {
        Plan plan = getPlan(planId);
        List<PlanParticipation> allByPlanId = planParticipationRepository.findAllByPlanId(planId);
        List<MemberResponse> participants = new ArrayList<>();
        for (PlanParticipation planParticipation : allByPlanId) {
            Member member = memberRepository.findById(planParticipation.getMember()).get();
            participants.add(new MemberResponse(member.getNickname(), member.getUrl()));
        }
        return toPlanDetailResponse(plan, participants);
    }

    private PlanDetailResponse toPlanDetailResponse(Plan plan, List<MemberResponse> participants) {
        return new PlanDetailResponse(
                plan.getPlanName(),
                plan.getStartedAt(),
                plan.getEndedAt(),
                plan.getContent(),
                participants
        );
    }

    public void validateManager(Long memberId, Long planId) {
        Plan plan = getPlan(planId);
        if (!Objects.equals(plan.getProject().getManager().getId(), memberId))
            throw new BusinessException(ExceptionCode.ONLY_MANAGER);
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.PLAN_NOT_EXIST));
    }

    public void inviteParticipant(Long planId, PlanInviteRequest inviteRequest) {
        for (Long memberId : inviteRequest.memberList()) {
            planParticipationRepository.save(new PlanParticipation(memberId, planId));
        }
    }

    public void exitPlan(Long planId, Long memberId) {
        planParticipationRepository.deleteByPlanAndMember(planId, memberId);
    }
}
