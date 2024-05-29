package com.example.cns.plan.domain.repository;

import com.example.cns.plan.domain.PlanParticipation;
import com.example.cns.plan.domain.PlanParticipationID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlanParticipationRepository extends JpaRepository<PlanParticipation, PlanParticipationID> {
    @Query("select pp from PlanParticipation pp where pp.plan = :planId")
    List<PlanParticipation> findAllByPlanId(@Param("planId") Long planId);

    @Modifying
    @Query("delete from PlanParticipation pp where pp.plan = :planId and pp.member = :memberId")
    void deleteByPlanAndMember(@Param("planId") Long planId, @Param("memberId") Long memberId);

    @Modifying
    @Query("delete from PlanParticipation pp where pp.member = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    void deleteByPlan(Long planId);
}
