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
import com.example.cns.hashtag.domain.repository.HashTagRepository;
import com.example.cns.hashtag.service.HashTagService;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.mention.domain.repository.MentionRepository;
import com.example.cns.mention.service.MentionService;
import com.example.cns.mention.type.MentionType;
import com.example.cns.notification.event.PostLikeEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
    private final ObjectMapper objectMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Value("${external-api.recommend-post}")
    private String api;

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
            //게시글의 댓글들 삭제 로직
            //todo 필요없는 로직
            post.getComments().forEach(
                    comment -> {
                        if (comment.getParentComment() == null)
                            commentService.deleteComment(-1L, new CommentDeleteRequest(postId, comment.getId()));
                    }
            );
            hashTagService.deleteHashTag(postId); //해시태그 삭제
            mentionService.deletePostMention(postId); //멘션 테이블 삭제
            postRepository.deleteById(postId); //게시글 삭제
        } else throw new BusinessException(ExceptionCode.NOT_POST_WRITER);

    }

    /*
    모든 게시글 조회
     */
    public List<PostResponse> getPosts(Long cursorValue, Long page, Long memberId) {

        if (page == 0 || page == null) page = 1L;

        //추천
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(makeUrl(memberId, page));
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    String responseJson = EntityUtils.toString(response.getEntity());
                    List<PostResponse> posts = objectMapper.readValue(responseJson, new TypeReference<>() {
                    });

                    if (posts.size() == 0) {
                        posts = getDefaultPosts(cursorValue, memberId);
                    } else if (posts.size() <= 9) { //추천받은 게시글이 10개를 만족못한다면, 필요한 만큼 데이터 찾아 추가하기
                        System.out.println(posts.size());
                        posts.addAll(postListRepository.findPosts(memberId, memberId, cursorValue, (10L - posts.size()), "posts", null, null));
                    }
                    return postResponseWithData(posts);

                } else { //추천으로 못받아 올경우 에러가 아닌 최신순 게시글 반환
                    return getDefaultPostsWithResponse(cursorValue, memberId);
                }
            } catch (IOException e) {
                throw new BusinessException(ExceptionCode.FAIL_GET_API);
            }
        } catch (IOException e) {
            throw new BusinessException(ExceptionCode.FAIL_GET_API);
        }
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

        // Optional<PostLike> postLike = isPostLikeExists(member, post);
        if (!postLikeRepository.existsByPostAndMember(post, member)) { //좋아요 중복 방지
            postLikeRepository.save(PostLike.builder()
                    .member(member)
                    .post(post)
                    .build());
            post.plusLikeCnt();

            eventPublisher.publishEvent(new PostLikeEvent(post.getMember(), member.getNickname(), post.getId()));
        }
    }

    @Transactional
    public void deleteLike(Long id, PostLikeRequest postLikeRequest) {
        Member member = isMemberExists(id);
        Post post = isPostExists(postLikeRequest.postId());
        if (postLikeRepository.existsByPostAndMember(post, member)) {
            postLikeRepository.deleteByPostAndMember(post, member);
        }

      /*  if (postLike.isPresent()) {
            postLikeRepository.deletePostLikeByMemberIdAndPostId(member.getId(), post.getId());
            post.minusLikeCnt();
        }*/
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

   /* private Optional<PostLike> isPostLikeExists(Member member, Post post) {
        return postLikeRepository.findByMemberIdAndPostId(member.getId(), post.getId());
    }*/

    private String makeUrl(Long memberId, Long page) {
        return api + "/" + memberId + "/" + page + "/10";
    }

    private List<PostResponse> postResponseWithData(List<PostResponse> posts) {
        List<Long> postIds = posts.stream().map(PostResponse::getId).toList();

        Map<Long, List<MentionInfo>> mentionsMap = getMentionsMap(postIds);
        Map<Long, List<String>> hashtagsMap = getHashtagsMap(postIds);

        return posts.stream()
                .map(postResponse -> {
                    List<MentionInfo> mentions = mentionsMap.getOrDefault(postResponse.getId(), Collections.emptyList());
                    List<String> hashtags = hashtagsMap.getOrDefault(postResponse.getId(), Collections.emptyList());
                    return postResponse.withData(mentions, hashtags);
                })
                .collect(Collectors.toList());
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

    private List<PostResponse> getDefaultPosts(Long cursorValue, Long memberId) {
        return postListRepository.findPosts(memberId, memberId, cursorValue, 10L, "posts", null, null);
    }

    private List<PostResponse> getDefaultPostsWithResponse(Long cursorValue, Long memberId) {
        List<PostResponse> postResponses = postListRepository.findPosts(memberId, memberId, cursorValue, 10L, "posts", null, null);
        return postResponseWithData(postResponses);
    }
}
