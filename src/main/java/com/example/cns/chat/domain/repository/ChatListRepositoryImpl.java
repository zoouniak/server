package com.example.cns.chat.domain.repository;

import com.example.cns.chat.domain.Chat;
import com.example.cns.chat.dto.response.ChatResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.cns.chat.domain.QChat.chat;

@Repository
public class ChatListRepositoryImpl extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public ChatListRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Chat.class);
        this.queryFactory = queryFactory;
    }

    public List<ChatResponse> paginationChat(Long roomId, Long chatId, int pageSize) {
        return queryFactory.select(Projections.constructor(ChatResponse.class,
                        chat.id.as("chatId"),
                        chat.content,
                        chat.from.nickname.as("from"),
                        chat.from.id.as("memberId"),
                        chat.createdAt,
                        chat.messageType))
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
}
