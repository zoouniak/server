package com.example.cns.hashtag.service;

import com.example.cns.hashtag.domain.HashTag;
import com.example.cns.hashtag.domain.HashTagPost;
import com.example.cns.hashtag.domain.HashTagPostId;
import com.example.cns.hashtag.domain.HashTagView;
import com.example.cns.hashtag.domain.repository.HashTagPostRepository;
import com.example.cns.hashtag.domain.repository.HashTagRepository;
import com.example.cns.hashtag.domain.repository.HashTagViewRepository;
import com.example.cns.hashtag.dto.response.HashTagSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HashTagService {

    private final HashTagRepository hashTagRepository;
    private final HashTagPostRepository hashTagPostRepository;
    private final HashTagViewRepository hashTagViewRepository;


    /*
    해시태그 검색
     */
    public List<HashTagSearchResponse> searchHashTag(String keyword) {
        List<HashTagSearchResponse> responses = new ArrayList<>();
        List<HashTagView> hashTags = hashTagViewRepository.findHashTagViewsByNameContainingIgnoreCase(keyword);
        hashTags.forEach(
                hashTagView ->
                        responses.add(
                                HashTagSearchResponse.builder()
                                        .name(hashTagView.getName())
                                        .postCnt(hashTagView.getPostCnt())
                                        .build()
                        )
        );
        return responses;
    }

    /*
    해시태그 추가
    1. 해시 태그 추가
    2. 게시글과 해시태그 연결
     */
    @Transactional
    public void createHashTag(Long postId, List<String> hashtags) {

        List<HashTag> responses = new ArrayList<>(); //게시글에 추가할 해시태그 리스트

        //해시태그 request를 한개씩 확인하면서 존재하는지? 확인 후 추가
        hashtags.forEach(requestHashTag -> {
            Optional<HashTag> hashTag = hashTagRepository.findByName(requestHashTag);
            if (hashTag.isEmpty()) { //해당하는 해시태그가 없을경우 생성후 선언
                hashTag = Optional.of(hashTagRepository.save(HashTag.builder().name(requestHashTag).build()));
            }
            responses.add(hashTag.get()); //해시태그 리스트에 추가

            //해시태그 연관관계 테이블 추가
            HashTagPostId id = HashTagPostId.builder()
                    .hashtagId(hashTag.get().getId())
                    .postId(postId)
                    .build();

            HashTagPost hashTagPost = HashTagPost.builder()
                    .id(id)
                    .build();

            hashTagPostRepository.save(hashTagPost);

        });
    }

    /*
    해시태그 삭제
    1. 게시글 관련 해시태그 삭제
    1-1. 단, 다른 게시글이 동일한 해시태그를 가지고있다면 삭제하면 안된다.
    2. 연관관계 테이블도 삭제
     */
    @Transactional
    public void deleteHashTag(List<HashTagPost> hashTagPostList) {

        hashTagPostList.forEach(
                hashTagPost -> {
                    List<HashTagPost> hashTagPostListByHashTag = hashTagPostRepository.findAllByHashTagId(hashTagPost.getId().getHashtag());
                    if (hashTagPostListByHashTag.size() <= 1) { //연관된 게시글이 단 한개라면 테이블 삭제 + 해시태그 삭제
                        hashTagRepository.deleteById(hashTagPost.getId().getHashtag());
                    } //연관된 게시글이 더 있다면 테이블만 삭제
                    hashTagPostRepository.deleteById(hashTagPost.getId());
                }
        );
    }

    /*
    해시태그 수정
     */
    @Transactional
    public void updateHashTag(Long postId, List<String> addedHashTags, List<String> removedHashTags) {
        //지워진 해시태그 삭제
        removedHashTags.forEach(
                removeHashTag -> {
                    Optional<HashTagView> hashTagView = hashTagViewRepository.findHashTagViewByName(removeHashTag);
                    if (hashTagView.isPresent()) {
                        if (hashTagView.get().getPostCnt() <= 1) { //해시태그 걸린 게시물이 1개인 경우, 해시태그와 연관관계 둘다 삭제
                            hashTagRepository.deleteById(hashTagView.get().getHashtagId());
                        }//해시태그 걸린 게시물이 1개 보다 많은 경우 연관관계 테이블만 삭제
                        hashTagPostRepository.deleteHashTagPostById_PostAndId_Hashtag(postId, hashTagView.get().getHashtagId());
                    }
                }
        );
        //지워진 해시태그 삭제

        //추가된 해시태그 생성
        createHashTag(postId, addedHashTags);
        //추가된 해시태그 생성
    }
}
