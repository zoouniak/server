package com.example.cns.hashtag.service;

import com.example.cns.feed.post.dto.response.PostResponse;
import com.example.cns.hashtag.domain.HashTag;
import com.example.cns.hashtag.domain.repository.HashTagRepository;
import com.example.cns.hashtag.domain.repository.HashTagSearchRepository;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HashTagSearchService {
    private final HashTagRepository hashTagRepository;
    private final HashTagSearchRepository searchRepository;
    private final MemberRepository memberRepository;

    public List<PostResponse> getAllPostsByHashTag(String hashtag, Long postId, Long memberId) {
        Optional<HashTag> optionalHashTag = hashTagRepository.findByName(hashtag);
        if (optionalHashTag.isEmpty()) {
            return Collections.emptyList();
        }
        Member member = memberRepository.findById(memberId).get();
        return searchRepository.getPostsByHashTag(optionalHashTag.get().getId(), member, postId, 10);
    }

}
