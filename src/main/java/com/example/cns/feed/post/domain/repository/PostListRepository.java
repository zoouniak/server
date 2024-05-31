package com.example.cns.feed.post.domain.repository;

import com.example.cns.feed.post.dto.response.PostResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static com.example.cns.feed.post.domain.QPost.post;
import static com.example.cns.feed.post.domain.QPostLike.postLike;

@Repository
public class PostListRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public PostListRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<PostResponse> findPostsByCondition(Long memberId, Long cursorValue, Long pageSize, String type, LocalDate start, LocalDate end, Long likeCnt){

        cursorValue = isCursorExists(cursorValue,type);

        BooleanBuilder condition = new BooleanBuilder();
        OrderSpecifier<?>[] orders = new OrderSpecifier[0];

        switch(type){
            case "posts" -> {
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
                LocalDateTime endDate = end.atStartOfDay();

                condition.and(post.member.id.eq(memberId));
                condition.and(post.id.gt(cursorValue));
                condition.and(post.createdAt.between(startDate,endDate));
                orders = new OrderSpecifier[]{post.createdAt.asc()};
            }
            case "like" -> {
                if(likeCnt == -1 || likeCnt == null){
                    likeCnt = 1L + Long.valueOf(jpaQueryFactory.select(post.likeCnt.max())
                            .from(post)
                            .where(post.member.id.eq(memberId))
                            .fetchOne());
                }
                condition.and(post.member.id.eq(memberId));
                condition.and(post.likeCnt.lt(likeCnt)
                        .or(post.likeCnt.eq(Math.toIntExact(likeCnt))).and(post.id.lt(cursorValue)));
                orders = new OrderSpecifier[]{post.likeCnt.desc(),post.createdAt.desc()};
            }
            case "myLike" -> {
                condition.and(postLike.member.id.eq(memberId));
                condition.and(postLike.member.id.lt(cursorValue));
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
                new CaseBuilder()
                        .when(postLike.count().gt(0))
                        .then(true)
                        .otherwise(false)
                        .as("liked")
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
                    break;
                default:
                    maxMinExpr = post.id.max();
            }
            cursorValue = 1 + (Long) jpaQueryFactory.select(maxMinExpr)
                    .from(post)
                    .fetchOne();
            if (cursorValue == null) {
                cursorValue = 0L;
            }
        }
        return cursorValue;
    }
}
