package com.example.cns.projectPost.projectPost.domain.repository;

import com.example.cns.projectPost.projectPost.domain.ProjectPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectPostRepository extends JpaRepository<ProjectPost, Long> {

    @Query("SELECT pp FROM ProjectPost pp WHERE pp.id = :postId AND pp.project.id = :projectId")
    Optional<ProjectPost> findByProjectIdAndPostId(@Param("projectId") Long projectId, @Param("postId") Long postId);

}
