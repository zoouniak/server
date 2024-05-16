package com.example.cns.project.domain;

import com.example.cns.chat.domain.ChatParticipationID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import org.springframework.data.domain.Persistable;

@Entity
@Getter
@IdClass(ProjectParticipationID.class)
public class ProjectParticipation implements Persistable<ProjectParticipationID> {
    @Id
    @Column(name = "member_id")
    private Long member;

    @Id
    @Column(name = "project_id")
    private Long project;

    @Override
    public ProjectParticipationID getId() {
        return new ProjectParticipationID(this.member,this.project);
    }

    @Override
    public boolean isNew() {
        return this.member != null && this.project != null;
    }
}
