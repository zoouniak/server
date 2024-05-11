package com.example.cns.feed.comment.domain.repository;

import com.example.cns.feed.comment.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    @Query("SELECT cl FROM CommentLike cl WHERE cl.member.id = :memberId AND cl.comment.id = :commentId")
    Optional<CommentLike> findByMemberIdAndCommentId(@Param("memberId") Long memberId, @Param("commentId") Long commentId);

    @Modifying
    @Query("DELETE FROM CommentLike cl WHERE cl.member.id = :memberId AND cl.comment.id = :commentId")
    void deleteByMemberIdAndCommentId(@Param("memberId") Long memberId, @Param("commentId") Long commentId);
}
