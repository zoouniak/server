package com.example.cns.feed.comment.domain.repository;

import com.example.cns.feed.comment.dto.response.CommentResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.cns.feed.comment.domain.QComment.comment;
import static com.example.cns.feed.comment.domain.QCommentLike.commentLike;

@Repository
public class CommentListRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public CommentListRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<CommentResponse> getCommentLists(Long memberId,Long postId, Long parentId){

        BooleanBuilder condition = createCondition(postId,parentId);

        return jpaQueryFactory.select(Projections.constructor(CommentResponse.class,
                comment.id.as("commentId"),
                comment.writer.id.as("memberId"),
                comment.writer.nickname,
                comment.writer.url.as("profile"),
                comment.content,
                comment.likeCnt,
                comment.createdAt,
                comment.childComments.size().as("commentReplyCnt"),
                new CaseBuilder()
                        .when(commentLike.count().gt(0))
                        .then(true)
                        .otherwise(false)
                        .as("liked")
                ))
                .from(comment)
                .leftJoin(commentLike)
                .on(commentLike.comment.eq(comment).and(commentLike.member.id.eq(memberId)))
                .where(condition)
                .groupBy(comment.id)
                .orderBy(comment.createdAt.asc())
                .fetch();
    }

    private BooleanBuilder createCondition(Long postId, Long parentId) {
        BooleanBuilder condition = new BooleanBuilder();
        if(postId != null){
            condition.and(comment.post.id.eq(postId));
        }
        if(parentId != null){
            condition.and(comment.parentComment.id.eq(parentId));
        }else {
            condition.and(comment.parentComment.id.isNull());
        }
        return condition;
    }
}
