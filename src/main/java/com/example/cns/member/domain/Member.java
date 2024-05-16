package com.example.cns.member.domain;

import com.example.cns.common.FileEntity;
import com.example.cns.company.domain.Company;
import com.example.cns.feed.post.domain.Post;
import com.example.cns.feed.post.domain.PostLike;
import com.example.cns.feed.post.dto.response.FileResponse;
import com.example.cns.member.type.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String introduction;

    @Column
    private String position;

    @Column
    private LocalDate birth;

    @Column
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column(name = "is_profile_existed")
    @ColumnDefault("false")
    private Boolean isProfileExisted;

    @Column(name = "is_resume_existed")
    @ColumnDefault("false")
    private Boolean isResumeExisted;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder
    public Member(Long id, String nickname, String password, String email, String firstName, String lastName, LocalDate birth, RoleType role, String position) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birth = birth;
        this.role = role;
        this.position = position;
    }

    public void enrollCompany(Company company) {
        this.company = company;
    }

    public void enrollPosition(String position) {
        this.position = position;
    }

    public void resetPassword(String password) {
        this.password = password;
    }

    public void updateInformation(String introduction, LocalDate birth) {
        this.introduction = introduction;
        this.birth = birth;
    }

    public void updateProfile(FileResponse fileResponse) {
        if (fileResponse == null) {
            clearProfile();
        } else {
            setProfile(fileResponse);
        }
    }

    public void updateResume(Boolean isResumeExisted) {
        this.isResumeExisted = isResumeExisted;
    }

    private void clearProfile() {
        this.setFileName(null);
        this.setUrl(null);
        this.setFileType(null);
        this.setCreatedAt(null);
        this.isProfileExisted = false;
    }

    private void setProfile(FileResponse fileResponse) {
        this.setFileName(fileResponse.uploadFileName());
        this.setUrl(fileResponse.uploadFileURL());
        this.setFileType(fileResponse.fileType());
        this.setCreatedAt(LocalDateTime.now());
        this.isProfileExisted = true;
    }
}
