package com.example.cns.hashtag.domain.repository;

import com.example.cns.hashtag.domain.HashTagPost;
import com.example.cns.hashtag.domain.HashTagPostId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HashTagPostRepository extends JpaRepository<HashTagPost, HashTagPostId> {
    List<HashTagPost> findAllById(HashTagPostId id);

    @Query("SELECT h FROM HashTagPost h WHERE h.id.post = :postId")
    List<HashTagPost> findAllByPostId(@Param("postId") Long postId);

    @Query("SELECT h FROM HashTagPost h WHERE h.id.hashtag = :hashtagId")
    List<HashTagPost> findAllByHashTagId(@Param("hashtagId") Long hashtagId);

    @Modifying
    @Query("DELETE FROM HashTagPost hp WHERE hp.id.post = :postId and hp.id.hashtag = :hashtagId")
    void deleteHashTagPostById_PostAndId_Hashtag(@Param("postId") Long postId, @Param("hashtagId") Long hashtagId);

}
