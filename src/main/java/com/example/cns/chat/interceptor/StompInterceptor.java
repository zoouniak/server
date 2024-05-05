package com.example.cns.chat.interceptor;

import com.example.cns.common.security.exception.AuthException;
import com.example.cns.common.security.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.cns.common.exception.ExceptionCode.EMPTY_ACCESS_TOKEN;
import static com.example.cns.common.exception.ExceptionCode.EXPIRED_TOKEN;

@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {
    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // 연결 요청시 JWT 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Authorization 헤더 추출
            List<String> authorization = accessor.getNativeHeader("Authorization");
            String token = jwtProvider.resolveAccessToken(authorization == null ? "" : authorization.get(0));
            if (!StringUtils.hasText(token))
                throw new AuthException(EMPTY_ACCESS_TOKEN);

            if (jwtProvider.isTokenExpired(token))
                throw new AuthException(EXPIRED_TOKEN);
        }
        return ChannelInterceptor.super.preSend(message, channel);
    }
}
