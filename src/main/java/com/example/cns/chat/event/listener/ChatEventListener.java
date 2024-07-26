package com.example.cns.chat.event.listener;

import com.example.cns.chat.dto.response.UnRead;
import com.example.cns.chat.event.UserRoomExitEvent;
import com.example.cns.chat.event.UserSubscribeEvent;
import com.example.cns.chat.event.UserUnsubscribeEvent;
import com.example.cns.chat.service.ChatParticipationService;
import com.example.cns.chat.service.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatEventListener {
    private final ChatParticipationService participationService;
    private final MessagePublisher messagePublisher;

    @EventListener
    public void handleUserSubscribe(UserSubscribeEvent event) {
        // 사용자 lastchatId null로 설정 후 unread list 계산
        participationService.removeMemberLastChat(event.memberId(), event.roomId());
        makeAndSendUnReadList(event.roomId());
    }

    @EventListener
    public void handleUserUnsubscribe(UserUnsubscribeEvent event) {
        participationService.updateMemberLastChat(event.memberId(), event.roomId());
        makeAndSendUnReadList(event.roomId());
    }

    @EventListener
    public void handleUserExitRoom(UserRoomExitEvent event) {
        makeAndSendUnReadList(event.roomId());
    }

    private void makeAndSendUnReadList(Long roomId) {
        List<UnRead> unReadList = participationService.countUnRead(roomId);

        messagePublisher.publishUnRead(roomId, unReadList);
    }
}
