package com.example.cns.project.dto.response;

import com.example.cns.member.dto.response.MemberSearchResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "프로젝트 참여자 조회 반환 DTO")
@Builder
public record ProjectParticipantInfo(
        @Schema(description = "프로젝트 담당자 인덱스값")
        Long managerId,
        @Schema(description = "프로젝트 참여자")
        List<MemberSearchResponse> memberList
) {
}
