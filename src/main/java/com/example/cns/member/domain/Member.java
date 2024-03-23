package com.example.cns.member.domain;

import com.example.cns.common.FileEntity;
import com.example.cns.company.domain.Company;
import com.example.cns.member.type.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

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
    private Date birth;

    @Column
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Builder
    public Member(Long id, String username, String password, String email, String firstName, String lastName, Date birth, RoleType role, String position) {
        this.id = id;
        this.username = username;
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
}
