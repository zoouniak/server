package com.example.cns.chat.service;

import com.example.cns.chat.domain.Chat;
import com.example.cns.chat.domain.ChatParticipation;
import com.example.cns.chat.domain.ChatParticipationID;
import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.domain.repository.ChatParticipationRepository;
import com.example.cns.chat.domain.repository.ChatRepository;
import com.example.cns.chat.domain.repository.ChatRoomListRepository;
import com.example.cns.chat.domain.repository.ChatRoomRepository;
import com.example.cns.chat.dto.request.ChatRoomCreateRequest;
import com.example.cns.chat.dto.request.MemberInfo;
import com.example.cns.chat.dto.response.*;
import com.example.cns.chat.event.UserRoomExitEvent;
import com.example.cns.chat.type.MessageType;
import com.example.cns.common.exception.BusinessException;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.cns.common.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipationRepository chatParticipationRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomListRepository chatRoomListRepository;

    private final ApplicationEventPublisher eventPublisher;

    private static void checkCapacity(int size) {
        if (size > 10)
            throw new BusinessException(ROOM_CAPACITY_EXCEEDED);
    }

    /*
     * 회원이 참여하고 있는 채팅방 조회 (페이지 단위), 마지막 채팅 최신순으로
     */
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> findChatRoomsByPage(Long memberId, int page) {
        return chatRoomListRepository.getMyChatRoomList(memberId, 10, page);
    }

    /*
     * 채팅방 생성
     */
    @Transactional
    public ChatRoomCreateResponse createChatRoom(Long memberId, ChatRoomCreateRequest request) {
        // 채팅방 수용 인원 검증
        checkCapacity(request.inviteList().size());

        // 채팅방 저장
        ChatRoom save = chatRoomRepository.save(request.toChatRoomEntity());

        // 초대 메시지 생성
        String msg = createInviteMsg(memberId, request.inviteList());
        // 초대 상태 메시지 저장
        saveStatusMsg(save, msg);

        // 초대자 추가
        request.inviteList().add(new MemberInfo(null, memberId));
        // 참여 저장
        saveChatParticipation(request.inviteList(), save.getId());

        return new ChatRoomCreateResponse(save.getId(), msg);
    }

    /*
     * 상태 메시지 생성
     */
    private String createInviteMsg(Long memberId, List<MemberInfo> inviteList) {
        String inviter = getNickname(memberId);

        StringBuilder inviteMsg = new StringBuilder(inviter + "님이 ");

        for (MemberInfo guest : inviteList) {
            inviteMsg.append(guest.nickname()).append("님, ");
        }

        return inviteMsg.substring(0, inviteMsg.length() - 2) + "을 초대하였습니다.";
    }

    /*
     * 상태 메시지 저장
     */
    private void saveStatusMsg(ChatRoom chatRoom, String msg) {
        chatRepository.save(Chat.builder()
                .chatRoom(chatRoom)
                .content(msg)
                .from(null)
                .messageType(MessageType.STATUS)
                .createdAt(LocalDateTime.now())
                .build()
        );
    }

    /*
     * 채팅 참여 정보 삭제
     */
    @Transactional
    public void leaveChatRoom(Long memberId, Long roomId) {
        chatParticipationRepository.deleteByMemberAndRoom(memberId, roomId);
        String nickname = getNickname(memberId);

        // 채팅방 인원 수 감소, 0명일 경우 채팅방 삭제
        chatRoomRepository.findById(roomId)
                .ifPresentOrElse(
                        chatRoom -> {
                            if (chatRoom.getMemberCnt() == 1) {
                                chatRoomRepository.deleteById(roomId);
                            } else {
                                saveStatusMsg(chatRoom, nickname + "님이 나갔습니다.");
                                chatRoom.decreaseMemberCnt();
                            }
                        },
                        () -> {
                            throw new BusinessException(CHATROOM_NOT_EXIST);
                        }
                );

        eventPublisher.publishEvent(new UserRoomExitEvent(roomId));
    }

    /*
     * 기존 채팅방에 참여자 추가
     */
    @Transactional
    public ChatRoomMsgResponse inviteMemberInChatRoom(Long memberId, List<MemberInfo> request, Long roomId) {
        // 사용자 검증
        verifyMemberInChatRoom(memberId, roomId);

        ChatRoom chatRoom = getChatRoom(roomId);

        // 채팅방 수용 인원 검증
        checkCapacity(chatRoom.getMemberCnt() + request.size());

        // 추가 참여자 저장
        saveChatParticipation(request, roomId);

        // 초대 상태 메시지 생성
        String msg = createInviteMsg(memberId, request);

        // 상태 메시지 저장
        saveStatusMsg(chatRoom, msg);

        return new ChatRoomMsgResponse(msg);
    }

    /*
     * 채팅방 참여자 조회
     */
    @Transactional(readOnly = true)
    public List<ChatParticipantsResponse> getChatParticipants(Long memberId, Long roomId) {
        // 검증
        verifyRoomId(roomId);
        verifyMemberInChatRoom(memberId, roomId);

        List<Member> participants = chatParticipationRepository.findMemberByRoom(roomId);

        return participants.stream()
                .map(member -> new ChatParticipantsResponse(
                        member.getId(),
                        member.getNickname(),
                        member.getUrl()))
                .collect(Collectors.toList());
    }

    /*
     * 채팅방에 속해있는 회원인지 여부 검증
     */
    @Transactional(readOnly = true)
    public void verifyMemberInChatRoom(Long memberId, Long roomId) {
        if (!chatParticipationRepository.existsById(new ChatParticipationID(memberId, roomId)))
            throw new BusinessException(NOT_CHAT_PARTICIPANTS);
    }

    /*
     * 채팅방 존재 여부 검증
     */
    @Transactional(readOnly = true)
    public void verifyRoomId(Long roomId) {
        getChatRoom(roomId);
    }

    /*
     * 채팅 참여 정보 저장
     */
    private void saveChatParticipation(List<MemberInfo> inviteList, Long roomId) {
        for (MemberInfo guest : inviteList) {
            chatParticipationRepository.save(ChatParticipation.builder()
                    .member(guest.memberId())
                    .room(roomId)
                    .build());
        }
    }

    private ChatRoom getChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(CHATROOM_NOT_EXIST));
    }

    private String getNickname(Long memberId) {
        return memberRepository.findById(memberId)
                .map(Member::getNickname)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
    }

    public List<PastChatResponse> getPastChat(Long roomId) {
        List<ChatParticipation> participants = chatParticipationRepository.findAllByRoom(roomId);

        return participants.stream()
                .map(cp -> new PastChatResponse(cp.getMember(), cp.getLastChatId()))
                .collect(Collectors.toList());
    }
}
