package com.example.cns.project.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.feed.post.dto.response.PostMember;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.member.dto.response.MemberSearchResponse;
import com.example.cns.project.domain.Project;
import com.example.cns.project.domain.ProjectParticipation;
import com.example.cns.project.domain.repository.ProjectParticipationRepository;
import com.example.cns.project.domain.repository.ProjectRepository;
import com.example.cns.project.dto.request.ProjectInviteRequest;
import com.example.cns.project.dto.request.ProjectRequest;
import com.example.cns.project.dto.response.ProjectInfoResponse;
import com.example.cns.project.dto.response.ProjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectParticipationRepository projectParticipationRepository;

    /*
    프로젝트 생성
     */
    @Transactional
    public Long createProject(Long memberId, ProjectRequest projectRequest){
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
        return projectRepository.save(projectRequest.toEntity(member)).getId();
    }

    /*
    프로젝트 참여자 초대
     */
    @Transactional
    public void inviteProject(Long projectId, ProjectInviteRequest projectInviteRequest){
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new BusinessException(ExceptionCode.PROJECT_NOT_EXIST));

        Member manager = memberRepository.findById(projectInviteRequest.managerId()).orElseThrow(
                () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));

        project.setManager(manager);

        List<ProjectParticipation> participationList = new ArrayList<>();

        ProjectParticipation participation = ProjectParticipation.builder()
                .project(projectId)
                .member(manager.getId())
                .build();
        participationList.add(participation);

        projectInviteRequest.memberList().forEach(
                memberId -> {
                    //조회를 하고 난이후에 추가할까?
                    ProjectParticipation member = ProjectParticipation.builder()
                            .project(projectId)
                            .member(memberId)
                            .build();
                    participationList.add(member);
                }
        );

        projectParticipationRepository.saveAll(participationList);
    }

    /*
    프로젝트 수정
     */
    @Transactional
    public void patchProject(Long projectId, ProjectRequest projectRequest){
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new BusinessException(ExceptionCode.PROJECT_NOT_EXIST));

        project.updateProject(projectRequest);
    }

    /*
    프로젝트 삭제
     */

    /*
    프로젝트 나가기
     */
    @Transactional
    public void exitProject(Long memberId, Long projectId){
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new BusinessException(ExceptionCode.PROJECT_NOT_EXIST));

        if(Objects.equals(project.getManager().getId(), memberId))
            throw new BusinessException(ExceptionCode.MANAGER_CANNOT_LEAVE);
        else projectParticipationRepository.deleteById(memberId,projectId);
    }

    /*
    특정 프로젝트 조회
     */
    public ProjectResponse getSpecificProject(Long projectId){
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new BusinessException(ExceptionCode.PROJECT_NOT_EXIST));
        return ProjectResponse.builder()
                .projectId(project.getId())
                .projectName(project.getProjectName())
                .detail(project.getDetail())
                .goal(project.getGoal())
                .start(project.getStart())
                .end(project.getEnd())
                .build();
    }

    /*
    프로젝트 목록 조회
     */
    public List<ProjectInfoResponse> getProjectListByMemberId(Long memberId){

        List<Project> projectList = projectRepository.findProjectsByMemberId(memberId);
        List<ProjectInfoResponse> responses = new ArrayList<>();

        projectList.forEach(
                project -> {
                    responses.add(
                            ProjectInfoResponse.builder()
                                    .projectId(project.getId())
                                    .projectName(project.getProjectName())
                                    .detail(project.getDetail())
                                    .postMember(new PostMember(project.getManager().getId(),project.getManager().getNickname(),project.getManager().getUrl()))
                                    .build()
                    );
                }
        );

        return responses;
    }

    /*
    프로젝트 참여자 조회
     */
    public List<MemberSearchResponse> getProjectParticipant(Long projectId){

        List<ProjectParticipation> participationList = projectParticipationRepository.findProjectParticipationsByProjectId(projectId);
        List<MemberSearchResponse> memberList = new ArrayList<>();

        participationList.forEach(
                participant -> {
                    Optional<Member> member = memberRepository.findById(participant.getMember());
                    if(member.isPresent()){
                        memberList.add(
                                MemberSearchResponse.builder()
                                        .memberId(member.get().getId())
                                        .nickname(member.get().getNickname())
                                        .profile(member.get().getUrl())
                                        .build()
                        );
                    }
                }
        );

        return memberList;
    }
}
