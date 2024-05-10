package com.example.cns.feed.post.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.service.S3Service;
import com.example.cns.common.type.FileType;
import com.example.cns.feed.post.domain.Post;
import com.example.cns.feed.post.domain.PostFile;
import com.example.cns.feed.post.domain.repository.PostFileRepository;
import com.example.cns.feed.post.domain.repository.PostRepository;
import com.example.cns.feed.post.dto.request.PostPatchRequest;
import com.example.cns.feed.post.dto.request.PostRequest;
import com.example.cns.feed.post.dto.response.PostFileResponse;
import com.example.cns.feed.post.dto.response.PostMember;
import com.example.cns.feed.post.dto.response.PostResponse;
import com.example.cns.hashtag.service.HashTagService;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.mention.service.MentionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final HashTagService hashTagService;
    private final MentionService mentionService;
    private final S3Service s3Service;

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
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
        if (postRequest.postFileList() != null) {
            postRequest.postFileList().stream().forEach(
                    file -> {
                        PostFile postfile = PostFile.builder()
                                .post(postRepository.findById(responseId).get())
                                .url(file.uploadFileURL())
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
        if (post.isPresent()) {
            if (post.get().getMember().getId() == id) {
                hashTagService.deleteHashTag(postId); //해시태그 삭제
                mentionService.deletePostMention(postId); //멘션 테이블 삭제
                postRepository.deleteById(postId); //게시글 삭제
            } else
                throw new BusinessException(ExceptionCode.INCORRECT_INFO);
        } else {
            throw new BusinessException(ExceptionCode.POST_NOT_EXIST);
        }
    }

    /*
    모든 게시글 조회
     */
    public List<PostResponse> getPosts(Long cursorValue) {

        if (cursorValue == null || cursorValue == 0) cursorValue = postRepository.getMaxPostId() + 1;

        List<Post> posts = postRepository.findPostsByCursor(10, cursorValue);
        List<PostResponse> postResponses = new ArrayList<>();

        posts.forEach(post -> {
            postResponses.add(PostResponse.builder()
                    .id(post.getId())
                    .postMember(new PostMember(post.getMember().getId(), post.getMember().getNickname()))
                    .content(post.getContent())
                    .likeCnt(post.getLikeCnt())
                    .fileCnt(post.getFileCnt())
                    .commentCnt(post.getComments().size())
                    .createdAt(post.getCreatedAt())
                    .isCommentEnabled(post.isCommentEnabled())
                    .build());
        });
        return postResponses;
    }

    /*
    게시글 미디어 조회
    1. 해당 게시글 id 받아옴
    2. url 전달
    3. 끝
     */
    public List<PostFileResponse> getPostMedia(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        List<PostFileResponse> postFileResponses = new ArrayList<>();
        if (post.isPresent()) {
            List<PostFile> allPostFile = postFileRepository.findAllByPostId(postId);
            allPostFile.forEach(
                    postFile -> {
                        postFileResponses.add(PostFileResponse.builder()
                                .uploadFileName(postFile.getFileName())
                                .uploadFileURL(postFile.getUrl())
                                .fileType(postFile.getFileType())
                                .build());
                    }
            );
            return postFileResponses;
        } else return null;//throw new BusinessException();
    }

    /*
    게시글 수정
    1. 변경된 데이터만 가져온다.
    2. 일부 데이터만 update
    3. 해시태그 수정시 -> 해시태그 확인 후 수정
    4. 멘션 수정시 -> 맨션 테이블 확인 후 수정
    5. 사진 수정시 -> 사진 테이블 확인 후 수정, S3 수정
     */
    @Transactional
    public void updatePost(Long id, Long postId, @Valid PostPatchRequest postPatchRequest) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            if (post.get().getMember().getId() == id) {
                String previousContent = post.get().getContent(); //이전 게시글에서 해시태그, 멘션 추출 해서 비교

                post.get().updateContent(postPatchRequest.content());

                //멘션 수정 로직
                List<String> previousMentions = extractMention(previousContent);
                List<String> updateMentions = postPatchRequest.mention();
                List<String> addedMentions = updateMentions.stream()
                        .filter(mention -> !previousMentions.contains(mention))
                        .collect(Collectors.toList());
                List<String> removedMentions = previousMentions.stream()
                        .filter(mention -> !updateMentions.contains(mention))
                        .collect(Collectors.toList());

                mentionService.updateMention(postId, addedMentions, removedMentions);
                //이후에 바뀐 멘션으로 개수 바꾸기
                post.get().updateMentionCnt(postPatchRequest.mention().size());
                //멘션 수정 로직

                //해시태그 수정 로직
                List<String> previousHashTags = extractHashTag(previousContent);
                List<String> updateHashTags = postPatchRequest.hashtag();
                List<String> addedHashTags = updateHashTags.stream()
                        .filter(hashtag -> !previousHashTags.contains(hashtag))
                        .collect(Collectors.toList());
                List<String> removedHashTags = previousHashTags.stream()
                        .filter(hashtag -> !updateHashTags.contains(hashtag))
                        .collect(Collectors.toList());

                hashTagService.updateHashTag(postId, addedHashTags, removedHashTags);
                //해시태그 수정 로직

                //댓글 허용여부 수정
                post.get().updateIsCommentEnabled(postPatchRequest.isCommentEnabled());
                //댓글 허용여부 수정

                //미디어 변경 로직
                List<PostFile> previousFiles = postFileRepository.findAllByPostId(postId);
                List<PostFileResponse> updateFile = new ArrayList<>();

                postPatchRequest.postFileList().forEach(
                        file -> {
                            String url = file.uploadFileURL();
                            String fileName = file.uploadFileName();
                            FileType fileType = file.fileType();

                            //새로 추가된 파일
                            boolean isNewFile = previousFiles.stream()
                                    .noneMatch(previousFile -> previousFile.getFileName().equals(fileName) && previousFile.getUrl().equals(url) && previousFile.getFileType().equals(fileType));
                            if (isNewFile) {
                                PostFile newFile = PostFile.builder()
                                        .post(post.get())
                                        .url(url)
                                        .fileName(fileName)
                                        .fileType(fileType)
                                        .createdAt(LocalDateTime.now())
                                        .build();
                                postFileRepository.save(newFile);
                            }
                            //새로 추가된 파일
                        }
                );
                //삭제된 파일
                previousFiles.forEach(previousFile -> {
                    boolean isDeleted = postPatchRequest.postFileList().stream()
                            .noneMatch(file -> file.uploadFileName().equals(previousFile.getFileName()) && file.uploadFileURL().equals(previousFile.getUrl()) && file.fileType().equals(previousFile.getFileType()));
                    if (isDeleted) { //삭제된 파일이면 연관관계 삭제 + S3에서도 삭제
                        try {
                            postFileRepository.deleteById(previousFile.getId());
                            s3Service.deleteFile(previousFile.getFileName());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                //삭제된 파일

                //미디어 변경 로직

                //변경사항 저장
                postRepository.save(post.get());
                //변경사항 저장
            } //else return; //해당 사용자가 아닐경우 오류
        }
    }

    public List<String> extractMention(String content) {
        List<String> mentions = new ArrayList<>();
        String[] lines = content.split("\\r?\\n");
        Pattern pattern = Pattern.compile("@(\\w+)");
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                mentions.add(matcher.group(1));
            }
        }
        return mentions;
    }

    public List<String> extractHashTag(String content) {
        List<String> hashtags = new ArrayList<>();
        String[] lines = content.split("\\r?\\n");
        Pattern pattern = Pattern.compile("#\\S+");
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                hashtags.add(matcher.group());
            }
        }
        return hashtags;
    }

}
