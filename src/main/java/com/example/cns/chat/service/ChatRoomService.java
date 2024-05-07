package com.example.cns.chat.service;

import com.example.cns.chat.domain.ChatParticipation;
import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.domain.repository.ChatParticipationRepository;
import com.example.cns.chat.domain.repository.ChatRepository;
import com.example.cns.chat.domain.repository.ChatRoomRepository;
import com.example.cns.chat.dto.ChatRoomCreateRequest;
import com.example.cns.chat.dto.ChatRoomResponse;
import com.example.cns.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipationRepository chatParticipationRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> findChatRoomsByPage(Long memberId, int pageNumber) {
        // 회원이 참여하고 있는 채팅방 조회 (페이지 단위)
        List<ChatRoom> ChatRoomList = chatRoomRepository.findChatRoomsByMemberIdByPage(PageRequest.of(pageNumber - 1, 10), memberId);

        // 조회된 채팅방이 없는 경우
        if (ChatRoomList.isEmpty())
            return null;

        List<ChatRoomResponse> responses = new ArrayList<>();

        for (ChatRoom chatRoom : ChatRoomList) {
            responses.add(ChatRoomResponse.builder()
                    .roomId(chatRoom.getId())
                    .roomName(chatRoom.getName())
                    .lastChatSendAt(chatRoom.getLastChatSendAt())
                    .isRead(chatParticipationRepository.findIsRead(memberId, chatRoom.getId())) // 회원의 채팅방 읽음 여부 조회
                    .lastChat(chatRoom.getLastChat())
                    .build());
        }

        return responses;
    }

    @Transactional
    public Long createChatRoom(ChatRoomCreateRequest request, Long inviter) {
        // 초대한 사람 리스트에 추가
        request.inviteList().add(inviter);
        ChatRoom chatRoom = request.toChatRoomEntity();
        ChatRoom save = chatRoomRepository.save(chatRoom);

        // 참여 저장
        saveChatParticipation(request.inviteList(), save.getId());

        //todo 채팅반 상단에 초대 멘트를 위한 회원 닉네임 전송 필요 여부?
        return save.getId();
    }

    private void saveChatParticipation(List<Long> inviteList, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).get();
        for (Long guestId : inviteList) {
            chatParticipationRepository.save(ChatParticipation.builder()
                    .member(memberRepository.findById(guestId).get())
                    .room(chatRoom)
                    .build());
        }
    }
}
