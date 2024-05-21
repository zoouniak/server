package com.example.cns.chat.service;

import com.example.cns.chat.domain.repository.ChatListRepositoryImpl;
import com.example.cns.chat.domain.repository.ChatRoomRepository;
import com.example.cns.chat.dto.response.LastChatInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * 각 채팅방의 마지막 채팅 정보를 주기적으로 갱신한다.
 */
@Service
@RequiredArgsConstructor
public class LastChatUpdateService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatListRepositoryImpl chatRepository;

    @Scheduled(cron = "* * 1 * * *") // 10초마다 실행
    @Transactional
    public void updateLastChat() {
        List<LastChatInfo> lastChatByChatRoom = chatRepository.getLastChatByChatRoom();
        for (LastChatInfo lastChatInfo : lastChatByChatRoom) {
            chatRoomRepository.findById(lastChatInfo.roomId()).ifPresent(chatRoom -> chatRoom.updateLastChat(lastChatInfo.lastChatId()));
        }
    }
}
