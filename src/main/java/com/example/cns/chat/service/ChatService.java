package com.example.cns.chat.service;

import com.example.cns.chat.domain.Chat;
import com.example.cns.chat.domain.ChatFile;
import com.example.cns.chat.domain.ChatFileRepository;
import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.domain.repository.ChatListRepositoryImpl;
import com.example.cns.chat.domain.repository.ChatRepository;
import com.example.cns.chat.domain.repository.ChatRoomRepository;
import com.example.cns.chat.dto.request.FileMessageFormat;
import com.example.cns.chat.dto.request.TextMessageFormat;
import com.example.cns.chat.dto.response.ChatResponse;
import com.example.cns.feed.post.dto.response.FileResponse;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Long saveTextMessage(TextMessageFormat message) {
        Member sender = memberRepository.findById(message.memberId()).get();
        ChatRoom chatRoom = chatRoomRepository.findById(message.roomId()).get();

        // 채팅 저장
        Chat save = chatRepository.save(message.toChatEntity(chatRoom, sender));

        return save.getId();
    }

    @Transactional
    public Long saveImageMessage(FileMessageFormat fileMessage, FileResponse fileInfo) {
        Member sender = memberRepository.findById(fileMessage.memberId()).get();
        ChatRoom chatRoom = chatRoomRepository.findById(fileMessage.roomId()).get();


        Chat newChat = Chat.builder().chatRoom(chatRoom)
                .from(sender)
                .content(fileInfo.uploadFileURL())
                .messageType(fileMessage.messageType())
                .createdAt(fileMessage.createdAt())
                .subjectId(null)
                .build();
        // 채팅 저장
        Chat save = chatRepository.save(newChat);

        // 채팅 파일 저장
        ChatFile chatFile = ChatFile.builder().fileName(fileInfo.uploadFileName())
                .url(fileInfo.uploadFileURL())
                .fileType(fileInfo.fileType())
                .createdAt(fileMessage.createdAt())
                .chat(save)
                .build();

        chatFileRepository.save(chatFile);

        return save.getId();
    }

    @Transactional(readOnly = true)
    public List<ChatResponse> getPaginationChat(Long roomId, Long chatId) {
        // 스크롤에 따라 no offset 페이징
        return chatListRepository.paginationChat(roomId, chatId, 10);
    }
}
