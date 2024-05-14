package com.example.cns.member.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.service.S3Service;
import com.example.cns.feed.post.dto.response.FileResponse;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.MemberResume;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.member.domain.repository.MemberResumeRepository;
import com.example.cns.member.dto.request.MemberFileRequest;
import com.example.cns.member.dto.request.MemberProfilePatchRequest;
import com.example.cns.member.dto.response.MemberProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final S3Service s3Service;

    private final MemberRepository memberRepository;
    private final MemberResumeRepository memberResumeRepository;

    public MemberProfileResponse getMemberProfile(Long memberId){

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));

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
    public void patchMemberProfile(Long memberId, MemberProfilePatchRequest memberProfilePatchRequest){
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));

        member.updateInformation(memberProfilePatchRequest.introduction(), memberProfilePatchRequest.birth());
    }

    @Transactional
    public void saveProfile(Long id, MemberFileRequest memberFileRequest) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));

        //해당 유저가 프로필이 존재한다면.. 삭제
        if(member.getIsProfileExisted()) {
            try {
                s3Service.deleteFile(member.getFileName(), "profile");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //프로필 등록
        FileResponse uploaded = s3Service.uploadFile(memberFileRequest.file(), "profile");
        member.updateProfile(uploaded);

    }

    @Transactional
    public void deleteProfile(Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
        if(member.getIsProfileExisted()) {
            try {
                s3Service.deleteFile(member.getFileName(),"profile");
                member.updateProfile(null);
            } catch (IOException e) {
                throw new BusinessException(ExceptionCode.IMAGE_DELETE_FAILED);
            }
        } //프로필 없는 경우 에러 처리
    }

    @Transactional
    public void saveResume(Long memberId, MemberFileRequest memberFileRequest) {

        if(!Objects.equals(memberFileRequest.file().getContentType(), "application/pdf"))
            throw new BusinessException(ExceptionCode.NOT_SUPPORT_EXT);

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));

        //해당 유저가 이력서가 존재한다면?
        if(member.getIsResumeExisted()) {
            Optional<MemberResume> resume = memberResumeRepository.findByMemberId(member.getId());
            try {
                s3Service.deleteFile(resume.get().getFileName(),"resume");
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
    public void deleteResume(Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));

        if(member.getIsResumeExisted()){ //이력서가 존재한다면, 찾아서 삭제
            Optional<MemberResume> resume = memberResumeRepository.findByMemberId(member.getId());
            try {
                s3Service.deleteFile(resume.get().getFileName(),"resume");
                memberResumeRepository.deleteById(resume.get().getId());
                member.updateResume(false);
            } catch (IOException e) {
                throw new BusinessException(ExceptionCode.IMAGE_DELETE_FAILED);
            }
        } //이력서가 없다면 오류

    }

    public FileResponse getMemberResume(Long memberId) {
        Optional<MemberResume> resume = memberResumeRepository.findByMemberId(memberId);
        if(resume.isPresent()){
            return FileResponse.builder()
                    .uploadFileName(resume.get().getFileName())
                    .uploadFileURL(resume.get().getUrl())
                    .fileType(resume.get().getFileType())
                    .build();
        } else {
            return null;
        }
    }
}
