package com.example.cns.chat.interceptor;

import com.example.cns.chat.service.ChatParticipationService;
import com.example.cns.common.security.exception.AuthException;
import com.example.cns.common.security.jwt.provider.JwtProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.cns.common.exception.ExceptionCode.EMPTY_ACCESS_TOKEN;
import static com.example.cns.common.exception.ExceptionCode.EXPIRED_TOKEN;

@Component
public class StompInterceptor implements ChannelInterceptor {
    private final JwtProvider jwtProvider;
    private final ChatParticipationService participationService;
    private Map<String, Long> memberSession;
    private Map<String, Long> sessionInRooms;

    public StompInterceptor(JwtProvider jwtProvider, ChatParticipationService participationService) {
        this.jwtProvider = jwtProvider;
        this.participationService = participationService;
        this.memberSession = new HashMap<>();
        this.sessionInRooms = new HashMap<>();
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Authorization 헤더 추출
            List<String> authorization = accessor.getNativeHeader("Authorization");
            // 연결 요청시 JWT 검증
            String token = jwtProvider.resolveAccessToken(authorization == null ? "" : authorization.get(0));
            if (!StringUtils.hasText(token))
                throw new AuthException(EMPTY_ACCESS_TOKEN);

            if (jwtProvider.isTokenExpired(token))
                throw new AuthException(EXPIRED_TOKEN);

            Long memberId = Long.valueOf(jwtProvider.getUserNameFromToken(token));
            memberSession.put(accessor.getSessionId(), memberId);

        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            // 구독 시 구독 내용 저장
            String destination = accessor.getDestination().split("/")[3];
            Long roomId = Long.valueOf(destination);
            sessionInRooms.put(accessor.getSessionId(), roomId);
            // last chat 초기화
            Long memberId = memberSession.get(accessor.getSessionId());
            participationService.removeMemberLastChat(memberId, roomId);

        } else if (StompCommand.UNSUBSCRIBE.equals(accessor.getCommand()) || StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            // 구독해제 or 연결해제 시 마지막 채팅 업데이트
            Long memberId = memberSession.get(accessor.getSessionId());
            Long roomId = sessionInRooms.get(accessor.getSessionId());
            participationService.updateMemberLastChat(memberId, roomId);

            // 소켓 제거
            sessionInRooms.remove(accessor.getSessionId());
            memberSession.remove(accessor.getSessionId());
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }
}
