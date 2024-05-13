package com.example.cns.feed.comment.domain.repository;

import com.example.cns.feed.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT c, CASE WHEN (SELECT COUNT(cl) FROM CommentLike cl WHERE cl.comment = c AND cl.member.id = :memberId) > 0 THEN true ELSE false END AS userLiked " +
            "FROM Comment c " +
            "WHERE c.post.id = :postId AND c.parentComment IS NULL " +
            "ORDER BY c.createdAt ASC")
    List<Object[]> findAllCommentByPostIdWithUserLiked(@Param("postId") Long postId, @Param("memberId") Long memberId);

    @Query(value = "SELECT c, CASE WHEN (SELECT COUNT(cl) FROM CommentLike cl WHERE cl.comment = c AND cl.member.id = :memberId) > 0 THEN true ELSE false END AS userLiked " +
            "FROM Comment c " +
            "WHERE c.post.id = :postId AND c.parentComment.id = :commentId " +
            "ORDER BY c.createdAt ASC")
    List<Object[]> findAllCommentReplyByPostIdWithUserLiked(@Param("postId") Long postId, @Param("memberId") Long memberId, @Param("commentId") Long commentId);
}
