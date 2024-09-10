package com.example.cns.feed.post.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.service.S3Service;
import com.example.cns.common.type.FileType;
import com.example.cns.feed.comment.domain.Comment;
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
import com.example.cns.hashtag.domain.HashTagPost;
import com.example.cns.hashtag.domain.HashTagPostId;
import com.example.cns.hashtag.domain.repository.HashTagPostRepository;
import com.example.cns.hashtag.service.HashTagService;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.member.type.RoleType;
import com.example.cns.mention.domain.repository.MentionRepository;
import com.example.cns.mention.service.MentionService;
import com.example.cns.mention.type.MentionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;
    @Mock
    private HashTagService hashTagService;
    @Mock
    private MentionService mentionService;
    @Mock
    private CommentService commentService;
    @Mock
    private S3Service s3Service;

    @Mock
    private PostRepository postRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PostFileRepository postFileRepository;
    @Mock
    private HashTagPostRepository hashTagPostRepository;
    @Mock
    private MentionRepository mentionRepository;
    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private PostListRepository postListRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("해시태그, 언급, 파일이 없는 경우에도 글 생성이 가능해야 한다.")
    void create_feed_success_with_no_hashtag_no_mention_no_fileList(){
        //given
        Member member = createMember();

        String content = "test content";
        List<String> hashTag = new ArrayList<>();
        List<String> mention = new ArrayList<>();
        List<FileResponse> postFileList = new ArrayList<>();

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);

        //when
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Long savedPostId = postService.savePost(member.getId(),postRequest);

        //then
        assertEquals(post.getId(),savedPostId);

        verify(postRepository,times(1)).save(any(Post.class));
        verify(mentionService,times(0)).savePostMention(anyLong(),anyList());
        verify(hashTagService,times(0)).createHashTag(anyLong(),anyList());
        verify(postFileRepository,times(postFileList.size())).save(any(PostFile.class));
    }

    @Test
    @DisplayName("해시태그가 존재하며, 언급, 파일이 없는 경우에도 글 생성이 가능해야 한다.")
    void create_feed_success_with_hashtag_no_mention_no_fileList(){
        //given
        Member member = createMember();

        String content = "test content\n#test #test1";
        List<String> hashTag = List.of("test", "test1");
        List<String> mention = new ArrayList<>();
        List<FileResponse> postFileList = new ArrayList<>();

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);

        //when
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Long savedPostId = postService.savePost(member.getId(),postRequest);

        //then
        assertEquals(post.getId(),savedPostId);

        verify(postRepository,times(1)).save(any(Post.class));
        verify(mentionService,times(0)).savePostMention(anyLong(),anyList());
        verify(hashTagService,times(1)).createHashTag(anyLong(),anyList());
        verify(postFileRepository,times(postFileList.size())).save(any(PostFile.class));
    }

    @Test
    @DisplayName("언급이 존재하며, 해시태그, 파일이 없는 경우에도 글 생성이 가능해야 한다.")
    void create_feed_success_with_no_hashtag_mention_no_fileList(){
        //given
        Member member = createMember();

        String content = "test content\n@testMember";
        List<String> hashTag = new ArrayList<>();
        List<String> mention = List.of("@testMember");
        List<FileResponse> postFileList = new ArrayList<>();

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);

        //when
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Long savedPostId = postService.savePost(member.getId(),postRequest);

        //then
        assertEquals(post.getId(),savedPostId);

        verify(postRepository,times(1)).save(any(Post.class));
        verify(mentionService,times(1)).savePostMention(anyLong(),anyList());
        verify(hashTagService,times(0)).createHashTag(anyLong(),anyList());
        verify(postFileRepository,times(postFileList.size())).save(any(PostFile.class));
    }

    @Test
    @DisplayName("해시태그, 언급이 존재하지 않으며, 파일이 존재할때 글 생성이 가능해야 한다.")
    void create_feed_success_with_no_hashtag_no_mention_fileList(){
        //given
        Member member = createMember();

        String content = "test content";
        List<String> hashTag = new ArrayList<>();
        List<String> mention = new ArrayList<>();
        List<FileResponse> postFileList = List.of(new FileResponse("uploadFileName1","/file1.png", FileType.PNG), new FileResponse("uploadFileName2","/file2.png",FileType.PNG));

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);

        //when
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        Long savedPostId = postService.savePost(member.getId(),postRequest);

        //then
        assertEquals(post.getId(),savedPostId);

        verify(postRepository,times(1)).save(any(Post.class));
        verify(mentionService,times(0)).savePostMention(anyLong(),anyList());
        verify(hashTagService,times(0)).createHashTag(anyLong(),anyList());
        verify(postFileRepository,times(postFileList.size())).save(any(PostFile.class));
    }

    @Test
    @DisplayName("해시태그, 언급, 파일이 존재할때 글 생성이 가능해야 한다.")
    void create_feed_success_with_hashtag_mention_fileList(){
        //given
        Member member = createMember();

        String content = "test content\n#test #test1\n@testMember";
        List<String> hashTag = List.of("test","test1");
        List<String> mention = List.of("@testMember");
        List<FileResponse> postFileList = List.of(new FileResponse("uploadFileName1","/file1.png", FileType.PNG), new FileResponse("uploadFileName2","/file2.png",FileType.PNG));

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);

        //when
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        Long savedPostId = postService.savePost(member.getId(),postRequest);

        //then
        assertEquals(post.getId(),savedPostId);

        verify(postRepository,times(1)).save(any(Post.class));
        verify(mentionService,times(1)).savePostMention(anyLong(),anyList());
        verify(hashTagService,times(1)).createHashTag(anyLong(),anyList());
        verify(postFileRepository,times(postFileList.size())).save(any(PostFile.class));
    }

    @Test
    @DisplayName("언급, 해시태그, 파일, 댓글이 없어도 삭제가 가능해야 한다.")
    void delete_feed_success_with_no_mention_no_hashtag_no_fileList_no_comment() throws IOException {
        //given
        Member member = createMember();

        String content = "test content";
        List<String> hashTag = new ArrayList<>();
        List<String> mention = new ArrayList<>();
        List<FileResponse> postFileList = new ArrayList<>();

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);

        //when
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        when(hashTagPostRepository.findAllByPostId(post.getId())).thenReturn(null);
        when(mentionRepository.findMentionsBySubjectId(List.of(post.getId()),MentionType.FEED)).thenReturn(null);
        when(postFileRepository.findAllByPostId(post.getId())).thenReturn(null);

        postService.deletePost(member.getId(),post.getId());

        //then
        verify(commentService,times(post.getComments().size())).deleteComment(anyLong(),any(CommentDeleteRequest.class));
        verify(hashTagService,times(0)).deleteHashTag(anyList());
        verify(mentionService,times(0)).deletePostMention(anyLong());
        verify(postFileRepository,times(0)).deleteById(anyLong());
        verify(s3Service,times(0)).deleteFile(anyString(),anyString());
        verify(postRepository,times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("언급이 존재하고, 해시태그, 파일, 댓글이 없어도 삭제가 가능해야 한다.")
    void delete_feed_success_with_mention_no_hashtag_no_fileList_no_comment() throws IOException {
        //given
        Member member = createMember();

        String content = "test content";
        List<String> hashTag = new ArrayList<>();
        List<String> mention = List.of("@testMember");
        List<FileResponse> postFileList = new ArrayList<>();

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);

        //when
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        when(hashTagPostRepository.findAllByPostId(post.getId())).thenReturn(null);
        when(mentionRepository.findMentionsBySubjectId(List.of(post.getId()),MentionType.FEED)).thenReturn(mentionObjectList(List.of(member)));
        when(postFileRepository.findAllByPostId(post.getId())).thenReturn(null);

        postService.deletePost(member.getId(),post.getId());

        //then
        verify(commentService,times(post.getComments().size())).deleteComment(anyLong(),any(CommentDeleteRequest.class));
        verify(hashTagService,times(0)).deleteHashTag(anyList());
        verify(mentionService,times(1)).deletePostMention(anyLong());
        verify(postFileRepository,times(0)).deleteById(anyLong());
        verify(s3Service,times(0)).deleteFile(anyString(),anyString());
        verify(postRepository,times(1)).deleteById(anyLong());

    }

    @Test
    @DisplayName("해시태그가 존재하고, 언급, 파일, 댓글이 없어도 삭제가 가능해야 한다.")
    void delete_feed_success_with_no_mention_hashtag_no_fileList_no_comment() throws IOException {
        //given
        Member member = createMember();

        String content = "test content\n#test #test1";
        List<String> hashTag = List.of("test","test1");
        List<String> mention = new ArrayList<>();
        List<FileResponse> postFileList = new ArrayList<>();

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);

        //when
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        when(hashTagPostRepository.findAllByPostId(post.getId())).thenReturn(hashTagPostList(hashTag));
        when(mentionRepository.findMentionsBySubjectId(List.of(post.getId()),MentionType.FEED)).thenReturn(null);
        when(postFileRepository.findAllByPostId(post.getId())).thenReturn(null);

        postService.deletePost(member.getId(),post.getId());

        //then
        verify(commentService,times(post.getComments().size())).deleteComment(anyLong(),any(CommentDeleteRequest.class));
        verify(hashTagService,times(1)).deleteHashTag(anyList());
        verify(mentionService,times(0)).deletePostMention(anyLong());
        verify(postFileRepository,times(0)).deleteById(anyLong());
        verify(s3Service,times(0)).deleteFile(anyString(),anyString());
        verify(postRepository,times(1)).deleteById(anyLong());

    }

    @Test
    @DisplayName("파일이 존재하고, 언급, 해시태그, 댓글이 없어도 삭제가 가능해야 한다.")
    void delete_feed_success_with_no_mention_no_hashtag_fileList_no_comment() throws IOException {
        //given
        Member member = createMember();

        String content = "test content";
        List<String> hashTag = new ArrayList<>();
        List<String> mention = new ArrayList<>();
        List<FileResponse> postFileList = List.of(new FileResponse("uploadFileName1","/file1.png", FileType.PNG), new FileResponse("uploadFileName2","/file2.png",FileType.PNG));

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);

        //when
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        when(hashTagPostRepository.findAllByPostId(post.getId())).thenReturn(null);
        when(mentionRepository.findMentionsBySubjectId(List.of(post.getId()),MentionType.FEED)).thenReturn(null);
        when(postFileRepository.findAllByPostId(post.getId())).thenReturn(fileList(post,postFileList));

        postService.deletePost(member.getId(),post.getId());

        //then
        verify(commentService,times(post.getComments().size())).deleteComment(anyLong(),any(CommentDeleteRequest.class));
        verify(hashTagService,times(0)).deleteHashTag(anyList());
        verify(mentionService,times(0)).deletePostMention(anyLong());
        verify(postFileRepository,times(postFileList.size())).deleteById(anyLong());
        verify(s3Service,times(postFileList.size())).deleteFile(anyString(),anyString());
        verify(postRepository,times(1)).deleteById(anyLong());

    }

    @Test
    @DisplayName("댓글이 존재하고, 언급, 해시태그, 파일이 없어도 삭제가 가능해야 한다.")
    void delete_feed_success_with_no_mention_no_hashtag_no_fileList_comment() throws IOException {
        //given
        Member member = createMember();

        String content = "test content";
        List<String> hashTag = new ArrayList<>();
        List<String> mention = new ArrayList<>();
        List<FileResponse> postFileList = new ArrayList<>();

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);
        ReflectionTestUtils.setField(post,"comments",createComment(post,member));

        //when
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        when(hashTagPostRepository.findAllByPostId(post.getId())).thenReturn(null);
        when(mentionRepository.findMentionsBySubjectId(List.of(post.getId()),MentionType.FEED)).thenReturn(null);
        when(postFileRepository.findAllByPostId(post.getId())).thenReturn(null);

        postService.deletePost(member.getId(),post.getId());

        //then
        verify(commentService,times(2)).deleteComment(anyLong(),any(CommentDeleteRequest.class));
        verify(hashTagService,times(0)).deleteHashTag(anyList());
        verify(mentionService,times(0)).deletePostMention(anyLong());
        verify(postFileRepository,times(postFileList.size())).deleteById(anyLong());
        verify(s3Service,times(postFileList.size())).deleteFile(anyString(),anyString());
        verify(postRepository,times(1)).deleteById(anyLong());

    }

    @Test
    @DisplayName("언급, 해시태그, 파일, 댓글이 있어도 삭제가 가능해야 한다.")
    void delete_feed_success_with_mention_hashtag_fileList_comment() throws IOException {
        //given
        Member member = createMember();

        String content = "test content\n #test #test1\n@testMember";
        List<String> hashTag = List.of("test","test1");
        List<String> mention = List.of("@testMember");
        List<FileResponse> postFileList = List.of(new FileResponse("uploadFileName1","/file1.png", FileType.PNG), new FileResponse("uploadFileName2","/file2.png",FileType.PNG));

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);
        ReflectionTestUtils.setField(post,"comments",createComment(post,member));

        //when
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        when(hashTagPostRepository.findAllByPostId(post.getId())).thenReturn(hashTagPostList(hashTag));
        when(mentionRepository.findMentionsBySubjectId(List.of(post.getId()),MentionType.FEED)).thenReturn(mentionObjectList(List.of(member)));
        when(postFileRepository.findAllByPostId(post.getId())).thenReturn(fileList(post,postFileList));

        postService.deletePost(member.getId(),post.getId());

        //then
        verify(commentService,times(2)).deleteComment(anyLong(),any(CommentDeleteRequest.class));
        verify(hashTagService,times(1)).deleteHashTag(anyList());
        verify(mentionService,times(1)).deletePostMention(anyLong());
        verify(postFileRepository,times(postFileList.size())).deleteById(anyLong());
        verify(s3Service,times(postFileList.size())).deleteFile(anyString(),anyString());
        verify(postRepository,times(1)).deleteById(anyLong());

    }

    @Test
    @DisplayName("게시글은 작성자만이 삭제가 가능해야 한다.")
    void delete_feed_only_writer() throws IOException {
        //given
        Member member = createMember();

        String content = "test content";
        List<String> hashTag = new ArrayList<>();
        List<String> mention = new ArrayList<>();
        List<FileResponse> postFileList = new ArrayList<>();

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);

        //when
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        postService.deletePost(member.getId(),post.getId());

        //then
        verify(commentService,times(post.getComments().size())).deleteComment(anyLong(),any(CommentDeleteRequest.class));
        verify(hashTagService,times(0)).deleteHashTag(anyList());
        verify(mentionService,times(0)).deletePostMention(anyLong());
        verify(postFileRepository,times(0)).deleteById(anyLong());
        verify(s3Service,times(0)).deleteFile(anyString(),anyString());
        verify(postRepository,times(1)).deleteById(anyLong());

    }

    @Test
    @DisplayName("작성자가 아닌 사람은 게시글을 삭제하면 안된다.")
    void delete_feed_not_writer(){
        //given
        Member member = createMember();

        String content = "test content";
        List<String> hashTag = new ArrayList<>();
        List<String> mention = new ArrayList<>();
        List<FileResponse> postFileList = new ArrayList<>();

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);

        //when
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            postService.deletePost(2L,post.getId());
        });

        //then
        assertEquals(ExceptionCode.NOT_POST_WRITER,exception.getExceptionCode());
    }

    @Test
    @DisplayName("게시글에는 좋아요가 가능해야 한다.")
    void post_like_success(){
        //given
        Member member = createMember();

        String content = "test content";
        List<String> hashTag = new ArrayList<>();
        List<String> mention = new ArrayList<>();
        List<FileResponse> postFileList = new ArrayList<>();

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);

        //when
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postLikeRepository.findByMemberIdAndPostId(anyLong(),anyLong())).thenReturn(Optional.empty());

        postService.addLike(member.getId(),new PostLikeRequest(post.getId()));

        //then
        assertEquals(1,post.getLikeCnt());

        verify(memberRepository,times(1)).findById(anyLong());
        verify(postRepository,times(1)).findById(anyLong());
        verify(postLikeRepository,times(1)).save(any(PostLike.class));
    }

    @Test
    @DisplayName("게시글에는 좋아요 취소가 가능해야 한다.")
    void post_like_cancel_success(){
        //given
        Member member = createMember();

        String content = "test content";
        List<String> hashTag = new ArrayList<>();
        List<String> mention = new ArrayList<>();
        List<FileResponse> postFileList = new ArrayList<>();

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);
        post.plusLikeCnt();

        PostLike postLike = createPostLike(member,post);

        //when
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postLikeRepository.findByMemberIdAndPostId(anyLong(),anyLong())).thenReturn(Optional.of(postLike));

        postService.deleteLike(member.getId(),new PostLikeRequest(post.getId()));

        //then
        assertEquals(0,post.getLikeCnt());

        verify(memberRepository,times(1)).findById(anyLong());
        verify(postRepository,times(1)).findById(anyLong());
        verify(postLikeRepository,times(1)).deletePostLikeByMemberIdAndPostId(anyLong(),anyLong());
    }

    @Test
    @DisplayName("게시글에 좋아요 중복은 불가능해야 한다.")
    void post_like_duplicate_fail(){
        //given
        Member member = createMember();

        String content = "test content";
        List<String> hashTag = new ArrayList<>();
        List<String> mention = new ArrayList<>();
        List<FileResponse> postFileList = new ArrayList<>();

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);
        post.plusLikeCnt();

        PostLike postLike = createPostLike(member,post);

        //when
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postLikeRepository.findByMemberIdAndPostId(anyLong(),anyLong())).thenReturn(Optional.of(postLike));

        postService.addLike(member.getId(),new PostLikeRequest(post.getId()));

        //then
        assertEquals(1,post.getLikeCnt());

        verify(memberRepository,times(1)).findById(anyLong());
        verify(postRepository,times(1)).findById(anyLong());
        verify(postLikeRepository,times(0)).save(any(PostLike.class));
    }

    @Test
    @DisplayName("게시글 내용 수정이 가능해야 한다.")
    void update_post_success_with_content() throws IOException {
        //given
        Member member = createMember();

        String content = "test content\n#test #test1\n@testMember";
        List<String> hashTag = List.of("test","test1");
        List<String> mention = List.of("testMember");
        List<FileResponse> postFileList = List.of(new FileResponse("uploadFileName1","/file1.png", FileType.PNG), new FileResponse("uploadFileName2","/file2.png",FileType.PNG));

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);
        ReflectionTestUtils.setField(post,"postFiles",postFileList);

        PostPatchRequest postPatchRequest = new PostPatchRequest(
                "Content Updated!!\n#test #test1\n@testMember",
                hashTag,
                mention,
                true,
                postFileList
        );

        //when
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postFileRepository.findAllByPostId(anyLong())).thenReturn(fileList(post,postFileList));

        postService.updatePost(member.getId(),post.getId(),postPatchRequest);

        //then
        assertEquals(postPatchRequest.content(),post.getContent());
        assertEquals(postPatchRequest.mention().size(),post.getMentionCnt());
        assertEquals(postPatchRequest.isCommentEnabled(),post.isCommentEnabled());
        assertEquals(postPatchRequest.postFileList().size(),post.getFileCnt());

        verify(postRepository,times(1)).findById(anyLong());

        verify(mentionService,times(0)).updateMention(anyLong(),anyList(),anyList());

        verify(hashTagService,times(0)).updateHashTag(anyLong(),anyList(),anyList());

        verify(postFileRepository,times(1)).findAllByPostId(anyLong());
        verify(postFileRepository,times(0)).save(any());

        verify(postFileRepository,times(0)).deleteById(anyLong());
        verify(s3Service,times(0)).deleteFile(anyString(),anyString());

        verify(postRepository,times(1)).save(any());
    }

    @Test
    @DisplayName("게시글 멘션 수정이 가능해야 한다.")
    void update_post_success_with_mention() throws IOException {
        //given
        Member member = createMember();

        String content = "test content\n#test #test1\n@testMember";
        List<String> hashTag = List.of("test","test1");
        List<String> mention = List.of("testMember");
        List<FileResponse> postFileList = List.of(new FileResponse("uploadFileName1","/file1.png", FileType.PNG), new FileResponse("uploadFileName2","/file2.png",FileType.PNG));

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);
        ReflectionTestUtils.setField(post,"postFiles",postFileList);

        PostPatchRequest postPatchRequest = new PostPatchRequest(
                "test content\n#test #test1\n@testMember1",
                hashTag,
                List.of("testMember1","testMember2"),
                true,
                postFileList
        );

        //when
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postFileRepository.findAllByPostId(anyLong())).thenReturn(fileList(post,postFileList));

        postService.updatePost(member.getId(),post.getId(),postPatchRequest);

        //then
        assertEquals(postPatchRequest.content(),post.getContent());
        assertEquals(postPatchRequest.mention().size(),post.getMentionCnt());
        assertEquals(postPatchRequest.isCommentEnabled(),post.isCommentEnabled());
        assertEquals(postPatchRequest.postFileList().size(),post.getFileCnt());

        verify(postRepository,times(1)).findById(anyLong());

        verify(mentionService,times(1)).updateMention(anyLong(),anyList(),anyList());

        verify(hashTagService,times(0)).updateHashTag(anyLong(),anyList(),anyList());

        verify(postFileRepository,times(1)).findAllByPostId(anyLong());
        verify(postFileRepository,times(0)).save(any());

        verify(postFileRepository,times(0)).deleteById(anyLong());
        verify(s3Service,times(0)).deleteFile(anyString(),anyString());

        verify(postRepository,times(1)).save(any());
    }

    @Test
    @DisplayName("게시글 해시태그 수정이 가능해야 한다.")
    void update_post_success_with_hashtag() throws IOException {
        //given
        Member member = createMember();

        String content = "test content\n#test #test1\n@testMember";
        List<String> hashTag = List.of("test","test1");
        List<String> mention = List.of("testMember");
        List<FileResponse> postFileList = List.of(new FileResponse("uploadFileName1","/file1.png", FileType.PNG), new FileResponse("uploadFileName2","/file2.png",FileType.PNG));

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);
        ReflectionTestUtils.setField(post,"postFiles",postFileList);

        PostPatchRequest postPatchRequest = new PostPatchRequest(
                "test content\n#test #test2\n@testMember1",
                List.of("test","test2"),
                mention,
                true,
                postFileList
        );

        //when
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postFileRepository.findAllByPostId(anyLong())).thenReturn(fileList(post,postFileList));

        postService.updatePost(member.getId(),post.getId(),postPatchRequest);

        //then
        assertEquals(postPatchRequest.content(),post.getContent());
        assertEquals(postPatchRequest.mention().size(),post.getMentionCnt());
        assertEquals(postPatchRequest.isCommentEnabled(),post.isCommentEnabled());
        assertEquals(postPatchRequest.postFileList().size(),post.getFileCnt());

        verify(postRepository,times(1)).findById(anyLong());

        verify(mentionService,times(0)).updateMention(anyLong(),anyList(),anyList());

        verify(hashTagService,times(1)).updateHashTag(anyLong(),anyList(),anyList());

        verify(postFileRepository,times(1)).findAllByPostId(anyLong());
        verify(postFileRepository,times(0)).save(any());

        verify(postFileRepository,times(0)).deleteById(anyLong());
        verify(s3Service,times(0)).deleteFile(anyString(),anyString());

        verify(postRepository,times(1)).save(any());
    }

    @Test
    @DisplayName("게시글 댓글 여부 수정이 가능해야 한다.")
    void update_post_success_with_IsCommentEnabled() throws IOException {
        //given
        Member member = createMember();

        String content = "test content\n#test #test1\n@testMember";
        List<String> hashTag = List.of("test","test1");
        List<String> mention = List.of("testMember");
        List<FileResponse> postFileList = List.of(new FileResponse("uploadFileName1","/file1.png", FileType.PNG), new FileResponse("uploadFileName2","/file2.png",FileType.PNG));

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);
        ReflectionTestUtils.setField(post,"postFiles",postFileList);

        PostPatchRequest postPatchRequest = new PostPatchRequest(
                content,
                hashTag,
                mention,
                false,
                postFileList
        );

        //when
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postFileRepository.findAllByPostId(anyLong())).thenReturn(fileList(post,postFileList));

        postService.updatePost(member.getId(),post.getId(),postPatchRequest);

        //then
        assertEquals(postPatchRequest.content(),post.getContent());
        assertEquals(postPatchRequest.mention().size(),post.getMentionCnt());
        assertEquals(postPatchRequest.isCommentEnabled(),post.isCommentEnabled());
        assertEquals(postPatchRequest.postFileList().size(),post.getFileCnt());

        verify(postRepository,times(1)).findById(anyLong());

        verify(mentionService,times(0)).updateMention(anyLong(),anyList(),anyList());

        verify(hashTagService,times(0)).updateHashTag(anyLong(),anyList(),anyList());

        verify(postFileRepository,times(1)).findAllByPostId(anyLong());
        verify(postFileRepository,times(0)).save(any());

        verify(postFileRepository,times(0)).deleteById(anyLong());
        verify(s3Service,times(0)).deleteFile(anyString(),anyString());

        verify(postRepository,times(1)).save(any());
    }

    @Test
    @DisplayName("게시글 이미지 수정이 가능해야 한다.")
    void update_post_success_with_postFile() throws IOException {
        //given
        Member member = createMember();

        String content = "test content\n#test #test1\n@testMember";
        List<String> hashTag = List.of("test","test1");
        List<String> mention = List.of("testMember");
        List<FileResponse> postFileList = List.of(new FileResponse("uploadFileName1","/file1.png", FileType.PNG), new FileResponse("uploadFileName2","/file2.png",FileType.PNG));
        List<FileResponse> updateFileList = List.of(new FileResponse("uploadFileName1","/file1.png",FileType.PNG), new FileResponse("uploadFileName3","/file3.png",FileType.PNG));

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);
        ReflectionTestUtils.setField(post,"postFiles",postFileList);

        PostPatchRequest postPatchRequest = new PostPatchRequest(
                content,
                hashTag,
                mention,
                true,
                updateFileList
        );

        //when
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postFileRepository.findAllByPostId(anyLong())).thenReturn(fileList(post,postFileList));

        postService.updatePost(member.getId(),post.getId(),postPatchRequest);
        ReflectionTestUtils.setField(post,"postFiles",updateFileList);

        //then
        assertEquals(postPatchRequest.content(),post.getContent());
        assertEquals(postPatchRequest.mention().size(),post.getMentionCnt());
        assertEquals(postPatchRequest.isCommentEnabled(),post.isCommentEnabled());
        assertEquals(postPatchRequest.postFileList().size(),post.getFileCnt());

        verify(postRepository,times(1)).findById(anyLong());

        verify(mentionService,times(0)).updateMention(anyLong(),anyList(),anyList());

        verify(hashTagService,times(0)).updateHashTag(anyLong(),anyList(),anyList());

        verify(postFileRepository,times(1)).findAllByPostId(anyLong());
        verify(postFileRepository,times(1)).save(any());

        verify(postFileRepository,times(1)).deleteById(anyLong());
        verify(s3Service,times(1)).deleteFile(anyString(),anyString());

        verify(postRepository,times(1)).save(any());
    }

    @Test
    @DisplayName("게시글 내용, 해시태그, 멘션, 댓글허용 여부, 사진 수정이 가능해야 한다.")
    void update_post_success_with_content_hashtag_mention_IsCommentEnabled_postFile() throws IOException {
        //given
        Member member = createMember();

        String content = "test content\n#test #test1\n@testMember";
        List<String> hashTag = List.of("test","test1");
        List<String> mention = List.of("testMember");
        List<FileResponse> postFileList = List.of(new FileResponse("uploadFileName1","/file1.png", FileType.PNG), new FileResponse("uploadFileName2","/file2.png",FileType.PNG));
        List<FileResponse> updateFileList = List.of(new FileResponse("uploadFileName1","/file1.png",FileType.PNG), new FileResponse("uploadFileName3","/file3.png",FileType.PNG));

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);
        ReflectionTestUtils.setField(post,"postFiles",postFileList);

        PostPatchRequest postPatchRequest = new PostPatchRequest(
                "Update Contents!\n#test #test3\n@testMember1 @testMember2",
                List.of("test","test3"),
                List.of("testMember1","testMember2"),
                false,
                updateFileList
        );

        //when
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postFileRepository.findAllByPostId(anyLong())).thenReturn(fileList(post,postFileList));

        postService.updatePost(member.getId(),post.getId(),postPatchRequest);
        ReflectionTestUtils.setField(post,"postFiles",updateFileList);

        //then
        assertEquals(postPatchRequest.content(),post.getContent());
        assertEquals(postPatchRequest.mention().size(),post.getMentionCnt());
        assertEquals(postPatchRequest.isCommentEnabled(),post.isCommentEnabled());
        assertEquals(postPatchRequest.postFileList().size(),post.getFileCnt());

        verify(postRepository,times(1)).findById(anyLong());

        verify(mentionService,times(1)).updateMention(anyLong(),anyList(),anyList());

        verify(hashTagService,times(1)).updateHashTag(anyLong(),anyList(),anyList());

        verify(postFileRepository,times(1)).findAllByPostId(anyLong());
        verify(postFileRepository,times(1)).save(any());

        verify(postFileRepository,times(1)).deleteById(anyLong());
        verify(s3Service,times(1)).deleteFile(anyString(),anyString());

        verify(postRepository,times(1)).save(any());
    }

    @Test
    @DisplayName("게시글 수정은 작성자만 가능해야 한다.")
    void update_post_fail_with_another_writer() throws IOException {
        //given
        Member member = createMember();

        String content = "test content\n#test #test1\n@testMember";
        List<String> hashTag = List.of("test","test1");
        List<String> mention = List.of("testMember");
        List<FileResponse> postFileList = List.of(new FileResponse("uploadFileName1","/file1.png", FileType.PNG), new FileResponse("uploadFileName2","/file2.png",FileType.PNG));

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);
        ReflectionTestUtils.setField(post,"postFiles",postFileList);

        PostPatchRequest postPatchRequest = new PostPatchRequest(
                content,
                hashTag,
                mention,
                true,
                postFileList
        );

        //when
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            postService.updatePost(2L,post.getId(),postPatchRequest);
        });

        //then
        assertEquals(ExceptionCode.NOT_POST_WRITER,exception.getExceptionCode());

        verify(postRepository,times(1)).findById(anyLong());

        verify(mentionService,times(0)).updateMention(anyLong(),anyList(),anyList());

        verify(hashTagService,times(0)).updateHashTag(anyLong(),anyList(),anyList());

        verify(postFileRepository,times(0)).findAllByPostId(anyLong());
        verify(postFileRepository,times(0)).save(any());

        verify(postFileRepository,times(0)).deleteById(anyLong());
        verify(s3Service,times(0)).deleteFile(anyString(),anyString());

        verify(postRepository,times(0)).save(any());
    }

    //테스트...?
//    @Test
//    @DisplayName("사용자별 추천 게시글 조회가 가능해야 한다.")
//    void get_post_success() throws IOException {
//        Long cursorValue = 0L;
//        Long page = 1L;
//        Long memberId = 1L;
//
//        List<PostResponse> recommendedPosts =new ArrayList<>();
//        recommendedPosts.add(new PostResponse());
//
//        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
//        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
//        StatusLine statusLine = mock(StatusLine.class);
//        HttpEntity httpEntity = mock(HttpEntity.class);
//        InputStream inputStream = new ByteArrayInputStream("[]".getBytes());
//
//        when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);
//        when(httpResponse.getStatusLine()).thenReturn(statusLine);
//        when(httpResponse.getStatusLine().getStatusCode()).thenReturn(200);
//        when(httpResponse.getEntity()).thenReturn(httpEntity);
//        when(httpEntity.getContent()).thenReturn(inputStream);
//        when(httpEntity.getContentLength()).thenReturn(2L);
//
//
//        when(objectMapper.readValue(anyString(), ArgumentMatchers.<JavaType>any())).thenReturn(recommendedPosts);
//
//        List<PostResponse> additionalPosts = new ArrayList<>();
//        additionalPosts.add(new PostResponse());
//        when(postListRepository.findPosts(anyLong(),anyLong(),anyLong(),anyLong(),anyString(),any(),any())).thenReturn(additionalPosts);
//
//        List<PostResponse> resultList = postService.getPosts(cursorValue,page,memberId);
//
//        assertEquals(2,resultList.size());
//    }

    @Test
    @DisplayName("게시글에 해당하는 이미지가 나와야한다.")
    void get_postImage_success(){

        //given
        List<PostFile> postFiles = new ArrayList<>();
        postFiles.add(new PostFile(null,"/file1.png","file1",LocalDateTime.now(),FileType.PNG));
        postFiles.add(new PostFile(null,"/file2.png","file2",LocalDateTime.now(),FileType.PNG));

        //when
        when(postFileRepository.findAllByPostId(anyLong())).thenReturn(postFiles);

        List<FileResponse> result = postService.getPostMedia(1L);

        //then
        List<FileResponse> expected = new ArrayList<>();
        expected.add(FileResponse.builder()
                        .uploadFileName("file1")
                        .uploadFileURL("/file1.png")
                        .fileType(FileType.PNG)
                .build());
        expected.add(FileResponse.builder()
                        .uploadFileName("file2")
                        .uploadFileURL("/file2.png")
                        .fileType(FileType.PNG)
                .build());

        assertEquals(expected,result);

        verify(postFileRepository,times(1)).findAllByPostId(anyLong());

    }


    private static Member createMember() {
        return new Member(1L, "testMember", "testpassword", "email", "first", "second", LocalDate.now(), RoleType.EMPLOYEE, "");
    }

    private static PostRequest craetePostRequest(String content, List<String> hashtag, List<String> mention, List<FileResponse> postFileList){
        return new PostRequest(content,hashtag,mention,true,postFileList);
    }

    private static List<Object[]> mentionObjectList(List<Member> members){
        List<Object[]> result = new ArrayList<>();

        for(Member member : members){
            result.add(new Object[]{
                    1L,member.getId(),member.getNickname()
            });
        }
        return result;
    }

    private static List<HashTagPost> hashTagPostList(List<String> hashTag){

        List<HashTagPost> result = new ArrayList<>();

        for(int i = 0; i < hashTag.size(); i++){
            Long hashTagId = (long) (i+1);
            result.add(new HashTagPost(new HashTagPostId(hashTagId,1L)));
        }

        return result;
    }

    private static List<PostFile> fileList(Post post,List<FileResponse> fileResponses){

        List<PostFile> result = new ArrayList<>();
        Long fileId = 1L;

        for(FileResponse file : fileResponses){
            PostFile postFile = PostFile.builder()
                    .post(post)
                    .fileName(file.uploadFileName())
                    .url(file.uploadFileURL())
                    .createdAt(LocalDateTime.now())
                    .fileType(file.fileType())
                    .build();
            ReflectionTestUtils.setField(postFile,"id",fileId++);
            result.add(postFile);
        }
        return result;
    }

    private static List<Comment> createComment(Post post, Member member){
        List<Comment> result = new ArrayList<>();

        Comment comment1 = Comment.builder()
                .writer(member)
                .post(post)
                .content("test Comment 1")
                .parentComment(null)
                .createdAt(LocalDateTime.now())
                .build();
        ReflectionTestUtils.setField(comment1,"id",1L);

        Comment comment2 = Comment.builder()
                .writer(member)
                .post(post)
                .content("test Comment 2")
                .parentComment(null)
                .createdAt(LocalDateTime.now())
                .build();
        ReflectionTestUtils.setField(comment2,"id",2L);

        Comment reply = Comment.builder()
                .writer(member)
                .post(post)
                .content("test reply comment")
                .parentComment(comment1)
                .createdAt(LocalDateTime.now())
                .build();
        ReflectionTestUtils.setField(reply,"id",3L);

        result.add(comment1);
        result.add(comment2);
        result.add(reply);

        return result;
    }

    private static PostLike createPostLike(Member member, Post post){
        return PostLike.builder()
                .post(post)
                .member(member)
                .build();
    }
}