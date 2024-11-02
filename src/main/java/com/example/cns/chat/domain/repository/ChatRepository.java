package com.example.cns.chat.domain.repository;

import com.example.cns.chat.domain.Chat;
import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.type.MessageType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> getAllByChatRoomAndMessageType(ChatRoom chatRoom, MessageType type);

    @Query("select c.id from Chat c where c.chatRoom.id = :roomId order by c.id desc")
    List<Long> findLastChat(@Param("roomId") Long roomId, Pageable pageable);
}
