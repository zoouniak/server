package com.example.cns.project.plan.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(PlanParticipationID.class)
public class PlanParticipation implements Persistable<PlanParticipationID> {
    @Id
    @Column(name = "member_id")
    private Long member;

    @Id
    @Column(name = "plan_id")
    private Long plan;

    public PlanParticipation(Long member, Long plan) {
        this.member = member;
        this.plan = plan;
    }

    @Override
    public PlanParticipationID getId() {
        return new PlanParticipationID(this.member, this.plan);
    }

    @Override
    public boolean isNew() {
        return this.member != null && this.plan != null;
    }
}
