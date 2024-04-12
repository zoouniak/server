package com.example.cns.project.domain;

import com.example.cns.company.domain.Company;
import com.example.cns.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member manager;

    @Column(nullable = false)
    private String projectName;

    @Column(nullable = false)
    private String goal;

}
