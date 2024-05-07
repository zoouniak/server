package com.example.cns.chat.domain.repository;

import com.example.cns.chat.domain.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select room from ChatRoom  room where room.id in (select cp.room.id from ChatParticipation  cp where cp.member.id = :memberId) order by room.lastChatSendAt desc")
    List<ChatRoom> findChatRoomsByMemberIdByPage(Pageable pageable, @Param("memberId") Long memberId);
}
