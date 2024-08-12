package com.example.cns.notification.domain.repository;

import com.example.cns.notification.dto.response.NotificationResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.cns.notification.domain.QNotification.notification;

@Repository
public class CustomNotificationRepository {
    private final JPAQueryFactory queryFactory;

    public CustomNotificationRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<NotificationResponse> getNotificationsByCursor(final Long memberId, final Long cursor) {
        return queryFactory.select(Projections.constructor(NotificationResponse.class,
                        notification.id.as("id"),
                        notification.message.as("message"),
                        notification.subjectId.as("subjectId"),
                        notification.notificationType.as("type")
                ))
                .from(notification)
                .where(ltCursor(cursor),
                        notification.to.id.eq(memberId)
                )
                .orderBy(notification.id.desc())
                .fetch();

    }

    private BooleanExpression ltCursor(final Long cursor) {
        if (cursor == null)
            return null;
        return notification.id.lt(cursor);
    }
}
