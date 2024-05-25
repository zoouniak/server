package com.example.cns.projectPost.projectPost.domain.repository;

import com.example.cns.projectPost.projectPost.dto.response.ProjectPostResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.cns.project.projectPost.domain.QProjectPost.projectPost;
import static com.example.cns.project.projectPost.domain.QProjectPostOpinion.projectPostOpinion;

@Repository
public class ProjectPostListRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;

    public ProjectPostListRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<ProjectPostResponse> paginationProjectPost(Long memberId, Long projectId, int pageSize, Long cursorValue) {

        if (cursorValue == null || cursorValue == 0L) { //cursorValue가 없을 경우
            cursorValue = 1L + (jpaQueryFactory.select(projectPost.id.max())
                    .from(projectPost)
                    .fetchOne());
            if (cursorValue == null) { //게시글이 없는 경우
                cursorValue = 0L;
            }
        }

        BooleanExpression cursorCondition = projectPost.id.lt(cursorValue);

        return jpaQueryFactory.select(Projections.constructor(ProjectPostResponse.class,
                        projectPost.id,
                        projectPost.project.id.as("projectId"),
                        projectPost.member.id.as("memberId"),
                        projectPost.member.nickname,
                        projectPost.member.url,
                        projectPost.content,
                        projectPost.createdAt,
                        projectPost.prosCnt,
                        projectPost.consCnt,
                        projectPost.checkCnt,
                        new CaseBuilder()
                                .when(projectPostOpinion.id.isNotNull())
                                .then(projectPostOpinion.opinionType.stringValue())
                                .otherwise((String) null)
                        ))
                .from(projectPost)
                .leftJoin(projectPost.opinions, projectPostOpinion)
                .on(projectPostOpinion.member.id.eq(memberId))
                .where(cursorCondition.and(projectPost.project.id.eq(projectId)))
                .orderBy(projectPost.id.desc())
                .limit(pageSize)
                .fetch();
    }

//        return results.stream().map(tuple -> {
//            Long postId = tuple.get(projectPost.id);
//            Long projectId = tuple.get(projectPost.project.id);
//            Long postMemberId = tuple.get(projectPost.member.id);
//            String postMemberNickname = tuple.get(projectPost.member.nickname);
//            String postMemberUrl = tuple.get(projectPost.member.url);
//            String content = tuple.get(projectPost.content);
//            LocalDateTime createdAt = tuple.get(projectPost.createdAt);
//            int prosCnt = tuple.get(projectPost.prosCnt);
//            int consCnt = tuple.get(projectPost.consCnt);
//            int checkCnt = tuple.get(projectPost.checkCnt);
//            String opinion = tuple.get(new CaseBuilder()
//                    .when(projectPostOpinion.id.isNotNull())
//                    .then(projectPostOpinion.opinionType.stringValue())
//                    .otherwise((String) null));
//
//            PostMember postMember = PostMember.builder()
//                    .id(postMemberId)
//                    .nickname(postMemberNickname)
//                    .profile(postMemberUrl)
//                    .build();
//
//            return ProjectPostResponse.builder()
//                    .id(postId)
//                    .projectId(projectId)
//                    .postMember(postMember)
//                    .content(content)
//                    .createdAt(createdAt)
//                    .prosCnt(prosCnt)
//                    .consCnt(consCnt)
//                    .checkCnt(checkCnt)
//                    .opinion(opinion)
//                    .build();
//        }).collect(Collectors.toList());

    }

