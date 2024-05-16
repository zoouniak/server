package com.example.cns.feed.post.domain.repository;

import com.example.cns.feed.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT MAX(p.id) FROM Post p")
    Long getMaxPostId();

    @Query("SELECT p, CASE WHEN (SELECT COUNT(pl) FROM PostLike pl WHERE pl.post = p AND pl.member.id = :memberId) > 0 THEN true ELSE false END AS userLiked " +
            "FROM Post p " +
            "WHERE p.id < :cursorValue " +
            "ORDER BY p.id DESC LIMIT :pageSize")
    List<Object[]> findPostsAndUserLikesWithCursor(@Param("memberId") Long memberId, @Param("cursorValue") Long cursorValue, @Param("pageSize") Long pageSize);

    @Query("SELECT p,CASE WHEN (SELECT COUNT(pl) FROM PostLike pl WHERE pl.post = p AND pl.member.id = :memberId) > 0 THEN true ELSE false END AS userLiked " +
            "FROM Post p " +
            "WHERE p.member.id = :memberId AND p.id < :cursorValue " +
            "ORDER BY p.createdAt DESC LIMIT :pageSize")
    List<Object[]> findNewestPosts(@Param("memberId") Long memberId, @Param("cursorValue") Long cursorValue, @Param("pageSize") Long pageSize);

    @Query("SELECT p,CASE WHEN (SELECT COUNT(pl) FROM PostLike pl WHERE pl.post = p AND pl.member.id = :memberId) > 0 THEN true ELSE false END AS userLiked " +
            "FROM Post p " +
            "WHERE p.member.id = :memberId AND p.id < :cursorValue " +
            "ORDER BY p.createdAt ASC LIMIT :pageSize")
    List<Object[]> findOldestPosts(@Param("memberId") Long memberId, @Param("cursorValue") Long cursorValue, @Param("pageSize") Long pageSize);

    @Query("SELECT p,CASE WHEN (SELECT COUNT(pl) FROM PostLike pl WHERE pl.post = p AND pl.member.id = :memberId) > 0 THEN true ELSE false END AS userLiked " +
            "FROM Post p " +
            "WHERE p.member.id = :memberId AND p.id < :cursorValue " +
            "AND p.createdAt between :start AND :end " +
            "ORDER BY p.createdAt ASC LIMIT :pageSize")
    List<Object[]> findPostsByPeriod(@Param("memberId") Long memberId, @Param("cursorValue") Long cursorValue, @Param("pageSize") Long pageSize, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p,CASE WHEN (SELECT COUNT(pl) FROM PostLike pl WHERE pl.post = p AND pl.member.id = :memberId) > 0 THEN true ELSE false END AS userLiked " +
            "FROM Post p " +
            "WHERE p.member.id = :memberId AND p.id < :cursorValue " +
            "ORDER BY p.likeCnt DESC, p.createdAt DESC LIMIT :pageSize")
    List<Object[]> findPostsByLikeCnt(@Param("memberId") Long memberId, @Param("cursorValue") Long cursorValue, @Param("pageSize") Long pageSize);

}
