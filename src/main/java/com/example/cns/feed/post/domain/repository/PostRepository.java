package com.example.cns.feed.post.domain.repository;

import com.example.cns.feed.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT MAX(p.likeCnt) FROM Post p where p.member.id = :memberId")
    Long getMaxLikeCntByMemberId(@Param("memberId") Long memberId);
}
