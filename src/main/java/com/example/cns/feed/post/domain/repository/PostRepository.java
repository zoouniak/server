package com.example.cns.feed.post.domain.repository;

import com.example.cns.feed.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "SELECT * FROM Post p WHERE p.id < :cursorValue ORDER BY p.id DESC LIMIT :pageSize", nativeQuery = true)
    List<Post> findPostsByCursor(@Param("pageSize") int pageSize, @Param("cursorValue") Long cursorValue);

    @Query("SELECT MAX(p.id) FROM Post p")
    Long getMaxPostId();

    @Query(value = "SELECT p, CASE WHEN (SELECT COUNT(pl) FROM PostLike pl WHERE pl.post = p AND pl.member.id = :memberId) > 0 THEN true ELSE false END AS userLiked " +
            "FROM Post p " +
            "WHERE p.id < :cursorValue " +
            "ORDER BY p.id DESC LIMIT :pageSize")
    List<Object[]> findPostsAndUserLikesWithCursor(@Param("memberId") Long memberId, @Param("cursorValue") Long cursorValue,@Param("pageSize") Long pageSize);
}
