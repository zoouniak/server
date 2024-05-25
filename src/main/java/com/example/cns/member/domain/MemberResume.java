package com.example.cns.member.domain;

import com.example.cns.common.FileEntity;
import com.example.cns.common.type.FileType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "member_resume")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberResume extends FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public MemberResume(Member member, String url, String fileName, LocalDateTime createdAt, FileType fileType) {
        this.member = member;
        this.setUrl(url);
        this.setFileName(fileName);
        this.setCreatedAt(createdAt);
        this.setFileType(fileType);
    }

}
