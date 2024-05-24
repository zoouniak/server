package com.example.cns.plan.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanParticipationID implements Serializable {
    private Long member;
    private Long plan;
}
