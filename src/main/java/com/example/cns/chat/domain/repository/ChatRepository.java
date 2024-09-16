package com.example.cns.chat.domain.repository;

import com.example.cns.chat.domain.Chat;
import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.type.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> getAllByChatRoomAndMessageType(ChatRoom chatRoom, MessageType type);

    List<Chat> findAllByIdBetween(Long startId, Long endId);

    @Query(value = "SELECT * FROM chat WHERE chat_room_id = :roomId AND MATCH(content) AGAINST(:keyword IN BOOLEAN MODE) ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<Chat> searchChat(@Param("keyword") String keyword, @Param("roomId") Long roomId);

    @Query(value = "SELECT * FROM chat WHERE chat_room_id = :roomId AND MATCH(content) AGAINST(:keyword IN BOOLEAN MODE) AND id < :chatId ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<Chat> searchChatLtThanChatId(@Param("keyword") String keyword, @Param("roomId") Long roomId, @Param("chatId") Long chatId);

}
