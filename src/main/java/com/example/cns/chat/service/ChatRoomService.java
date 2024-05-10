package com.example.cns.chat.service;

import com.example.cns.chat.domain.ChatParticipation;
import com.example.cns.chat.domain.ChatParticipationID;
import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.domain.repository.ChatParticipationRepository;
import com.example.cns.chat.domain.repository.ChatRoomRepository;
import com.example.cns.chat.dto.request.ChatRoomCreateRequest;
import com.example.cns.chat.dto.response.ChatParticipantsResponse;
import com.example.cns.chat.dto.response.ChatRoomResponse;
import com.example.cns.common.exception.BusinessException;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.example.cns.common.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
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

    /*
     * 채팅방 생성
     */
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

    /*
     * 채팅 참여 정보 저장
     */
    private void saveChatParticipation(List<Long> inviteList, Long roomId) {
        // 채팅 참여 저장 전 채팅방 존재 검증
        verifyRoomId(roomId);
        for (Long guestId : inviteList) {
            // 사용자 존재 검증
            verifyMember(guestId);
            chatParticipationRepository.save(ChatParticipation.builder()
                    .member(guestId)
                    .room(roomId)
                    .build());
        }
    }

    /*
     * 채팅 참여 정보 삭제
     */
    @Transactional
    public void leaveChatRoom(Long memberId, Long roomId) {
        chatParticipationRepository.deleteByMemberAndRoom(memberId, roomId);
    }

    @Transactional(readOnly = true)
    public List<ChatParticipantsResponse> getChatParticipants(Long roomId, Long memberId) {
        // 채팅방 존재 여부 검증
        verifyChatRoom(roomId);
        // 요청 회원의 채팅방 참여자 여부 검증
        verifyMemberInChatRoom(memberId, roomId);

        // 참여자 조회
        List<ChatParticipation> participants = chatParticipationRepository.findMemberByRoom(roomId);

        List<ChatParticipantsResponse> responses = new ArrayList<>();

        for (ChatParticipation participant : participants) {
            Member member = memberRepository.findById(participant.getMember()).get();
            responses.add(new ChatParticipantsResponse(
                    member.getId(),
                    member.getNickname()
            ));
        }
        return responses;
    }

    private void verifyMember(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
    }

    private void verifyRoomId(Long roomId) {
        chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ChatROOM_NOT_EXIST));
    }


    private void verifyMemberInChatRoom(Long memberId, Long roomId) {
        chatParticipationRepository.findById(new ChatParticipationID(memberId, roomId))
                .orElseThrow(() -> new BusinessException(NOT_PARTICIPANTS));
    }

    private void verifyChatRoom(Long roomId) {
        chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ChatROOM_NOT_EXIST));
    }
}
