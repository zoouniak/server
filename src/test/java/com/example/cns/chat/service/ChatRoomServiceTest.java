package com.example.cns.chat.service;

import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.domain.repository.ChatParticipationRepository;
import com.example.cns.chat.domain.repository.ChatRepository;
import com.example.cns.chat.domain.repository.ChatRoomListRepository;
import com.example.cns.chat.domain.repository.ChatRoomRepository;
import com.example.cns.chat.dto.request.ChatRoomCreateRequest;
import com.example.cns.chat.dto.request.MemberInfo;
import com.example.cns.chat.dto.response.ChatRoomCreateResponse;
import com.example.cns.chat.dto.response.ChatRoomMsgResponse;
import com.example.cns.chat.type.RoomType;
import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.member.type.RoleType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {
    @InjectMocks
    private ChatRoomService sut;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ChatParticipationRepository chatParticipationRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ChatRoomListRepository chatRoomListRepository;

    private static Member createMember() {
        return new Member(0L, "닉네임0", "testpassword", "email", "first", "second", LocalDate.now(), RoleType.EMPLOYEE, "");
    }

    /*
     * 채팅방 정원은 10명이다.
     * 채팅방 추가 초대는 채팅방에 참여하고 있어야 가능하다.
     * 채팅방 인원이 모두 나가면 채팅방은 삭제된다.
     */
    @Test
    void _10명_이하의_그룹채팅을_생성할_수_있다() {
        // given
        ChatRoomCreateRequest request = createChatRoomRequest(2);
        when(chatRoomRepository.save(any())).thenReturn(createChatRoom(2));
        when(memberRepository.findById(0L)).thenReturn(Optional.of(createMember()));

        // when
        ChatRoomCreateResponse response = sut.createChatRoom(0L, request);

        // then
        verify(chatRoomRepository).save(any());
        verify(chatParticipationRepository, times(3)).save(any());
        Assertions.assertEquals("닉네임0님이 닉네임1님, 닉네임2님을 초대하였습니다.", response.inviteMsg());
    }

    private ChatRoomCreateRequest createChatRoomRequest(int cnt) {
        List<MemberInfo> memberInfos = createMemberInfos(cnt);
        return new ChatRoomCreateRequest("방 이름", memberInfos);
    }

    private List<MemberInfo> createMemberInfos(int cnt) {
        List<MemberInfo> memberInfos = new ArrayList<>();
        for (int i = 1; i <= cnt; i++) {
            memberInfos.add(new MemberInfo("닉네임" + i, (long) i));
        }
        return memberInfos;
    }

    private ChatRoom createChatRoom(int cnt) {
        return new ChatRoom(RoomType.GROUP, "방 이름", cnt);
    }

    @Test
    void 채팅방_정원을_초과하여_초대할_수_없다() {
        // given
        ChatRoomCreateRequest request = createChatRoomRequest(11);

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> sut.createChatRoom(0L, request));

        // then
        Assertions.assertEquals(ExceptionCode.ROOM_CAPACITY_EXCEEDED, exception.getExceptionCode());
    }

    @Test
    void 채팅방에_직장동료를_추가할_수_있다() {
        // given
        when(chatParticipationRepository.existsById(any())).thenReturn(true);
        List<MemberInfo> request = createMemberInfos(2);
        when(chatRoomRepository.findById(0L)).thenReturn(Optional.of(createChatRoom(4)));
        when(memberRepository.findById(0L)).thenReturn(Optional.of(createMember()));
        // when
        ChatRoomMsgResponse response = sut.inviteMemberInChatRoom(0L, request, 0L);
        // then
        verify(chatParticipationRepository, times(2)).save(any());
        Assertions.assertEquals("닉네임0님이 닉네임1님, 닉네임2님을 초대하였습니다.", response.msg());
    }

    @Test
    void 채팅방_참여_검증에_실패하면_초대_할수_없다() {
        when(chatParticipationRepository.existsById(any())).thenReturn(false);
        List<MemberInfo> request = createMemberInfos(2);
        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> sut.inviteMemberInChatRoom(0L, request, 0L));
        // then
        Assertions.assertEquals(ExceptionCode.NOT_CHAT_PARTICIPANTS, exception.getExceptionCode());
    }

    @Test
    void 채팅방_정원을_초과하여_추가초대할_수_없다() {
        // given
        when(chatParticipationRepository.existsById(any())).thenReturn(true);
        List<MemberInfo> request = createMemberInfos(7);
        when(chatRoomRepository.findById(0L)).thenReturn(Optional.of(createChatRoom(6)));

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> sut.inviteMemberInChatRoom(0L, request, 0L));

        // then
        Assertions.assertEquals(ExceptionCode.ROOM_CAPACITY_EXCEEDED, exception.getExceptionCode());
    }

    @Test
    void 자신이_참여하고있는_채팅방에서_나갈_수_있다() {
        // given
        when(chatRoomRepository.findById(0L)).thenReturn(Optional.of(createChatRoom(3)));
        when(memberRepository.findById(0L)).thenReturn(Optional.of(createMember()));

        // when
        sut.leaveChatRoom(0L, 0L);

        // then
        verify(chatParticipationRepository).deleteByMemberAndRoom(0L, 0L);

        ChatRoom chatRoom = chatRoomRepository.findById(0L).get();
        Assertions.assertEquals(2, chatRoom.getMemberCnt());
    }

    @Test
    void 채팅방_인원_모두가_나갈_경우_방은_삭제된다() {
        // given
        when(chatRoomRepository.findById(0L)).thenReturn(Optional.of(createChatRoom(1)));
        when(memberRepository.findById(0L)).thenReturn(Optional.of(createMember()));

        // when
        sut.leaveChatRoom(0L, 0L);

        // then
        verify(chatParticipationRepository).deleteByMemberAndRoom(0L, 0L);
        verify(chatRoomRepository).deleteById(0L);
    }


}