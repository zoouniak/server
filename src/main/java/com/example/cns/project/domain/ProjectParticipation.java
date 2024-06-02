package com.example.cns.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ProjectParticipationID.class)
public class ProjectParticipation implements Persistable<ProjectParticipationID> {
    @Id
    @Column(name = "member_id")
    private Long member;

    @Id
    @Column(name = "project_id")
    private Long project;

    @Builder
    public ProjectParticipation(Long member, Long project) {
        this.member = member;
        this.project = project;
    }

    @Override
    public ProjectParticipationID getId() {
        return new ProjectParticipationID(this.member, this.project);
    }

    @Override
    public boolean isNew() {
        return this.member != null && this.project != null;
    }

}
