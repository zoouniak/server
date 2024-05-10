package com.example.cns.feed.post.domain.repository;

import com.example.cns.feed.post.domain.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostFileRepository extends JpaRepository<PostFile, Long> {

    @Query("SELECT p FROM PostFile p WHERE p.post.id = :postId ORDER BY p.createdAt ASC")
    List<PostFile> findAllByPostId(@Param("postId") Long postId);
}
