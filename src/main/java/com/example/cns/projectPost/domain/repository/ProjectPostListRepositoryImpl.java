package com.example.cns.projectPost.domain.repository;

import com.example.cns.projectPost.domain.QProjectPost;
import com.example.cns.projectPost.domain.QProjectPostOpinion;
import com.example.cns.projectPost.dto.response.ProjectPostResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProjectPostListRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;

    public ProjectPostListRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<ProjectPostResponse> paginationProjectPost(Long memberId, Long projectId, int pageSize, Long cursorValue) {

        if (cursorValue == null || cursorValue == 0L) { //cursorValue가 없을 경우
            cursorValue = 1L + (jpaQueryFactory.select(QProjectPost.projectPost.id.max())
                    .from(QProjectPost.projectPost)
                    .fetchOne());
            if (cursorValue == null) { //게시글이 없는 경우
                cursorValue = 0L;
            }
        }

        BooleanExpression cursorCondition = QProjectPost.projectPost.id.lt(cursorValue);

        return jpaQueryFactory.select(Projections.constructor(ProjectPostResponse.class,
                        QProjectPost.projectPost.id,
                        QProjectPost.projectPost.project.id.as("projectId"),
                        QProjectPost.projectPost.member.id.as("memberId"),
                        QProjectPost.projectPost.member.nickname,
                        QProjectPost.projectPost.member.url,
                        QProjectPost.projectPost.content,
                        QProjectPost.projectPost.createdAt,
                        QProjectPost.projectPost.prosCnt,
                        QProjectPost.projectPost.consCnt,
                        QProjectPost.projectPost.checkCnt,
                        new CaseBuilder()
                                .when(QProjectPostOpinion.projectPostOpinion.id.isNotNull())
                                .then(QProjectPostOpinion.projectPostOpinion.opinionType.stringValue())
                                .otherwise((String) null)
                ))
                .from(QProjectPost.projectPost)
                .leftJoin(QProjectPost.projectPost.opinions, QProjectPostOpinion.projectPostOpinion)
                .on(QProjectPostOpinion.projectPostOpinion.member.id.eq(memberId))
                .where(cursorCondition.and(QProjectPost.projectPost.project.id.eq(projectId)))
                .orderBy(QProjectPost.projectPost.id.desc())
                .limit(pageSize)
                .fetch();
    }
}

