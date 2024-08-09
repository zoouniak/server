package com.example.cns.mention.service;

import com.example.cns.feed.comment.dto.request.CommentDeleteRequest;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.member.type.RoleType;
import com.example.cns.mention.domain.repository.MentionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentionServiceTest {
    @InjectMocks
    private MentionService mentionService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MentionRepository mentionRepository;

    @Test
    @DisplayName("게시글에서 사용자 언급시 저장이 되어야 한다.")
    void save_mention_on_post_success(){
        //given
        Long postId = 1L;
        Member member = createMember(1L,"testMember");
        List<String> mention = List.of("@testMember");

        //when
        when(memberRepository.findByNickname(anyString())).thenReturn(Optional.of(member));

        mentionService.savePostMention(postId,mention);

        //then
        verify(memberRepository,times(mention.size())).findByNickname(anyString());
        verify(mentionRepository,times(mention.size())).save(any());
    }

    @Test
    @DisplayName("게시글에서 언급한 사용자가 없을시에 저장이 되면 안된다.")
    void save_mention_on_post_fail(){
        //given
        Long postId = 1L;
        List<String> mention = List.of("@NoMember");

        //when
        when(memberRepository.findByNickname(anyString())).thenReturn(Optional.empty());

        mentionService.savePostMention(postId,mention);

        //then
        verify(memberRepository,times(mention.size())).findByNickname(anyString());
        verify(mentionRepository,times(0)).save(any());
    }

    @Test
    @DisplayName("게시글의 멘션은 삭제가 되어야 한다.")
    void delete_mention_on_post_success(){
        //given
        Long postId = 1L;

        //when
        mentionService.deletePostMention(postId);

        //then
        verify(mentionRepository,times(1)).deleteAllMentionBySubjectId(anyLong(),any());
    }

    @Test
    @DisplayName("댓글에서 사용자 언급시 저장이 가능해야 한다.")
    void save_mention_on_comment_success(){
        //given
        Long commentId = 1L;
        Member member = createMember(1L,"testMember");
        List<String> mention = List.of("@testMember");

        //when
        when(memberRepository.findByNickname(member.getNickname())).thenReturn(Optional.of(member));

        mentionService.saveCommentMention(commentId,mention);

        //then
        verify(memberRepository,times(mention.size())).findByNickname(anyString());
        verify(mentionRepository,times(mention.size())).save(any());

    }

    @Test
    @DisplayName("댓글에서 언급한 사용자가 없을시에 저장이 되면 안된다.")
    void save_mention_on_comment_fail(){
        //given
        Long commentId = 1L;
        List<String> mention = List.of("@NoMember");

        //when
        when(memberRepository.findByNickname("NoMember")).thenReturn(Optional.empty());

        mentionService.saveCommentMention(commentId,mention);

        //then
        verify(memberRepository,times(mention.size())).findByNickname(anyString());
        verify(mentionRepository,times(0)).save(any());
    }

    @Test
    @DisplayName("댓글의 멘션은 삭제가 되어야 한다.")
    void delete_mention_on_comment_success(){
        //given
        Long postId = 1L;
        Long commentId = 1L;

        //when
        mentionService.deleteCommentMention(new CommentDeleteRequest(postId,commentId));

        //then
        verify(mentionRepository,times(1)).deleteAllMentionBySubjectId(anyLong(),any());
    }

    @Test
    @DisplayName("게시글 언급은 수정이 되어야 한다.")
    void update_mention_on_post_success(){
        //given
        Long postId = 1L;
        Member member = createMember(1L,"testMember");
        Member member2 = createMember(2L,"testMember1");
        Member member3 = createMember(3L,"testMember2");
        List<String> addedMentions = List.of("testMember1","testMember2");
        List<String> removedMentions = List.of("testMember");

        //when
        when(memberRepository.findByNickname("testMember")).thenReturn(Optional.of(member));
        when(memberRepository.findByNickname("testMember1")).thenReturn(Optional.of(member2));
        when(memberRepository.findByNickname("testMember2")).thenReturn(Optional.of(member3));

        mentionService.updateMention(postId,addedMentions,removedMentions);

        //then
        verify(memberRepository,times(removedMentions.size()+addedMentions.size())).findByNickname(anyString());
        verify(mentionRepository,times(removedMentions.size())).deleteMentionBySubjectIdAndMentionTypeAndMember(anyLong(),any(),anyLong());
        verify(mentionRepository,times(addedMentions.size())).save(any());

    }

    private static Member createMember(Long memberId, String nickname) {
        return new Member(memberId, nickname, "testpassword", "email", "first", "second", LocalDate.now(), RoleType.EMPLOYEE, "");
    }
}