package com.example.cns.mention.service;

import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.mention.domain.Mention;
import com.example.cns.mention.repository.MentionRepository;
import com.example.cns.mention.type.MentionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    3. 해당 사용자의 id값을
     */
    @Transactional
    public void savePostMention(Long postId, List<String> mention){

        List<String> mentionNickname = new ArrayList<>();
        mention.stream().forEach(nickname -> {
            mentionNickname.add(nickname.replace("@",""));
        });

        mentionNickname.forEach(nickname -> {
            Mention data = Mention.builder()
                    .member(memberRepository.findByNickname(nickname).get())
                    .subjectId(postId)
                    .mentionType(MentionType.FEED)
                    .build();
            mentionRepository.save(data);
        });
    }

    @Transactional
    public void deletePostMention(Long postId){
        mentionRepository.deleteAllMentionBySubjectId(postId,MentionType.FEED);
    }
}
