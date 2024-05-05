package com.example.cns.chat.domain.repository;

import com.example.cns.chat.domain.ChatParticipation;
import com.example.cns.chat.domain.ChatParticipationID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatParticipationRepository extends JpaRepository<ChatParticipation, ChatParticipationID> {
    @Query("SELECT room.name FROM ChatRoom room WHERE room.id in (SELECT cp.room FROM ChatParticipation cp WHERE cp.member = :memberId)")
    List<String> findChatRoomNameByMemberId(@Param("memberId") Long memberId);

}
