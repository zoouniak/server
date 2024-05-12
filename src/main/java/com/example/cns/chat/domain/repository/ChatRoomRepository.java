package com.example.cns.chat.domain.repository;

import com.example.cns.chat.domain.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select room from ChatRoom as room where room.id in (select cp.room from ChatParticipation  as cp where cp.member = :memberId)")
    List<ChatRoom> getMyChatRoomsByPage(Pageable pageable, @Param("memberId") Long memberId);

}