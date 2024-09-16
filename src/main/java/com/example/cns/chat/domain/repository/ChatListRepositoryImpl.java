package com.example.cns.chat.domain.repository;

import com.example.cns.chat.domain.Chat;
import com.example.cns.chat.dto.response.ChatResponse;
import com.example.cns.chat.dto.response.LastChatInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.cns.chat.domain.QChat.chat;
import static com.example.cns.member.domain.QMember.member;

@Repository
public class ChatListRepositoryImpl {
    private final JPAQueryFactory queryFactory;

    public ChatListRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<ChatResponse> paginationChat(final Long roomId, final Long chatId, final int pageSize) {
        return queryFactory.select(Projections.constructor(ChatResponse.class,
                        chat.id.as("chatId"),
                        chat.content,
                        chat.from.nickname.as("from"),
                        chat.from.id.as("memberId"),
                        chat.from.url.as("profile"),
                        chat.createdAt,
                        chat.messageType
                ))
                .from(chat)
                .leftJoin(chat.from, member)
                .where(
                        ltChatId(chatId),
                        eqRoomId(roomId)
                )
                .orderBy(chat.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression eqRoomId(final Long roomId) {
        return chat.chatRoom.id.eq(roomId);
    }

    private BooleanExpression ltChatId(final Long chatId) {
        if (chatId == null) {
            return null;
        }
        return chat.id.lt(chatId);
    }

    public List<LastChatInfo> getLastChatByChatRoom() {
        return queryFactory
                .select(Projections.constructor(LastChatInfo.class, chat.chatRoom.id, chat.id.max()))
                .from(chat)
                .groupBy(chat.chatRoom.id)
                .fetch();
    }

    public Chat searchChat(final String word, final Long roomId, final Long chatId) {
        return queryFactory.select(chat).from(chat)
                .where(eqRoomId(roomId)
                        , chat.content.contains(word)
                        , ltChatId(chatId))
                .orderBy(chat.id.desc())
                .limit(1L)
                .fetchOne();

    }
}
