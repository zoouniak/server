package com.example.cns.hashtag.domain.repository;

import com.example.cns.hashtag.domain.HashTagView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashTagViewRepository extends JpaRepository<HashTagView,Long> {
    @Query("SELECT hv FROM hashtag_view hv where hv.name= :keyword")
    Optional<HashTagView> findHashTagViewByName(@Param("keyword") String keyword);
}
