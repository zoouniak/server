package com.example.cns.project.plan.domain.repository;

import com.example.cns.project.plan.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    @Query("select p from Plan p where p.project.id=:projectId")
    List<Plan> getAllByProject(@Param("projectId") Long projectId);
}
