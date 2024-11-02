package com.example.cns.channel.domain;

import com.example.cns.project.domain.Project;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private String name;

    @Builder
    public Channel(String name, Project project) {
        this.name = name;
        this.project = project;
    }

    public void updateChannelName(String name) {
        this.name = name;
    }
}
