package com.example.cns.plan.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.plan.domain.Plan;
import com.example.cns.plan.domain.PlanParticipation;
import com.example.cns.plan.domain.repository.PlanParticipationRepository;
import com.example.cns.plan.domain.repository.PlanRepository;
import com.example.cns.plan.dto.request.PlanCreateRequest;
import com.example.cns.plan.dto.request.PlanDateEditRequest;
import com.example.cns.plan.dto.request.PlanInviteRequest;
import com.example.cns.plan.dto.response.MemberResponse;
import com.example.cns.plan.dto.response.PlanCreateResponse;
import com.example.cns.plan.dto.response.PlanDetailResponse;
import com.example.cns.plan.dto.response.PlanListResponse;
import com.example.cns.project.domain.Project;
import com.example.cns.project.domain.repository.ProjectRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.DateFormatter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.example.cns.common.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final ProjectRepository projectRepository;
    private final PlanRepository planRepository;
    private final PlanParticipationRepository planParticipationRepository;
    private final MemberRepository memberRepository;

    private final ObjectMapper objectMapper;

    @Value("${spring.ai.openai.api-key}")
    private String openaiAPIKey;

    @Value("${spring.ai.openai.chat.options.model}")
    private String aiModel;

    @Value("${spring.ai.openai.chat.options.temperature}")
    private Double modelTemperature;

    @Transactional
    public PlanCreateResponse createPlan(Long projectId, PlanCreateRequest request) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new BusinessException(ExceptionCode.PROJECT_NOT_EXIST));
        Plan save = planRepository.save(request.toPlanEntity(project));
        saveParticipant(request.inviteList(), save.getId());
        return new PlanCreateResponse(save.getId());
    }

    @Transactional
    public void editPlan(Long planId, PlanCreateRequest planEditRequest) {
        Plan plan = getPlan(planId);
        plan.updatePlan(planEditRequest);
        if (!planEditRequest.inviteList().isEmpty()) {
            planParticipationRepository.deleteByPlan(planId);
            saveParticipant(planEditRequest.inviteList(), planId);
        }
    }

    @Transactional(readOnly = true)
    public List<PlanListResponse> getPlanListByProject(Long projectId) {
        List<Plan> planList = planRepository.getAllByProject(projectId);
        return planList.stream()
                .map(plan -> new PlanListResponse(plan.getId(), plan.getPlanName(), plan.getStartedAt(), plan.getEndedAt()))
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
        List<MemberResponse> participants = allByPlanId.stream()
                .map(planParticipation -> memberRepository.findById(planParticipation.getMember())
                        .map(member -> new MemberResponse(
                                member.getId(),
                                member.getNickname(),
                                member.getUrl()))
                        .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND)))
                .collect(Collectors.toList());
        return toPlanDetailResponse(plan, participants);
    }

    @Transactional
    public void openaiGeneratePlan(Long memberId, Long projectId){

        Project project = getValidatedProject(memberId, projectId);

        ChatResponse response = callOpenAiAPI(project);

        String content = response.getResult().getOutput().getContent();

        try {
            Map<String,Object> parsedContent = objectMapper.readValue(content,Map.class); //일정 내용 String 얻어옴
            Object data = parsedContent.get("plan");
            if(data != null){
                //데이터 리스트
                List<Map<String,Object>> dataList = (List<Map<String, Object>>) data;

                //일정 객체로 변환
                List<Plan> planList = dataList.stream().map(
                        plan -> Plan.builder()
                                .planName(plan.get("planName").toString())
                                .startedAt(LocalDateTime.parse(plan.get("startedAt").toString()))
                                .endedAt(LocalDateTime.parse(plan.get("endedAt").toString()))
                                .content(plan.get("content").toString())
                                .project(project)
                                .build()
                ).toList();

                List<Plan> savedPlans = planRepository.saveAll(planList);

                List<PlanParticipation> planParticipationList = savedPlans.stream().map(
                        plan -> new PlanParticipation(memberId, plan.getId())
                ).toList();

                planParticipationRepository.saveAll(planParticipationList);

            }else {
                throw new BusinessException(PLAN_GENERATE_FAILED);
            }
        } catch (JsonProcessingException e) {
            throw new BusinessException(PLAN_GENERATE_FAILED);
        }
    }

    //비동기 api 호출
    public CompletableFuture<ChatResponse> callOpenAiAPIAsync(Project project){
        return CompletableFuture.supplyAsync(() -> callOpenAiAPI(project));
    }

    private ChatResponse callOpenAiAPI(Project project){

        var openAiApi = new OpenAiApi(openaiAPIKey);

        var openAiChatOptions = OpenAiChatOptions.builder()
                .withModel(aiModel)
                .withTemperature(modelTemperature)
                .withMaxTokens(1000)
                .build();

        var chatModel = new OpenAiChatModel(openAiApi,openAiChatOptions);

        Message userMessage = new UserMessage("프로젝트 제목 : " + project.getProjectName() + "\n" +
                "프로젝트 목표 : " + project.getGoal() +"\n" +
                "시작 날짜 : " + project.getStart() + "\n" +
                "종료 날짜 : " + project.getEnd() + "\n" +
                "프로젝트 상세내용 : " + project.getDetail());
        Message systemMessage = new SystemMessage("응답값은 다음의 JSON 형태를 따라야한다. {\"plan\" : [{\"planName\" : \"프로젝트 계획\", \"startedAt\" : \"2024-01-01T09:00:00\", \"endedAt\" : \"2024-01-01T17:00:00\", \"content\" : \"프로젝트 시작을 위한 준비\"}]}");

        Prompt prompt = new Prompt(List.of(userMessage,systemMessage));

        return chatModel.call(prompt);
    }

    private PlanDetailResponse toPlanDetailResponse(Plan plan, List<MemberResponse> participants) {
        return new PlanDetailResponse(
                plan.getId(),
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

    @Transactional
    public void inviteParticipant(Long planId, PlanInviteRequest inviteRequest) {
        saveParticipant(inviteRequest.memberList(), planId);
    }

    private void saveParticipant(List<Long> inviteRequest, Long planId) {
        for (Long memberId : inviteRequest) {
            planParticipationRepository.save(new PlanParticipation(memberId, planId));
        }
    }

    @Transactional
    public void exitPlan(Long planId, Long memberId) {
        planParticipationRepository.deleteByPlanAndMember(planId, memberId);
    }

    @Transactional
    public void editPlanSchedule(Long planId, PlanDateEditRequest dateEditRequest) {
        Plan plan = getPlan(planId);
        plan.updateSchedule(dateEditRequest);
    }

    private Project getValidatedProject(Long memberId, Long projectId){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(PROJECT_NOT_EXIST));
        if(!Objects.equals(project.getManager().getId(),memberId))
            throw new BusinessException(ONLY_MANAGER);
        return project;
    }
}
