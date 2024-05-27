package com.example.cns.feed.post.domain.repository;

import com.example.cns.feed.post.dto.response.PostResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

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

    public List<PostResponse> findPostsAndUserLikeWithCursor(Long memberId, Long cursorValue, Long pageSize){

        cursorValue = isCursorExists(cursorValue, NumberExpression::max);

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
                .where(post.id.lt(cursorValue))
                .groupBy(post.id)
                .orderBy(post.id.desc())
                .limit(pageSize)
                .fetch();
    }

    public List<PostResponse> findNewestPosts(Long memberId, Long cursorValue, Long pageSize){

        cursorValue = isCursorExists(cursorValue, NumberExpression::max);

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
                .where(post.member.id.eq(memberId).and(post.id.lt(cursorValue)))
                .groupBy(post.id)
                .orderBy(post.id.desc())
                .limit(pageSize)
                .fetch();
    }

    public List<PostResponse> findOldestPosts(Long memberId, Long cursorValue, Long pageSize){

        cursorValue = isCursorExists(cursorValue,NumberExpression::min);

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
                .where(post.member.id.eq(memberId).and(post.id.gt(cursorValue)))
                .groupBy(post.id)
                .orderBy(post.createdAt.asc())
                .limit(pageSize)
                .fetch();
    }

    public List<PostResponse> findPostsByPeriod(Long memberId, Long cursorValue, Long pageSize, LocalDateTime start, LocalDateTime end){

        cursorValue = isCursorExists(cursorValue,NumberExpression::min);

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
                .where(post.member.id.eq(memberId)
                        .and(post.id.gt(cursorValue))
                        .and(post.createdAt.between(start,end)))
                .groupBy(post.id)
                .orderBy(post.createdAt.asc())
                .limit(pageSize)
                .fetch();
    }

    public List<PostResponse> findPostsByLikeCnt(Long memberId, int likeCnt, Long cursorValue, Long pageSize){

        cursorValue = isCursorExists(cursorValue,NumberExpression::max);

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
                .where(post.member.id.eq(memberId)
                        .and(post.likeCnt.lt(likeCnt)
                                .or(post.likeCnt.eq(likeCnt).and(post.id.lt(cursorValue)))))
                .groupBy(post.id)
                .orderBy(post.likeCnt.desc(),post.createdAt.desc())
                .limit(pageSize)
                .fetch();
    }

    public List<PostResponse> findPostLikesByMemberId(Long memberId, Long cursorValue, Long pageSize){

        cursorValue = isCursorExists(cursorValue,NumberExpression::max);

        return jpaQueryFactory.select(Projections.constructor(PostResponse.class,
                postLike.post.id,
                postLike.member.id.as("memberId"),
                postLike.member.nickname,
                postLike.member.url.as("profile"),
                postLike.post.content,
                postLike.post.createdAt,
                postLike.post.likeCnt,
                postLike.post.fileCnt,
                postLike.post.comments.size().as("commentCnt"),
                postLike.post.isCommentEnabled,
                new CaseBuilder()
                        .when(postLike.count().gt(0))
                        .then(true)
                        .otherwise(false)
                        .as("liked")
                )).from(postLike)
                .where(postLike.member.id.eq(memberId)
                        .and(postLike.post.id.lt(cursorValue)))
                .groupBy(postLike.post.id)
                .orderBy(postLike.post.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private Long isCursorExists(Long cursorValue, Function<NumberExpression<Long>, NumberExpression<Long>> aggregationFunction) {
        if (cursorValue == null || cursorValue == 0L) { //cursorValue가 없을 경우
            cursorValue = 1L + (jpaQueryFactory.select(aggregationFunction.apply(post.id))
                    .from(post)
                    .fetchOne());
            if (cursorValue == null) { //게시글이 없는 경우
                cursorValue = 0L;
            }
        }
        return cursorValue;
    }
}
