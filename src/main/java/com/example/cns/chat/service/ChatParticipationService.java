package com.example.cns.chat.service;

import com.example.cns.chat.domain.ChatParticipation;
import com.example.cns.chat.domain.ChatParticipationID;
import com.example.cns.chat.domain.repository.ChatParticipationRepository;
import com.example.cns.chat.domain.repository.ChatRepository;
import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatParticipationService {
    private final ChatParticipationRepository chatParticipationRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public void updateMemberLastChat(Long memberId, Long roomId) {
        ChatParticipation chatParticipation = chatParticipationRepository.findById(new ChatParticipationID(memberId, roomId))
                .orElseThrow(() -> new BusinessException(ExceptionCode.PARTICIPANT_NOT_EXIST));
        Long lastChat = chatRepository.findLastChat(roomId, PageRequest.of(0, 1)).get(0);
        chatParticipation.updateLastChat(lastChat);
        System.out.println(lastChat);
    }

    @Transactional
    public void removeMemberLastChat(Long memberId, Long roomId) {
        ChatParticipation chatParticipation = chatParticipationRepository.findById(new ChatParticipationID(memberId, roomId))
                .orElseThrow(() -> new BusinessException(ExceptionCode.PARTICIPANT_NOT_EXIST));
        Long lastChat = chatRepository.findLastChat(roomId, PageRequest.of(0, 1)).get(0);
        chatParticipation.updateLastChat(null);
        System.out.println(lastChat);
    }
}
