package com.example.cns.project.domain.repository;

import com.example.cns.project.domain.ProjectParticipation;
import com.example.cns.project.domain.ProjectParticipationID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectParticipationRepository extends JpaRepository<ProjectParticipation, ProjectParticipationID> {

    @Modifying
    @Query("DELETE FROM ProjectParticipation pp WHERE pp.member = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM ProjectParticipation pp WHERE pp.member = :memberId and pp.project = :projectId")
    void deleteById(@Param("memberId") Long memberId, @Param("projectId") Long projectId);

    @Query("SELECT pp FROM ProjectParticipation pp WHERE pp.project = :projectId")
    List<ProjectParticipation> findProjectParticipationsByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT pp.member FROM ProjectParticipation pp WHERE pp.project = :projectId")
    List<Long> findProjectParticipationsIdByProjectId(@Param("projectId") Long projectId);

    @Modifying
    @Query("DELETE FROM ProjectParticipation pp WHERE pp.project = :projectId")
    void deleteAllByProjectId(@Param("projectId") Long projectId);
}
