package com.example.cns.chat.domain.repository;

import com.example.cns.chat.dto.response.ChatResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.cns.chat.domain.QChat.chat;

@Repository
public class ChatListRepositoryImpl {
    private final JPAQueryFactory queryFactory;

    public ChatListRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<ChatResponse> paginationChat(Long roomId, Long chatId, int pageSize) {
        return queryFactory.select(Projections.constructor(ChatResponse.class,
                        chat.id.as("chatId"),
                        chat.content,
                        chat.from.nickname.as("from"),
                        chat.from.id.as("memberId"),
                        chat.createdAt,
                        chat.messageType
                ))
                .from(chat)
                .where(
                        ltChatId(chatId),
                        eqRoomId(roomId)
                )
                .orderBy(chat.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression eqRoomId(Long roomId) {
        return chat.chatRoom.id.eq(roomId);
    }

    private BooleanExpression ltChatId(Long chatId) {
        if (chatId == null) {
            return null;
        }
        return chat.id.lt(chatId);
    }

    public List<Tuple> getLastChatByChatRoom() {
        return queryFactory
                .select(chat.chatRoom.id, chat.id.max())
                .from(chat)
                .groupBy(chat.chatRoom.id)
                .fetch();
    }
}
