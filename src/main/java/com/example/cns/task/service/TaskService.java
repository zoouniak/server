package com.example.cns.task.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.project.domain.Project;
import com.example.cns.project.domain.repository.ProjectParticipationRepository;
import com.example.cns.project.domain.repository.ProjectRepository;
import com.example.cns.task.domain.Task;
import com.example.cns.task.domain.repository.TaskRepository;
import com.example.cns.task.dto.request.TaskCreateRequest;
import com.example.cns.task.dto.request.TaskEditRequest;
import com.example.cns.task.dto.request.TaskUpdateStateRequest;
import com.example.cns.task.dto.response.TaskListResponse;
import com.example.cns.task.dto.response.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.cns.common.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectParticipationRepository projectParticipationRepository;

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
    }

    private Project getProject(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new BusinessException(PROJECT_NOT_EXIST));
    }

    private Task getTask(Long todoId) {
        return taskRepository.findById(todoId).orElseThrow(() -> new BusinessException(TASK_NOT_EXIST));
    }

    public Long createTask(Long memberId, Long projectId, TaskCreateRequest request) {
        Member member = getMember(memberId);
        Project project = getProject(projectId);
        Task save = taskRepository.save(request.toEntity(member, project));
        return save.getId();
    }

    @Transactional
    public void editTask(Long memberId, Long todoId, TaskEditRequest request) {
        Member member = getMember(memberId);
        Task task = getTask(todoId);
        task.updateContent(request.content());
    }

    @Transactional
    public void deleteTask(Long memberId, Long todoId) {
        taskRepository.deleteById(todoId);
    }

    public void ValidateTaskByMember(Long memberId, Long taskId) {
        Member member = getMember(memberId);
        if (!taskRepository.existsByIdAndMember(taskId, member))
            throw new BusinessException(NOT_TASK_OWNER);
    }

    @Transactional
    public void updateTaskState(Long memberId, Long todoId, TaskUpdateStateRequest request) {
        Task task = getTask(todoId);
        task.updateState(request.state());
    }

    public void getAllTaskByProject(Long projectId) {
        List<Long> memberList = projectParticipationRepository.findProjectParticipationsIdByProjectId(projectId);
    }

    public TaskListResponse getMyTaskByProject(Long memberId, Long projectId) {
        Member member = getMember(memberId);
        Project project = getProject(projectId);

        List<TaskResponse> taskList = taskRepository.findAllByMemberAndProject(member, project).stream()
                .map((task) -> new TaskResponse(task.getContent(), task.getState())).collect(Collectors.toList());
        return new TaskListResponse(member.getNickname(), taskList);
    }
}
