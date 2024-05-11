package com.example.cns.chat.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatFileRepository extends JpaRepository<ChatFile, Long> {
}
