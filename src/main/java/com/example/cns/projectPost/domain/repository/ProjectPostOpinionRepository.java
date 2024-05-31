package com.example.cns.projectPost.domain.repository;

import com.example.cns.projectPost.domain.ProjectPostOpinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectPostOpinionRepository extends JpaRepository<ProjectPostOpinion, Long> {

    @Query("SELECT ppo FROM ProjectPostOpinion ppo WHERE ppo.member.id = :memberId AND ppo.post.id = :postId")
    Optional<ProjectPostOpinion> findByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    void deleteProjectPostOpinionByMemberIdAndPostId(Long memberId, Long postId);
}
