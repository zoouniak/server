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

    @Query("SELECT p FROM ProjectParticipation p WHERE p.member = :memberId")
    List<ProjectParticipation> findProjectIdsByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM ProjectParticipation p WHERE p.member = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
