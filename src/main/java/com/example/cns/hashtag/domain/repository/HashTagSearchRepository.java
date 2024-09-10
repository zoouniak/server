package com.example.cns.hashtag.domain.repository;

import com.example.cns.feed.post.dto.response.PostResponse;
import com.example.cns.member.domain.Member;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.cns.chat.domain.QChat.chat;
import static com.example.cns.feed.post.domain.QPost.post;
import static com.example.cns.feed.post.domain.QPostLike.postLike;
import static com.example.cns.hashtag.domain.QHashTag.hashTag;
import static com.example.cns.hashtag.domain.QHashTagPost.hashTagPost;

@Repository
public class HashTagSearchRepository {
    private final JPAQueryFactory queryFactory;

    public HashTagSearchRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<PostResponse> getPostsByHashTag(Long hashtagId, Member member, Long postId, int pageSize) {
        return queryFactory
                .select(Projections.constructor(PostResponse.class,
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
                                        postLike.member.eq(member)
                                ).exists(), "liked")
                ))
                .from(hashTagPost)
                .join(post)
                .on(hashTagPost.id.post.eq(post.id))
                .where(hashTagPost.id.hashtag.eq(hashtagId),
                        gtPostId(postId))
                .orderBy(post.id.asc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression gtPostId(Long postId) {
        if (postId == null) {
            return null;
        }
        return chat.id.lt(postId);
    }

    public List<String> getHashTagPostWithTagName(Long postId) {
        return queryFactory.select(hashTag.name)
                .from(hashTagPost)
                .join(hashTag)
                .on(hashTagPost.id.post.eq(hashTag.id))
                .where(hashTagPost.id.post.eq(postId))
                .fetch();
    }
}
