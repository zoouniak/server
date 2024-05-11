package com.example.cns.chat.domain.repository;

import com.example.cns.chat.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
