package com.example.cns.feed.post.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.service.S3Service;
import com.example.cns.common.type.FileType;
import com.example.cns.feed.comment.dto.request.CommentDeleteRequest;
import com.example.cns.feed.comment.service.CommentService;
import com.example.cns.feed.post.domain.Post;
import com.example.cns.feed.post.domain.PostFile;
import com.example.cns.feed.post.domain.PostLike;
import com.example.cns.feed.post.domain.repository.PostFileRepository;
import com.example.cns.feed.post.domain.repository.PostLikeRepository;
import com.example.cns.feed.post.domain.repository.PostListRepository;
import com.example.cns.feed.post.domain.repository.PostRepository;
import com.example.cns.feed.post.dto.request.PostLikeRequest;
import com.example.cns.feed.post.dto.request.PostPatchRequest;
import com.example.cns.feed.post.dto.request.PostRequest;
import com.example.cns.feed.post.dto.response.FileResponse;
import com.example.cns.feed.post.dto.response.MentionInfo;
import com.example.cns.feed.post.dto.response.PostDataListResponse;
import com.example.cns.feed.post.dto.response.PostResponse;
import com.example.cns.hashtag.domain.HashTagPost;
import com.example.cns.hashtag.domain.repository.HashTagPostRepository;
import com.example.cns.hashtag.domain.repository.HashTagRepository;
import com.example.cns.hashtag.domain.repository.HashTagSearchRepository;
import com.example.cns.hashtag.service.HashTagService;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.mention.domain.Mention;
import com.example.cns.mention.domain.repository.MentionRepository;
import com.example.cns.mention.service.MentionService;
import com.example.cns.mention.type.MentionType;
import com.example.cns.notification.event.PostLikeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
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
    private final CommentService commentService;

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostFileRepository postFileRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostListRepository postListRepository;
    private final MentionRepository mentionRepository;
    private final HashTagRepository hashTagRepository;
    private final HashTagSearchRepository hashTagSearchRepository;
    private final HashTagPostRepository hashTagPostRepository;

    private final ApplicationEventPublisher eventPublisher;

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
        Member member = isMemberExists(id);
        Long responseId = postRepository.save(postRequest.toEntity(member)).getId();

        //postRequest 에서 언급된 인원 가져와 멘션 테이블 저장
        mentionService.savePostMention(responseId, postRequest.mention());
        //postRequest 에서 만든 해시태그 저장
        hashTagService.createHashTag(responseId, postRequest.hashtag());

        //파일이 있을시에 DB에 연관된 파일 저장
        if (postRequest.postFileList() != null) {
            postRequest.postFileList().forEach(
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
        Post post = isPostExists(postId);

        if (post.getMember().getId().equals(id)) {

            //댓글 삭제
            post.getComments().forEach(
                    comment -> {
                        if (comment.getParentComment() == null)
                            commentService.deleteComment(-1L, new CommentDeleteRequest(postId, comment.getId()));
                    }
            );
            //댓글 삭제

            //해시태그 삭제
            List<HashTagPost> isHashTagList = hashTagPostRepository.findAllByPostId(postId);
            if (isHashTagList != null && !(isHashTagList.isEmpty()))
                hashTagService.deleteHashTag(isHashTagList);
            //해시태그 삭제

            //멘션 테이블 삭제
            List<Object[]> isMentionList = mentionRepository.findMentionsBySubjectId(List.of(postId), MentionType.FEED);
            if (isMentionList != null && !(isMentionList.isEmpty()))
                mentionService.deletePostMention(postId);
            //멘션 테이블 삭제

            //파일 삭제
            List<PostFile> isFileList = postFileRepository.findAllByPostId(postId);
            if (isFileList != null && !(isFileList.isEmpty())) {
                isFileList.forEach(
                        postFile -> {
                            try {
                                postFileRepository.deleteById(postFile.getId());
                                s3Service.deleteFile(postFile.getFileName(), "post");
                            } catch (IOException e) { //파일 삭제 실패
                                throw new BusinessException(ExceptionCode.IMAGE_DELETE_FAILED);
                            }
                        }
                );
            }
            //파일 삭제

            //게시글 삭제
            postRepository.deleteById(postId);
            //게시글 삭제

        } else throw new BusinessException(ExceptionCode.NOT_POST_WRITER);


    }

    public List<PostResponse> getPosts(Long cursorValue, Long page, Long memberId) {

        if (page == 0 || page == null) page = 1L;

        List<PostResponse> postResponses = postListRepository.findPosts(memberId, memberId, cursorValue, 10L, "posts", null, null);

        List<Long> postIds = postResponses.stream().map(PostResponse::getId).toList();

        Map<Long, List<MentionInfo>> mentionsMap = getMentionsMap(postIds);
        Map<Long, List<String>> hashtagsMap = getHashtagsMap(postIds);

        return postResponses.stream()
                .map(postResponse -> {
                    List<MentionInfo> mentions = mentionsMap.getOrDefault(postResponse.getId(), Collections.emptyList());
                    List<String> hashtags = hashtagsMap.getOrDefault(postResponse.getId(), Collections.emptyList());
                    return postResponse.withData(mentions, hashtags);
                })
                .collect(Collectors.toList());

    }


    /*
    게시글 미디어 조회
    1. 해당 게시글 id 받아옴
    2. url 전달
    3. 끝
     */
    public List<FileResponse> getPostMedia(Long postId) {
        List<PostFile> allPostFile = postFileRepository.findAllByPostId(postId);

        List<FileResponse> postFileResponses = allPostFile.stream()
                .map((file) -> new FileResponse(
                        file.getFileName(),
                        file.getUrl(),
                        file.getFileType()
                )).collect(Collectors.toList());

        return postFileResponses;
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
        Post post = isPostExists(postId);

        if (Objects.equals(post.getMember().getId(), id)) {
            String previousContent = post.getContent(); //이전 게시글에서 해시태그, 멘션 추출 해서 비교

            post.updateContent(postPatchRequest.content());

            //멘션 수정 로직
            List<String> previousMentions = extractMentionNickname(previousContent);
            List<String> updateMentions = postPatchRequest.mention();
            List<String> addedMentions = updateMentions.stream()
                    .filter(mention -> !previousMentions.contains(mention))
                    .collect(Collectors.toList());
            List<String> removedMentions = previousMentions.stream()
                    .filter(mention -> !updateMentions.contains(mention))
                    .collect(Collectors.toList());

            mentionService.updateMention(postId, addedMentions, removedMentions);
            //이후에 바뀐 멘션으로 개수 바꾸기
            post.updateMentionCnt(postPatchRequest.mention().size());
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
            post.updateIsCommentEnabled(postPatchRequest.isCommentEnabled());
            //댓글 허용여부 수정

            //미디어 변경 로직
            List<PostFile> previousFiles = postFileRepository.findAllByPostId(postId);

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
                                    .post(post)
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
                        s3Service.deleteFile(previousFile.getFileName(), "post");
                    } catch (IOException e) { //파일 수정 실패
                        throw new BusinessException(ExceptionCode.IMAGE_UPDATE_FAILED);
                    }
                }
            });
            post.updateFileCnt(postPatchRequest.postFileList().size());
            //삭제된 파일

            //미디어 변경 로직

            //변경사항 저장
            postRepository.save(post);
            //변경사항 저장
        } else throw new BusinessException(ExceptionCode.NOT_POST_WRITER);
    }

    @Transactional
    public void addLike(Long id, PostLikeRequest postLikeRequest) {
        Member member = isMemberExists(id);
        Post post = isPostExists(postLikeRequest.postId());

        if (!postLikeRepository.existsByPostAndMember(post, member)) { //좋아요 중복 방지
            postLikeRepository.save(PostLike.builder()
                    .member(member)
                    .post(post)
                    .build());
            post.plusLikeCnt();

            eventPublisher.publishEvent(new PostLikeEvent(post.getMember(), member, post.getId()));
        }
    }

    @Transactional
    public void deleteLike(Long id, PostLikeRequest postLikeRequest) {
        Member member = isMemberExists(id);
        Post post = isPostExists(postLikeRequest.postId());
        if (postLikeRepository.existsByPostAndMember(post, member)) {
            postLikeRepository.deleteByPostAndMember(post, member);
            post.minusLikeCnt();
        }
    }

    public PostResponse getPost(final Long postId, final Long memberId) {
        Post post = isPostExists(postId);
        Member member = isMemberExists(memberId);
        boolean isLike = postLikeRepository.existsByPostAndMember(post, member);
        List<Mention> mentions = mentionRepository.findAllBySubjectIdAndMentionType(postId, MentionType.FEED);
        List<String> postWithTagName = hashTagSearchRepository.getHashTagPostWithTagName(postId);
        return PostResponse.of(post, isLike, mentions, postWithTagName);
    }

    public PostDataListResponse getSpecificPost(Long id, Long postId) {
        Member member = isMemberExists(id);
        Post post = isPostExists(postId);
        if (Objects.equals(post.getMember().getId(), member.getId())) {

            List<String> mentions = extractMention(post.getContent());
            List<String> hashtags = extractHashTag(post.getContent());

            return PostDataListResponse.builder()
                    .mentions(mentions)
                    .hashtags(hashtags)
                    .build();

        } else throw new BusinessException(ExceptionCode.NOT_POST_WRITER);
    }

    private List<String> extractMentionNickname(String content) {
        List<String> mentions = new ArrayList<>();
        String[] lines = content.split("\\r?\\n");
        Pattern pattern = Pattern.compile("@(\\w+)");
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                mentions.add(matcher.group(1)); //@제외
            }
        }
        return mentions;
    }

    private List<String> extractHashTag(String content) {
        List<String> hashtags = new ArrayList<>();
        String[] lines = content.split("\\r?\\n");
        Pattern pattern = Pattern.compile("#(\\S+)");
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                hashtags.add(matcher.group(1)); //# 제외
            }
        }
        return hashtags;
    }

    private List<String> extractMention(String content) {
        List<String> mentions = new ArrayList<>();
        String[] lines = content.split("\\r?\\n");
        Pattern pattern = Pattern.compile("@\\S+");
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                mentions.add(matcher.group()); //@사람 형태
            }
        }
        return mentions;
    }

    private Member isMemberExists(Long id) {
        return memberRepository.findById(id).orElseThrow(
                () -> new BusinessException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    private Post isPostExists(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new BusinessException(ExceptionCode.POST_NOT_EXIST));
    }

    private Map<Long, List<MentionInfo>> getMentionsMap(List<Long> postIds) {
        List<Object[]> mentionsList = mentionRepository.findMentionsBySubjectId(postIds, MentionType.FEED);
        Map<Long, List<MentionInfo>> mentionsMap = new HashMap<>();
        for (Object[] mention : mentionsList) {
            Long postId = (Long) mention[0];
            Long mentionId = (Long) mention[1];
            String mentionNickname = (String) mention[2];
            mentionsMap.computeIfAbsent(postId, k -> new ArrayList<>()).add(MentionInfo.builder()
                    .nickname(mentionNickname)
                    .memberId(mentionId)
                    .build());
        }
        return mentionsMap;
    }

    private Map<Long, List<String>> getHashtagsMap(List<Long> postIds) {
        List<Object[]> hashtagsList = hashTagRepository.findHashTagNamesByPostIds(postIds);
        Map<Long, List<String>> hashtagsMap = new HashMap<>();
        for (Object[] hashtag : hashtagsList) {
            Long postId = (Long) hashtag[0];
            String tagName = (String) hashtag[1];
            hashtagsMap.computeIfAbsent(postId, k -> new ArrayList<>()).add(tagName);
        }
        return hashtagsMap;
    }
}
