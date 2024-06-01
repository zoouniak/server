package com.example.cns.member.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.service.S3Service;
import com.example.cns.company.domain.Company;
import com.example.cns.company.service.CompanySearchService;
import com.example.cns.feed.post.domain.repository.PostListRepository;
import com.example.cns.feed.post.dto.response.FileResponse;
import com.example.cns.feed.post.dto.response.PostMember;
import com.example.cns.feed.post.dto.response.PostResponse;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.MemberResume;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.member.domain.repository.MemberResumeRepository;
import com.example.cns.member.dto.request.MemberCompanyPatchRequest;
import com.example.cns.member.dto.request.MemberFileRequest;
import com.example.cns.member.dto.request.MemberProfilePatchRequest;
import com.example.cns.member.dto.response.MemberProfileResponse;
import com.example.cns.plan.domain.repository.PlanParticipationRepository;
import com.example.cns.project.domain.Project;
import com.example.cns.project.domain.repository.ProjectParticipationRepository;
import com.example.cns.project.domain.repository.ProjectRepository;
import com.example.cns.task.domain.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final S3Service s3Service;
    private final CompanySearchService companySearchService;

    private final MemberRepository memberRepository;
    private final MemberResumeRepository memberResumeRepository;
    private final ProjectRepository projectRepository;
    private final ProjectParticipationRepository projectParticipationRepository;
    private final PlanParticipationRepository planParticipationRepository;
    private final PostListRepository postListRepository;
    private final TaskRepository taskRepository;

    public MemberProfileResponse getMemberProfile(Long memberId) {

        Member member = isMemberExists(memberId);

        return MemberProfileResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .introduction(member.getIntroduction())
                .position(member.getPosition())
                .companyName(member.getCompany().getName())
                .profile(member.getUrl())
                .birth(member.getBirth())
                .build();
    }

    @Transactional
    public void patchMemberProfile(Long memberId, MemberProfilePatchRequest memberProfilePatchRequest) {
        Member member = isMemberExists(memberId);

        member.updateInformation(memberProfilePatchRequest.introduction(), memberProfilePatchRequest.birth());
    }

    @Transactional
    public PostMember saveProfile(Long id, MemberFileRequest memberFileRequest) {
        Member member = isMemberExists(id);

        //해당 유저가 프로필이 존재한다면.. 삭제
        if (member.getIsProfileExisted()) {
            try {
                s3Service.deleteFile(member.getFileName(), "profile");
            } catch (IOException e) {
                throw new BusinessException(ExceptionCode.IMAGE_DELETE_FAILED);
            }
        }

        //프로필 등록
        FileResponse uploaded = s3Service.uploadFile(memberFileRequest.file(), "profile");
        member.updateProfile(uploaded);

        return PostMember.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .profile(member.getUrl())
                .build();

    }

    @Transactional
    public void deleteProfile(Long memberId) {
        Member member = isMemberExists(memberId);
        if (member.getIsProfileExisted()) {
            try {
                s3Service.deleteFile(member.getFileName(), "profile");
                member.updateProfile(null);
            } catch (IOException e) {
                throw new BusinessException(ExceptionCode.IMAGE_DELETE_FAILED);
            }
        } //프로필 없는 경우 에러 처리
    }

    @Transactional
    public void saveResume(Long memberId, MemberFileRequest memberFileRequest) {

        if (!Objects.equals(memberFileRequest.file().getContentType(), "application/pdf"))
            throw new BusinessException(ExceptionCode.NOT_SUPPORT_EXT);

        Member member = isMemberExists(memberId);

        //해당 유저가 이력서가 존재한다면?
        if (member.getIsResumeExisted()) {
            Optional<MemberResume> resume = memberResumeRepository.findByMemberId(member.getId());
            try {
                s3Service.deleteFile(resume.get().getFileName(), "resume");
            } catch (IOException e) {
                throw new BusinessException(ExceptionCode.IMAGE_DELETE_FAILED);
            }
            memberResumeRepository.deleteByMemberId(memberId);
        }

        //프로필 등록
        FileResponse uploaded = s3Service.uploadFile(memberFileRequest.file(), "resume");
        MemberResume memberResume = MemberResume.builder()
                .member(member)
                .url(uploaded.uploadFileURL())
                .fileName(uploaded.uploadFileName())
                .fileType(uploaded.fileType())
                .createdAt(LocalDateTime.now())
                .build();
        memberResumeRepository.save(memberResume);
        member.updateResume(true);
    }

    @Transactional
    public void deleteResume(Long memberId) {
        Member member = isMemberExists(memberId);

        if (member.getIsResumeExisted()) { //이력서가 존재한다면, 찾아서 삭제
            Optional<MemberResume> resume = memberResumeRepository.findByMemberId(member.getId());
            try {
                s3Service.deleteFile(resume.get().getFileName(), "resume");
                memberResumeRepository.deleteById(resume.get().getId());
                member.updateResume(false);
            } catch (IOException e) {
                throw new BusinessException(ExceptionCode.IMAGE_DELETE_FAILED);
            }
        } throw new BusinessException(ExceptionCode.RESUME_NOT_EXIST);

    }

    public FileResponse getMemberResume(Long memberId) {
        Optional<MemberResume> resume = memberResumeRepository.findByMemberId(memberId);
        return resume.map(memberResume -> FileResponse.builder()
                .uploadFileName(memberResume.getFileName())
                .uploadFileURL(memberResume.getUrl())
                .fileType(memberResume.getFileType())
                .build()).orElse(null);
    }

    public FileResponse getMemberProfileImage(Long memberId) {
        Member member = isMemberExists(memberId);
        return FileResponse.builder()
                .uploadFileName(member.getFileName())
                .uploadFileURL(member.getUrl())
                .fileType(member.getFileType())
                .build();
    }

    public List<PostResponse> getMemberLikedPost(Long memberId, Long cursorValue) {
        isMemberExists(memberId);
        return postListRepository.findPostsByCondition(memberId, cursorValue, 10L, "myLike", null, null, null);
    }

    public List<PostResponse> getMemberPostWithFilter(Long memberId, String filterType, LocalDate start, LocalDate end, Long cursorValue, Long likeCnt) {
        return postListRepository.findPostsByCondition(memberId, cursorValue, 10L, filterType, start, end, likeCnt);
    }

    @Transactional
    public void patchMemberCompany(Long memberId, MemberCompanyPatchRequest memberCompanyPatchRequest) {
        Member member = isMemberExists(memberId);

        if (member.getCompany() == null) { //첫 등록시?
            Company company = companySearchService.findByCompanyName(memberCompanyPatchRequest.companyName());
            member.enrollCompany(company);
        } else if (Objects.equals(member.getCompany().getName(), memberCompanyPatchRequest.companyName())) { //직무만 수정
            member.enrollPosition(memberCompanyPatchRequest.position());
        } else { //회사가 달라졌다면?

            Company company = companySearchService.findByCompanyName(memberCompanyPatchRequest.companyName());
            List<Project> projectList = projectRepository.findAllByManagerId(member.getId());

            if (projectList.size() >= 1) {//담당자이면 -> 수정 불가
                throw new BusinessException(ExceptionCode.COMPANY_UPDATE_FORBIDDEN);
            } else { //담당자가 아니면, 참여중인 프로젝트에서 나가기 + 참여중인 일정에서 나가기
                //단 게시글, 일정 내용은 남겨야지 프로젝트에 문제가 없을것 같음
                List<Project> projects = projectParticipationRepository.findProjectsByMemberId(member.getId());
                projects.forEach( //해당 프로젝트의 todo를 담당자에게 넘기기
                        project -> taskRepository.updateTasksByMemberId(project.getManager(),member.getId(),project.getId())
                );
                planParticipationRepository.deleteByMemberId(member.getId());
                projectParticipationRepository.deleteAllByMemberId(member.getId());
            }
            member.enrollCompany(company);
        }
        member.enrollPosition(memberCompanyPatchRequest.position());
    }

    @Transactional
    public void deleteMemberCompany(Long memberId) {

        List<Project> projectList = projectRepository.findAllByManagerId(memberId);

        if (projectList.size() >= 1) {
            throw new BusinessException(ExceptionCode.COMPANY_UPDATE_FORBIDDEN);
        } else {
            projectParticipationRepository.deleteAllByMemberId(memberId);
        }

        Member member = isMemberExists(memberId);

        member.enrollCompany(null);
        member.enrollPosition(null);
    }

    public String getMemberProfileUrl(Long memberId) {
        Member member = memberRepository.findById(memberId).get();
        return member.getUrl();
    }

    private Member isMemberExists(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
