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
                        eqMemberId(memberId)
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
    private BooleanExpression eqMemberId(Long memberId) {
        if (memberId == null)
            return null;
        return post.member.id.eq(memberId);
    }

    private BooleanExpression betweenDate(LocalDate start, LocalDate end) {
        if (start == null || end == null)
            return null;
        return post.createdAt.between(start.atStartOfDay(), end.atStartOfDay());
    }

    // filter에 따라 cursorValue lt,gt 결정
    private BooleanExpression decideCursor(String type, Long cursorValue) {
        if (cursorValue == null)
            return null;
        if (type.equals("oldest") || type.equals("period")) return post.id.gt(cursorValue);
        return post.id.lt(cursorValue);
    }

    //=============================================================================================================
    public List<PostResponse> findPostsByCondition(Long currentMemberId, Long memberId, Long cursorValue, Long pageSize, String type, LocalDate start, LocalDate end, Long likeCnt) {

        cursorValue = isCursorExists(cursorValue, type);
        BooleanBuilder condition = new BooleanBuilder();
        OrderSpecifier<?>[] orders = new OrderSpecifier[0];

        switch (type) {
            case "posts" -> { //전체 게시글 조회시, 본인이 보는 경우
                memberId = currentMemberId;
                condition.and(post.id.lt(cursorValue));
                orders = new OrderSpecifier[]{post.id.desc()};
            }
            case "newest" -> {
                condition.and(post.member.id.eq(memberId));
                condition.and(post.id.lt(cursorValue));
                orders = new OrderSpecifier[]{post.id.desc()};
            }
            case "oldest" -> {
                condition.and(post.member.id.eq(memberId));
                condition.and(post.id.gt(cursorValue));
                orders = new OrderSpecifier[]{post.createdAt.asc()};
            }
            case "period" -> {
                LocalDateTime startDate = start.atStartOfDay();
                LocalDateTime endDate = end.atTime(LocalTime.MAX);

                condition.and(post.member.id.eq(memberId));
                condition.and(post.id.gt(cursorValue));
                condition.and(post.createdAt.between(startDate, endDate));
                orders = new OrderSpecifier[]{post.createdAt.asc()};
            }
            case "like" -> {
                if (likeCnt == -1 || likeCnt == null) {
                    likeCnt = 1L + Long.valueOf(jpaQueryFactory.select(post.likeCnt.max())
                            .from(post)
                            .where(post.member.id.eq(memberId))
                            .fetchOne());
                }
                condition.and(post.member.id.eq(memberId));
                condition.and(post.likeCnt.lt(likeCnt)
                        .or(post.likeCnt.eq(Math.toIntExact(likeCnt))).and(post.id.lt(cursorValue)));
                orders = new OrderSpecifier[]{post.likeCnt.desc(), post.createdAt.desc()};
            }
            case "myLike" -> {
                condition.and(postLike.member.id.eq(memberId));
                condition.and(postLike.post.id.lt(cursorValue));
                orders = new OrderSpecifier[]{postLike.post.id.desc()};
            }
        }

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
                        currentMemberId.equals(memberId) ?
                                new CaseBuilder()
                                        .when(postLike.count().gt(0))
                                        .then(true)
                                        .otherwise(false)
                                        .as("liked") :
                                Expressions.constant(false)
                ))
                .from(post)
                .leftJoin(postLike)
                .on(postLike.post.eq(post).and(postLike.member.id.eq(memberId)))
                .where(condition)
                .groupBy(post.id)
                .orderBy(orders)
                .limit(pageSize)
                .fetch();
    }

    private Long isCursorExists(Long cursorValue, String searchType) {
        if (cursorValue == null || cursorValue == 0L) {
            NumberExpression<?> maxMinExpr;
            switch (searchType) {
                case "oldest":
                case "period":
                    maxMinExpr = post.id.min();
                    cursorValue = 1 - (Long) jpaQueryFactory.select(maxMinExpr)
                            .from(post)
                            .fetchOne();
                    break;
                default:
                    maxMinExpr = post.id.max();
                    cursorValue = 1 + (Long) jpaQueryFactory.select(maxMinExpr)
                            .from(post)
                            .fetchOne();
            }
            if (cursorValue == null) {
                cursorValue = 0L;
            }
        }
        return cursorValue;
    }
}
