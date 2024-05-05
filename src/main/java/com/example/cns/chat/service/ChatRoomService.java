package com.example.cns.chat.service;

import com.example.cns.chat.domain.ChatParticipation;
import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.domain.repository.ChatParticipationRepository;
import com.example.cns.chat.domain.repository.ChatRoomRepository;
import com.example.cns.chat.dto.ChatRoomCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipationRepository chatParticipationRepository;

    @Transactional(readOnly = true)
    public List<String> findAllChatroom(Long memberId) {
        // 내가 참여하고 있는 모든 채팅방 조회
        // select name from chat_room where id=( select room_id from chat_participation where member_id = 'memberId');
        return chatParticipationRepository.findChatRoomNameByMemberId(memberId);
    }

    @Transactional
    public Long createChatRoom(ChatRoomCreateRequest request, Long inviter) {
        // 초대한 사람 리스트에 추가
        request.inviteList().add(inviter);
        ChatRoom chatRoom = request.toChatRoomEntity();
        ChatRoom save = chatRoomRepository.save(chatRoom);

        System.out.println("here?");
        // 참여 저장
        saveChatParticipation(request.inviteList(), save.getId());

        //todo 채팅반 상단에 초대 멘트를 위한 회원 닉네임 전송 필요 여부?
        return save.getId();
    }

    private void saveChatParticipation(List<Long> inviteList, Long roomId) {
        for (Long guestId : inviteList) {
            chatParticipationRepository.save(ChatParticipation.builder()
                    .member(guestId)
                    .room(roomId)
                    .build());
        }
    }
}
