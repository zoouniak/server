package com.example.cns.chat.domain.repository;

import com.example.cns.chat.domain.ChatFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatFileRepository extends JpaRepository<ChatFile, Long> {
}
