package com.example.cns.chat.service.redisMessageBroker;

import com.example.cns.chat.dto.request.TextMessageFormat;
import com.example.cns.chat.service.MessageSubscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisMessageSubscriber implements MessageListener, MessageSubscriber {
    private final RedisMessageListenerContainer container;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessageSendingOperations messageSendingOp;
    private final ObjectMapper mapper;

    /*
    리스너에 수신된 메시지를 SimpMessageSendingOperations를 통해 websocket 구독자들에게 전달
    */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 발행된 메시지
        String publishedMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());
        // 역직렬화 후 전송
        try {
            TextMessageFormat msg = mapper.readValue(publishedMessage, TextMessageFormat.class);
            messageSendingOp.convertAndSend("sub/chat/" + msg.roomId(), msg);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void subscribe(String roomId) {
        container.addMessageListener(this, ChannelTopic.of(roomId));
    }

    @Override
    public void unsubscribe() {

    }
}
