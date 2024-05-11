package com.example.cns.feed.post.domain.repository;

import com.example.cns.feed.post.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Modifying
    @Query("DELETE FROM PostLike pk where pk.member.id = :memberId and pk.post.id = :postId")
    void deletePostLikeByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    @Query("SELECT pk FROM PostLike pk where pk.member.id = :memberId and pk.post.id = :postId")
    Optional<PostLike> findByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    @Query("SELECT CASE WHEN COUNT(pl) > 0 THEN true ELSE false END FROM PostLike pl WHERE pl.post.id = :postId AND pl.member.id = :memberId")
    boolean existsByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

}
