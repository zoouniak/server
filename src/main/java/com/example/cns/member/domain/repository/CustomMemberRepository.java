package com.example.cns.member.domain.repository;

import com.example.cns.chat.domain.QChatParticipation;
import com.example.cns.member.domain.QMember;
import com.example.cns.member.dto.response.MemberSearchResponseWithChatParticipation;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomMemberRepository {
    private final JPAQueryFactory queryFactory;

    public CustomMemberRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<MemberSearchResponseWithChatParticipation> searchMembersInSameCompanyWithChatParticipation(Long companyId, Long roomId, String nickname) {
        QMember member = QMember.member;
        QChatParticipation chatParticipation = QChatParticipation.chatParticipation;

        return queryFactory
                .select(Projections.constructor(MemberSearchResponseWithChatParticipation.class,
                        member.id,
                        member.nickname,
                        member.url.as("profile"),
                        chatParticipation.member.isNull().not()))
                .from(member)
                .leftJoin(chatParticipation)
                .on(member.id.eq(chatParticipation.member).and(chatParticipation.room.eq(roomId)))
                .where(member.company.id.eq(companyId), member.nickname.startsWith(nickname))
                .fetch();
    }
}
