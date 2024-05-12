package com.example.cns.feed.comment.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.feed.comment.domain.Comment;
import com.example.cns.feed.comment.domain.CommentLike;
import com.example.cns.feed.comment.domain.repository.CommentLikeRepository;
import com.example.cns.feed.comment.domain.repository.CommentRepository;
import com.example.cns.feed.comment.dto.request.CommentDeleteRequest;
import com.example.cns.feed.comment.dto.request.CommentLikeRequest;
import com.example.cns.feed.comment.dto.request.CommentPostRequest;
import com.example.cns.feed.comment.dto.request.CommentReplyPostRequest;
import com.example.cns.feed.comment.dto.response.CommentResponse;
import com.example.cns.feed.post.domain.Post;
import com.example.cns.feed.post.domain.repository.PostRepository;
import com.example.cns.feed.post.dto.response.PostMember;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.mention.service.MentionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final MentionService mentionService;

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;

    /*
    댓글 달기
    1. 사용자한테 댓글 내용과 게시글 번호를 받아옴
    2. 멘션 확인후 댓글 등록
    3. 멘션 등록
     */
    @Transactional
    public void createComment(Long id, CommentPostRequest commentPostRequest) {
        //댓글 저장
        Post post = postRepository.findById(commentPostRequest.postId()).orElseThrow(
                () -> new BusinessException(ExceptionCode.POST_NOT_EXIST));

        //댓글 허용여부 확인
        if (post.isCommentEnabled()) {

            Member member = memberRepository.findById(id).orElseThrow(
                    () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));

            Comment comment = commentPostRequest.toEntity(member, post, null);

            Long responseId = commentRepository.save(comment).getId();
            //댓글 저장

            //댓글 멘션 저장
            if (commentPostRequest.mention().size() >= 1)
                mentionService.saveCommentMention(responseId, commentPostRequest.mention());
        }
    }


    /*
    대댓글 생성
    1. 연관관계 맺을 용 데이터 조회
    2. 대댓글 생성
    3. 멘션이 있을 경우 멘션 데이터 추가
     */
    @Transactional
    public void createCommentReply(Long id, CommentReplyPostRequest commentReplyPostRequest) {
        //대댓글 저장
        Post post = postRepository.findById(commentReplyPostRequest.postId()).orElseThrow(
                () -> new BusinessException(ExceptionCode.POST_NOT_EXIST));

        if (post.isCommentEnabled()) {
            Member member = memberRepository.findById(id).orElseThrow(
                    () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
            Comment comment = commentRepository.findById(commentReplyPostRequest.commentId()).orElseThrow(
                    () -> new BusinessException(ExceptionCode.COMMENT_NOT_EXIST));

            Comment reply = commentReplyPostRequest.toEntity(member, post, comment);

            Long responseId = commentRepository.save(reply).getId();
            //대댓글 저장

            if (commentReplyPostRequest.mention().size() >= 1)
                mentionService.saveCommentMention(responseId, commentReplyPostRequest.mention());
        }
    }

    /*
    댓글 및 대댓글 삭제
    1. 사용자가 댓글 작성자인지, 해당 게시글에 맞는건지 확인
    2. 언급 데이터 삭제
    3. 댓글 및 대댓글 삭제
    4. 게시글에서 댓글 삭제시에는? 사용자 검증 패스해야함... 조건문에 id가 0일경우? 게시글삭제라고할까?
     */
    @Transactional
    public void deleteComment(Long id, CommentDeleteRequest commentDeleteRequest) {
        Comment comment = commentRepository.findById(commentDeleteRequest.commentId()).orElseThrow(
                () -> new BusinessException(ExceptionCode.COMMENT_NOT_EXIST));
        if ((Objects.equals(comment.getWriter().getId(), id) && Objects.equals(comment.getPost().getId(), commentDeleteRequest.postId())) || (id == -1L)) {

            //해당 댓글이 자식이 있으면 해당 자식들의 멘션 삭제
            if (comment.getChildComments().size() > 0) {
                //대댓글들의 멘션 삭제
                comment.getChildComments().forEach(
                        childComment -> {
                            System.out.println(childComment.getId());
                            mentionService.deleteCommentMention(new CommentDeleteRequest(commentDeleteRequest.postId(), childComment.getId()));
                        }
                );
            }
            //해당 댓글 언급 삭제
            mentionService.deleteCommentMention(commentDeleteRequest);
            //해당 댓글 및 자식 댓글 삭제
            commentRepository.deleteById(commentDeleteRequest.commentId());
        } else throw new BusinessException(ExceptionCode.NOT_COMMENT_WRITER);
    }

    /*
    댓글 조회
    1. 해당 게시글 댓글 생성순으로 오름차순 조회
    2. 데이터 가공후 전달
     */
    public List<CommentResponse> getComment(Long id, Long postId) {
        List<Object[]> comments = commentRepository.findAllCommentByPostIdWithUserLiked(postId, id);
        List<CommentResponse> responses = new ArrayList<>();
        comments.forEach(
                objects -> {
                    Comment comment = (Comment) objects[0];
                    responses.add(CommentResponse.builder()
                            .commentId(comment.getId())
                            .postMember(new PostMember(comment.getWriter().getId(), comment.getWriter().getNickname(),comment.getWriter().getUrl()))
                            .content(comment.getContent())
                            .likeCnt(comment.getLikeCnt())
                            .createdAt(comment.getCreatedAt())
                            .commentReplyCnt(comment.getChildComments().size())
                            .liked((Boolean) objects[1])
                            .build());
                }
        );
        return responses;
    }

    /*
    대댓글 조회
     */
    public List<CommentResponse> getCommentReply(Long id, Long postId, Long commentId) {
        List<Object[]> comments = commentRepository.findAllCommentReplyByPostIdWithUserLiked(postId, id, commentId);
        List<CommentResponse> responses = new ArrayList<>();
        comments.forEach(
                objects -> {
                    Comment comment = (Comment) objects[0];
                    responses.add(CommentResponse.builder()
                            .commentId(comment.getId())
                            .postMember(new PostMember(comment.getWriter().getId(), comment.getWriter().getNickname(),comment.getWriter().getUrl()))
                            .content(comment.getContent())
                            .likeCnt(comment.getLikeCnt())
                            .createdAt(comment.getCreatedAt())
                            .commentReplyCnt(0)
                            .liked((Boolean) objects[1])
                            .build());
                }
        );
        return responses;
    }

    /*
    댓글 좋아요 기능
     */
    @Transactional
    public void addLike(Long id, CommentLikeRequest commentLikeRequest) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentLikeRequest.commentId()).orElseThrow(
                () -> new BusinessException(ExceptionCode.COMMENT_NOT_EXIST));

        Optional<CommentLike> commentLike = commentLikeRepository.findByMemberIdAndCommentId(member.getId(), comment.getId());
        if (commentLike.isEmpty()) {
            CommentLike like = CommentLike.builder()
                    .comment(comment)
                    .member(member)
                    .build();
            commentLikeRepository.save(like);
            comment.plusLikeCnt();
        }
    }

    /*
    댓글 좋아요 취소 기능
     */
    @Transactional
    public void deleteLike(Long id, CommentLikeRequest commentLikeRequest) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentLikeRequest.commentId()).orElseThrow(
                () -> new BusinessException(ExceptionCode.COMMENT_NOT_EXIST));

        Optional<CommentLike> commentLike = commentLikeRepository.findByMemberIdAndCommentId(member.getId(), comment.getId());
        if (commentLike.isPresent()) {
            commentLikeRepository.deleteByMemberIdAndCommentId(member.getId(), comment.getId());
            comment.minusLikeCnt();
        }
    }

}
