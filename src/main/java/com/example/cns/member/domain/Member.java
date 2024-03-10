package com.example.cns.member.domain;

import com.example.cns.common.FileEntity;
import com.example.cns.member.type.RoleType;
import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Date;

@Entity
@Getter
public class Member extends FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String introduction;

    @Column
    private Date birth;

    @Column
    @Enumerated(EnumType.STRING )
    private RoleType role;
}
