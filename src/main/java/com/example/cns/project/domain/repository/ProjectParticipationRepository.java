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

    @Query("SELECT pp FROM ProjectParticipation pp WHERE pp.member = :memberId")
    List<ProjectParticipation> findProjectIdsByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM ProjectParticipation pp WHERE pp.member = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM ProjectParticipation pp WHERE pp.member = :memberId and pp.project = :projectId")
    void deleteById(@Param("memberId") Long memberId, @Param("projectId") Long projectId);

    @Query("SELECT pp FROM ProjectParticipation pp WHERE pp.project = :projectId")
    List<ProjectParticipation> findProjectParticipationsByProjectId(@Param("projectId") Long projectId);
}
