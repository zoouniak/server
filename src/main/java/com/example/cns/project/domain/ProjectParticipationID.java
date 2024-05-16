package com.example.cns.project.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class ProjectParticipationID implements Serializable{
    private Long member;
    private Long project;
}