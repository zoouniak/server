package com.example.cns.projectPost.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.project.domain.Project;
import com.example.cns.project.domain.repository.ProjectRepository;
import com.example.cns.projectPost.domain.ProjectPost;
import com.example.cns.projectPost.domain.ProjectPostOpinion;
import com.example.cns.projectPost.domain.repository.ProjectPostListRepositoryImpl;
import com.example.cns.projectPost.domain.repository.ProjectPostOpinionRepository;
import com.example.cns.projectPost.domain.repository.ProjectPostRepository;
import com.example.cns.projectPost.dto.request.ProjectPostOpinionRequest;
import com.example.cns.projectPost.dto.request.ProjectPostRequest;
import com.example.cns.projectPost.dto.response.ProjectPostResponse;
import com.example.cns.projectPost.type.OpinionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectPostService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectPostRepository projectPostRepository;
    private final ProjectPostOpinionRepository projectPostOpinionRepository;
    private final ProjectPostListRepositoryImpl projectPostListRepository;

    //게시글 조회
    public List<ProjectPostResponse> getProjectPosts(Long memberId, Long projectId, Long cursorValue){
        return projectPostListRepository.paginationProjectPost(memberId, projectId, 5, cursorValue);
    }

    //게시글 작성
    @Transactional
    public void createProjectPost(Long memberId, Long projectId, ProjectPostRequest projectPostRequest){
        Member member = isMemberExists(memberId);
        Project project = isProjectExists(projectId);

        projectPostRepository.save(projectPostRequest.toEntity(member,project));
    }

    //게시글 수정
    @Transactional
    public void patchProjectPost(Long memberId, Long projectId, Long postId, ProjectPostRequest projectPostRequest){

        ProjectPost projectPost = isProjectPostExists(projectId, postId);
        //해당 작성자인지?
        if(projectPost.getMember().getId().equals(memberId))
            projectPost.updateContent(projectPostRequest.content());
        else throw new BusinessException(ExceptionCode.NOT_POST_WRITER);
    }

    //게시글 삭제
    @Transactional
    public void deleteProjectPost(Long memberId, Long projectId, Long postId){
        ProjectPost projectPost = isProjectPostExists(projectId, postId);
        if(projectPost.getMember().getId().equals(memberId))
            projectPostRepository.deleteById(postId);
        else throw new BusinessException(ExceptionCode.NOT_POST_WRITER);
    }

    //의견 추가
    @Transactional
    public void addProjectPostOpinion(Long memberId, Long projectId, Long postId, ProjectPostOpinionRequest projectPostOpinionRequest){

        Optional<ProjectPostOpinion> data = projectPostOpinionRepository.findByMemberIdAndPostId(memberId, postId);

        //하나의 게시글에 여러개 의견 금지
        if(data.isEmpty()){

            Member member = isMemberExists(memberId);
            ProjectPost projectPost = isProjectPostExists(projectId, postId);

            ProjectPostOpinion opinion = ProjectPostOpinion.builder()
                    .post(projectPost)
                    .member(member)
                    .opinionType(toOpinionTypeEnum(projectPostOpinionRequest.type()))
                    .build();

            projectPostOpinionRepository.save(opinion);
        }

    }

    //의견 삭제
    @Transactional
    public void deleteProjectPostOpinion(Long memberId, Long projectId, Long postId){

        isProjectPostExists(projectId, postId);

        Optional<ProjectPostOpinion> opinion = projectPostOpinionRepository.findByMemberIdAndPostId(memberId, postId);

        if(opinion.isPresent()){
            projectPostOpinionRepository.deleteProjectPostOpinionByMemberIdAndPostId(memberId,postId);
        }
    }

    private OpinionType toOpinionTypeEnum(String type){
        switch (type) {
            case "CONS" -> {
                return OpinionType.CONS;
            }
            case "PROS" -> {
                return OpinionType.PROS;
            }
            case "CHECK" -> {
                return OpinionType.CHECK;
            }
            default -> {
                throw new BusinessException(ExceptionCode.MISMATCH_OPINION_TYPE);
            }
        }
    }

    private Project isProjectExists(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(
                () -> new BusinessException(ExceptionCode.PROJECT_NOT_EXIST));
    }

    private Member isMemberExists(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    private ProjectPost isProjectPostExists(Long projectId, Long postId) {
        return projectPostRepository.findByProjectIdAndPostId(projectId, postId).orElseThrow(
                () -> new BusinessException(ExceptionCode.POST_NOT_EXIST));
    }
}
