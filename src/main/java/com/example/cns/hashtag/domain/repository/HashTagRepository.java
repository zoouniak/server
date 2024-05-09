package com.example.cns.hashtag.domain.repository;

import com.example.cns.hashtag.domain.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashTagRepository extends JpaRepository<HashTag,Long> {
    List<HashTag> findAllByNameContainingIgnoreCase(String name);
    Optional<HashTag> findByName(String name);

    @Query("SELECT hp.id.hashtag, COUNT(hp.id.post) FROM HashTagPost hp GROUP BY hp.id.hashtag")
    List<Object[]> countPostsByHashTag();

}
