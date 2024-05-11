package com.example.cns.chat.service;

import com.example.cns.chat.domain.Chat;
import com.example.cns.chat.domain.ChatFile;
import com.example.cns.chat.domain.ChatFileRepository;
import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.domain.repository.ChatListRepositoryImpl;
import com.example.cns.chat.domain.repository.ChatRepository;
import com.example.cns.chat.domain.repository.ChatRoomRepository;
import com.example.cns.chat.dto.request.ImageMessageFormat;
import com.example.cns.chat.dto.request.TextMessageFormat;
import com.example.cns.chat.dto.response.ChatResponse;
import com.example.cns.feed.post.dto.response.PostFileResponse;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatListRepositoryImpl chatListRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatFileRepository chatFileRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveTextMessage(TextMessageFormat message) {
        Member sender = memberRepository.findById(message.memberId()).get();
        ChatRoom chatRoom = chatRoomRepository.findById(message.roomId()).get();

        LocalDateTime now = LocalDateTime.now();
        // 채팅방의 마지막 채팅 갱신
        chatRoom.saveLastChat(message.content(), now);

        // 채팅 저장
        Chat chat = message.toChatEntity(chatRoom, sender, now);
        chatRepository.save(chat);
    }

    @Transactional
    public void saveImageMessage(ImageMessageFormat imageMessage, List<PostFileResponse> fileResponses) {
        Member sender = memberRepository.findById(imageMessage.memberId()).get();
        ChatRoom chatRoom = chatRoomRepository.findById(imageMessage.roomId()).get();

        LocalDateTime now = LocalDateTime.now();
        // 채팅방의 마지막 채팅 갱신
        chatRoom.saveLastChat("", now);

        // 채팅 저장
        Chat save = chatRepository.save(imageMessage.toChatEntity(chatRoom, sender, now));

        // 채팅 파일 저장
        for (PostFileResponse fileInfo : fileResponses) {
            ChatFile chatFile = ChatFile.builder().fileName(fileInfo.uploadFileName())
                    .url(fileInfo.uploadFileURL())
                    .fileType(fileInfo.fileType())
                    .createdAt(now)
                    .chat(save)
                    .build();
            chatFileRepository.save(chatFile);
        }
    }

    @Transactional(readOnly = true)
    public List<ChatResponse> getPaginationChat(Long roomId, Long chatId) {
        // 스크롤에 따라 no offset 페이징
        return chatListRepository.paginationChat(roomId, chatId, 10);
    }
}
