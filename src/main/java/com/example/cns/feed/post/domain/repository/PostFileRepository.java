package com.example.cns.feed.post.domain.repository;

import com.example.cns.feed.post.domain.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostFileRepository extends JpaRepository<PostFile,Long> {

}
