package com.example.cns.hashtag.domain.repository;

import com.example.cns.hashtag.domain.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface HashTagRepository extends JpaRepository<HashTag, Long> {
    Optional<HashTag> findByName(String name);

    @Query("SELECT htp.id.post, ht.name FROM HashTagPost htp JOIN HashTag ht ON htp.id.hashtag = ht.id WHERE htp.id.post IN :postIds")
    List<Object[]> findHashTagNamesByPostIds(@Param("postIds") List<Long> postIds);
}
