package com.example.cns.task.domain.repository;

import com.example.cns.member.domain.Member;
import com.example.cns.project.domain.Project;
import com.example.cns.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByMember(Member member);

    boolean existsByIdAndMember(Long taskId, Member member);

    List<Task> findAllByMemberAndProject(Member member, Project project);
}
