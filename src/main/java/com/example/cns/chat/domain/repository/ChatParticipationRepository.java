package com.example.cns.chat.domain.repository;

import com.example.cns.chat.domain.ChatParticipation;
import com.example.cns.chat.domain.ChatParticipationID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatParticipationRepository extends JpaRepository<ChatParticipation, ChatParticipationID> {
   @Query("SELECT cp.isRead from ChatParticipation cp where cp.member.id = :memberId and cp.room.id=:roomId")
   boolean findIsRead(@Param("memberId") Long memberId, @Param("roomId") Long roomId);
}
