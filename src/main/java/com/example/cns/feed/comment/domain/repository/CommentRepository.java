package com.example.cns.feed.comment.domain.repository;

import com.example.cns.feed.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId and c.parentComment is null ORDER BY c.createdAt ASC")
    List<Comment> findAllCommentByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId and c.parentComment.id = :commentId ORDER BY c.createdAt ASC")
    List<Comment> findAllCommentReplyByPostId(@Param("postId") Long postId, @Param("commentId") Long commentId);
}
