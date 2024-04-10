package com.example.cns.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class PlanParticipation {
    @Id
    @Column(name = "member_id")
    private Long member;

    @Id
    @Column(name = "plan_id")
    private Long plan;
}
