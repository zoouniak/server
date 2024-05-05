package com.example.cns.chat.service;

import com.example.cns.chat.domain.Chat;
import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.domain.repository.ChatRepository;
import com.example.cns.chat.domain.repository.ChatRoomRepository;
import com.example.cns.chat.dto.MessageFormat;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveMessage(MessageFormat message) {
        Member sender = memberRepository.findById(message.memberId()).get();
        ChatRoom chatRoom = chatRoomRepository.findById(message.roomId()).get();

        Chat chat = message.toChatEntity(chatRoom, sender);
        chatRepository.save(chat);
    }

}
