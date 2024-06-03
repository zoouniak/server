package com.example.cns.feed.post.domain.repository;

import com.example.cns.feed.post.dto.response.PostResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.example.cns.feed.post.domain.QPost.post;
import static com.example.cns.feed.post.domain.QPostLike.postLike;

@Repository
public class PostListRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public PostListRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    //=============================================================================================================
    // filter에 따른 동적쿼리
    public List<PostResponse> findPosts(Long currentMemberId, Long memberId, Long cursorValue, Long pageSize, String type, LocalDate start, LocalDate end) {
        // todo filter enum으로 변환
        return jpaQueryFactory.select(Projections.constructor(PostResponse.class,
                        post.id,
                        post.member.id.as("memberId"),
                        post.member.nickname,
                        post.member.url.as("profile"),
                        post.content,
                        post.createdAt,
                        post.likeCnt,
                        post.fileCnt,
                        post.comments.size().as("commentCnt"),
                        post.isCommentEnabled,
                        ExpressionUtils.as(JPAExpressions.select(postLike)
                                .from(postLike)
                                .where(postLike.post.eq(post),
                                        postLike.member.id.eq(currentMemberId)
                                ).exists(), "liked")
                ))
                .from(post)
                .leftJoin(postLike)
                .on(postLike.post.eq(post).and(postLike.member.id.eq(memberId)))
                .where(decideCursor(type, cursorValue),
                        betweenDate(start, end),
                        eqMemberId(memberId,type)
                )
                .groupBy(post.id)
                .orderBy(decideOrder(type))
                .limit(pageSize)
                .fetch();
    }

    // filter에 따라 정렬 방식 결정
    private OrderSpecifier[] decideOrder(String type) {
        return switch (type) {
            case "oldest", "period" -> new OrderSpecifier[]{post.createdAt.asc()};
            case "like" -> new OrderSpecifier[]{post.likeCnt.desc(), post.createdAt.desc()};
            default -> new OrderSpecifier[]{post.id.desc()};
        };
    }

    // 특정 사용자 게시물 조회를 위한 동적 쿼리 생성
    private BooleanExpression eqMemberId(Long memberId,String type) {
        if (memberId == null)
            return null;
        if (type.equals("myLike")) return postLike.member.id.eq(memberId);
        return post.member.id.eq(memberId);
    }

    private BooleanExpression betweenDate(LocalDate start, LocalDate end) {
        if (start == null || end == null)
            return null;
        return post.createdAt.between(start.atStartOfDay(), end.atTime(LocalTime.MAX));
    }

    // filter에 따라 cursorValue lt,gt 결정
    private BooleanExpression decideCursor(String type, Long cursorValue) {
        if (cursorValue == null)
            return null;
        if (type.equals("oldest") || type.equals("period")) return post.id.gt(cursorValue);
        if (type.equals("myLike")) return postLike.post.id.lt(cursorValue);
        return post.id.lt(cursorValue);
    }
}
