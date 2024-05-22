package com.example.cns.project.plan.domain.repository;

import com.example.cns.project.plan.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> getAllByProject(Long projectId);
}
