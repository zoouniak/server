package com.example.cns.chat.domain.repository;

import com.example.cns.chat.dto.response.ChatRoomResponse;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.cns.chat.domain.QChat.chat;
import static com.example.cns.chat.domain.QChatParticipation.chatParticipation;
import static com.example.cns.chat.domain.QChatRoom.chatRoom;

@Repository
public class ChatRoomListRepository {
    private final JPAQueryFactory queryFactory;

    public ChatRoomListRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<ChatRoomResponse> getMyChatRoomList(Long memberId, int pageSize, int offset) {
        return queryFactory.select(Projections.constructor(ChatRoomResponse.class,
                        chatRoom.id.as("roomId"),
                        chatRoom.name.as("roomName"),
                        ExpressionUtils.as(JPAExpressions.select(chat.content)
                                .from(chat)
                                .where(chat.id.eq(chatRoom.lastChatId)
                                ), "lastChat"),
                        ExpressionUtils.as(JPAExpressions.select(chat.createdAt)
                                .from(chat)
                                .where(chat.id.eq(chatRoom.lastChatId)
                                ), "lastChatSendAt"),
                        chatRoom.roomType,
                        chatParticipation.isRead
                ))
                .from(chatRoom)
                .join(chatParticipation).on(chatRoom.id.eq(chatParticipation.room))
                .where(chatParticipation.member.eq(memberId))
                .orderBy(chatRoom.lastChatId.desc())
                .limit(pageSize)
                .offset((offset - 1) * pageSize)
                .fetch();
    }
}
