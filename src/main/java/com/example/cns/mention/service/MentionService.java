package com.example.cns.mention.service;

import com.example.cns.feed.comment.dto.request.CommentDeleteRequest;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.mention.domain.Mention;
import com.example.cns.mention.domain.repository.MentionRepository;
import com.example.cns.mention.type.MentionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentionService {

    private final MemberRepository memberRepository;
    private final MentionRepository mentionRepository;

    /*
    게시글 언급시 저장 기능
    1. 게시글 번호와 멘션 리스트 입력
    2. 멘션 리스트에서 사용자 추출
    3. 해당 사용자의 id값을 추가해 저장
     */
    @Transactional
    public void savePostMention(Long postId, List<String> mention) {

        List<String> mentionNickname = extractMember(mention);

        saveMention(postId,mentionNickname,MentionType.FEED);
    }

    @Transactional
    public void deletePostMention(Long postId) {
        mentionRepository.deleteAllMentionBySubjectId(postId, MentionType.FEED);
    }

    /*
    댓글,대댓글 언급시 저장 기능
    1. 댓글 번호와 멘션 리스트 입력
    2. 멘션 리스트에서 사용자 추출
    3. 해당 사용자의 id값을 추가해 저장
     */
    @Transactional
    public void saveCommentMention(Long commentId, List<String> mention) {

        List<String> mentionNickname = extractMember(mention);

        saveMention(commentId,mentionNickname,MentionType.COMMENT);

    }

    @Transactional
    public void deleteCommentMention(CommentDeleteRequest commentDeleteRequest) {
        mentionRepository.deleteAllMentionBySubjectId(commentDeleteRequest.commentId(),MentionType.COMMENT);
    }

    @Transactional
    public void updateMention(Long postId, List<String> addedMentions, List<String> removedMentions) {
        List<String> extractAddedMentions = extractMember(addedMentions);
        List<String> extractRemovedMentions = extractMember(removedMentions);

        //사라진 멘션 삭제
        extractRemovedMentions.forEach(
                nickname -> {
                    Optional<Member> member = memberRepository.findByNickname(nickname);
                    if(member.isPresent()){
                        mentionRepository.deleteMentionBySubjectIdAndMentionTypeAndMember(postId,MentionType.FEED,member.get().getId());
                    }
                }
        );

        //추가된 멘션 추가
        saveMention(postId,extractAddedMentions,MentionType.FEED);
    }

    //멘션 리스트에서 사용자 닉네임 추출
    private List<String> extractMember(List<String> mention){
        List<String> mentionNickname = new ArrayList<>();
        mention.forEach(nickname -> mentionNickname.add(nickname.replace("@","")));
        return mentionNickname;
    }

    //저장 메소드 중복되어서 메소드 만들었음
    private void saveMention(Long subjectId, List<String> mentionList, MentionType mentionType){
        mentionList.forEach(
                nickname -> {
                    Optional<Member> member = memberRepository.findByNickname(nickname);
                    if(member.isPresent()){
                        Mention data = Mention.builder()
                                .member(member.get())
                                .subjectId(subjectId)
                                .mentionType(mentionType)
                                .build();
                        mentionRepository.save(data);
                    }
                }
        );
    }
}
