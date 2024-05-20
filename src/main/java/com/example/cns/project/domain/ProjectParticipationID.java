package com.example.cns.project.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectParticipationID implements Serializable {
    private Long member;
    private Long project;
}