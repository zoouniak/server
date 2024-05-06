package com.example.cns.feed.post.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.feed.post.domain.PostFile;
import com.example.cns.feed.post.domain.repository.PostFileRepository;
import com.example.cns.feed.post.dto.request.PostPatchRequest;
import com.example.cns.feed.post.dto.request.PostRequest;
import com.example.cns.feed.post.dto.response.PostMember;
import com.example.cns.feed.post.dto.response.PostResponse;
import com.example.cns.feed.post.domain.Post;
import com.example.cns.feed.post.domain.repository.PostRepository;
import com.example.cns.hashtag.service.HashTagService;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.mention.service.MentionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final MentionService mentionService;
    private final PostFileRepository postFileRepository;

    /*
    게시글 저장
    1. 사용자 id, 게시글 데이터 받아온다
    2. 사용자 id를 이용해 post 개체에 저장
    3. 게시글 저장
    4. 해시태그 추가 api 요청
    ++ 해시태그 추가 및 멘션 추가 필요
     */
    @Transactional
    public Long savePost(Long id, PostRequest postRequest) {
        Optional<Member> member = memberRepository.findById(id);
        Post post = postRequest.toEntity(member.get());
        Long responseId = postRepository.save(post).getId();

        //postRequest 에서 언급된 인원 가져와 멘션 테이블 저장
        mentionService.savePostMention(responseId, postRequest.mention());

        //파일이 있을시에 DB에 연관된 파일 저장
        if(postRequest.postFileList() != null){
            postRequest.postFileList().stream().forEach(
                    file -> {
                        PostFile postfile = PostFile.builder()
                                .post(postRepository.findById(responseId).get())
                                .url(file.uploadFileURL())
                                .uuid(file.uploadFileName())
                                .fileName(file.uploadFileName())
                                .fileType(file.fileType())
                                .createdAt(LocalDateTime.now())
                                .build();
                        postFileRepository.save(postfile);
                    }
            );
        }

        return responseId;
    }

    /*
    게시글 삭제
    1. 사용자 id, 게시글 id 받아온다
    2. 해당 게시글이 존재하는지?
    3. 해당 게시글과 사용자가 일치하는지?
    4. 삭제 성공
    + 사진 삭제 필요?
     */
    @Transactional
    public void deletePost(Long id, Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if(post.isPresent()){
            if(post.get().getMember().getId() == id){
                hashTagService.deleteHashTag(postId); //해시태그 삭제
                mentionService.deletePostMention(postId); //멘션 테이블 삭제
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
            return PostResponse.builder()
                    .id(post.get().getId())
                    .postMember(PostMember.builder()
                            .id(post.get().getMember().getId())
                            .nickname(post.get().getMember().getNickname())
                            .build())
                    .content(post.get().getContent())
                    .createdAt(post.get().getCreatedAt())
                    .likeCnt(post.get().getLikeCnt())
                    .mentionCnt(post.get().getMentionCnt())
                    .fileCnt(post.get().getFileCnt())
                    .isCommentEnabled(post.get().isCommentEnabled())
                    .build();
        else throw new BusinessException(ExceptionCode.POST_NOT_EXIST);
    }
    /*
    게시글 수정
    1. 변경된 데이터만 가져온다.
    2. 일부 데이터만 update
    3. 해시태그 수정시 -> 해시태그 확인 후 수정
    4. 멘션 수정시 -> 맨션 테이블 확인 후 수정
     */
    @Transactional
    public void updatePost(Long id, Long postId, @Valid PostPatchRequest postPatchRequest){
        Optional<Post> post = postRepository.findById(postId);
        if(post.isPresent()) {
            if(post.get().getMember().getId() == id) {
                String previousContent = post.get().getContent(); //이전 게시글에서 해시태그, 멘션 추출 해서 비교

                post.get().updateContent(postPatchRequest.content());

                if(postPatchRequest.mention() != null){ //수정된 멘션 값이 있을 경우
                    /*
                    1. 이전 멘션과 비교
                    2. 이전 멘션에서 추가된 사람, 사라진 사람 구하기
                    3. 멘션 테이블 업데이트
                     */
                } else { //수정된 멘션 값이 없을 경우
                    /*
                    이전 멘션된 사람들 삭제
                     */
                }
                //이후에 바뀐 멘션으로 개수 바꾸기
                post.get().updateMentionCnt(postPatchRequest.mention().size());

                if(postPatchRequest.hashtag() != null){ //수정된 해시태그 값이 있을 경우
                    /*
                    1. 이전 해시태그와 비교
                    2. 이전 해시태그에서 추가된 해시태그, 사라진 해시태그 구하기
                    3. 해시태그 테이블 업데이트
                     */
                }
                //댓글 허용여부 수정

            } else return; //해당 사용자가 아닐경우 오류
        }
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
