package com.example.cns.feed.post.domain.repository;

import com.example.cns.feed.post.domain.Post;
import com.example.cns.feed.post.domain.PostLike;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Modifying
    @Query("DELETE FROM PostLike pk where pk.member.id = :memberId and pk.post.id = :postId")
    void deletePostLikeByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    @Query("SELECT pk FROM PostLike pk where pk.member.id = :memberId and pk.post.id = :postId")
    Optional<PostLike> findByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    @Query("SELECT pl.post FROM PostLike pl " +
            "WHERE pl.member.id = :memberId AND pl.post.id < :cursorValue " +
            "ORDER BY pl.post.id DESC LIMIT :pageSize")
    List<Post> findPostLikesByMemberId(@Param("memberId") Long memberId, @Param("cursorValue") Long cursorValue, @Param("pageSize") Long pageSize);
}
