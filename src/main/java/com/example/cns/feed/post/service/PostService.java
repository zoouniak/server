package com.example.cns.feed.post.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.feed.post.dto.request.PostRequest;
import com.example.cns.feed.post.dto.response.PostMember;
import com.example.cns.feed.post.dto.response.PostResponse;
import com.example.cns.feed.post.domain.Post;
import com.example.cns.feed.post.domain.repository.PostRepository;
import com.example.cns.hashtag.service.HashTagService;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.common.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final HashTagService hashTagService;

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    /*
    게시글 저장
    1. 사용자 id, 게시글 데이터 받아온다
    2. 사용자 id를 이용해 post 개체에 저장
    3. 게시글 저장

    ++ 해시태그 추가 및 멘션 추가 필요
     */
    @Transactional
    public Long savePost(Long id, PostRequest postRequest) {
        Optional<Member> member = memberRepository.findById(id);
        Post post = postRequest.toEntity(member.get());
        return postRepository.save(post).getId();
    }

    /*
    게시글 삭제
    1. 사용자 id, 게시글 id 받아온다
    2. 해당 게시글이 존재하는지?
    3. 해당 게시글과 사용자가 일치하는지?
    4. 삭제 성공
    + 해당 게시글과 연관관계가 있는 해시태그, 연관관계 테이블 삭제 필요
     */
    @Transactional
    public void deletePost(Long id, Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if(post.isPresent()){
            if(post.get().getMember().getId() == id){
                hashTagService.deleteHashTag(postId); //해시태그 삭제
                postRepository.deleteById(postId); //게시글 삭제
            }
            else
                throw new BusinessException(ExceptionCode.INCORRECT_INFO);
        }else {
            throw new BusinessException(ExceptionCode.POST_NOT_EXIST);
        }
    }

    /*
    특정 게시글 조회
    1. 게시글 id 받아옴
    2. 해당 게시글이 존재하는지?
    3. 존재하면 반환, 아니면 오류 반환
     */
    public PostResponse getPostById(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if(post.isPresent())
            return new PostResponse(post.get().getId(),new PostMember(post.get().getMember().getId(),post.get().getMember().getNickname()),post.get().getContent(),post.get().getCreatedAt(),post.get().getLike_cnt(),post.get().getMention_cnt(),post.get().isCommentEnabled());
        else
            throw new BusinessException(ExceptionCode.POST_NOT_EXIST);
    }
    /*
    게시글 수정
     */
    public void updatePost(){

    }

    /*
    해시태그 추출
     */
    public List<String> extractHashTag(String content){

        List<String> hashtags = new ArrayList<>();

        Pattern pattern = Pattern.compile("#\\S+");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            hashtags.add(matcher.group());
        }

        return hashtags;
    }
}
