package com.example.cns.feed.post.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.service.S3Service;
import com.example.cns.common.type.FileType;
import com.example.cns.feed.comment.dto.request.CommentDeleteRequest;
import com.example.cns.feed.comment.service.CommentService;
import com.example.cns.feed.post.domain.Post;
import com.example.cns.feed.post.domain.PostFile;
import com.example.cns.feed.post.domain.repository.PostFileRepository;
import com.example.cns.feed.post.domain.repository.PostLikeRepository;
import com.example.cns.feed.post.domain.repository.PostListRepository;
import com.example.cns.feed.post.domain.repository.PostRepository;
import com.example.cns.feed.post.dto.request.PostRequest;
import com.example.cns.feed.post.dto.response.FileResponse;
import com.example.cns.hashtag.domain.repository.HashTagRepository;
import com.example.cns.hashtag.service.HashTagService;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.member.type.RoleType;
import com.example.cns.mention.domain.repository.MentionRepository;
import com.example.cns.mention.service.MentionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private S3Service s3Service;
    @Mock
    private CommentService commentService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PostFileRepository postFileRepository;
    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private PostListRepository postListRepository;
    @Mock
    private MentionRepository mentionRepository;
    @Mock
    private HashTagRepository hashTagRepository;
    @Spy
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

        //when
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Long savedPostId = postService.savePost(member.getId(),postRequest);

        //then
        assertEquals(post.getId(),savedPostId);
        verify(postRepository,times(1)).save(any(Post.class));
        verify(mentionService,times(1)).savePostMention(savedPostId,mention);
        verify(hashTagService,times(1)).createHashTag(savedPostId,hashTag);
        verify(postFileRepository,times(0)).save(any(PostFile.class));
    }

    @Test
    @DisplayName("해시태그, 언급은 존재하며, 파일이 없는 경우에도 글 생성이 가능해야 한다.")
    void create_feed_success_with_hashtag_mention_no_fileList(){
        //given
        Member member = createMember();

        String content = "test content\n#test\n@test";
        List<String> hashTag = List.of("test");
        List<String> mention = List.of("@test");
        List<FileResponse> postFileList = new ArrayList<>();

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);

        //when
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Long savedPostId = postService.savePost(member.getId(),postRequest);

        //then
        assertEquals(post.getId(),savedPostId);

        verify(postRepository,times(1)).save(any(Post.class));
        verify(mentionService,times(1)).savePostMention(eq(savedPostId),eq(mention));
        verify(hashTagService,times(1)).createHashTag(eq(savedPostId),eq(hashTag));
        verify(postFileRepository,times(0)).save(any(PostFile.class));
    }

    @Test
    @DisplayName("해시태그, 언급, 파일이 존재할때 글 생성이 가능해야 한다.")
    void create_feed_success_with_hashtag_mention_fileList(){
        //given
        Member member = createMember();

        String content = "test content\n#test\n@test";
        List<String> hashTag = List.of("test");
        List<String> mention = List.of("@test");
        List<FileResponse> postFileList = List.of(new FileResponse("uploadFileName","/file1.png", FileType.PNG));

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);

        List<PostFile> postFiles = createPostFile(post,postFileList);

        //when
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postRepository.findById(any())).thenReturn(Optional.of(post));

        Long savedPostId = postService.savePost(member.getId(),postRequest);

        //then
        assertEquals(post.getId(),savedPostId);

        verify(postRepository,times(1)).save(any(Post.class));
        verify(mentionService,times(1)).savePostMention(eq(savedPostId),eq(mention));
        verify(hashTagService,times(1)).createHashTag(eq(savedPostId),eq(hashTag));
        verify(postFileRepository,times(postFiles.size())).save(any(PostFile.class));
    }

    @Test
    @DisplayName("게시글은 작성자만이 삭제가 가능해야 한다.")
    void delete_feed_only_writer(){
        //given
        Member member = createMember();

        String content = "test content";
        List<String> hashTag = new ArrayList<>();
        List<String> mention = new ArrayList<>();
        List<FileResponse> postFileList = new ArrayList<>();

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);

        //when
        when(postRepository.findById(null)).thenReturn(Optional.of(post));
        doNothing().when(hashTagService).deleteHashTag(post.getId());
        doNothing().when(mentionService).deletePostMention(post.getId());
        doNothing().when(postRepository).deleteById(post.getId());

        postService.deletePost(member.getId(),post.getId());

        //then
        verify(commentService,times(post.getComments().size())).deleteComment(anyLong(),any(CommentDeleteRequest.class));
        verify(hashTagService,times(1)).deleteHashTag(post.getId());
        verify(mentionService,times(1)).deletePostMention(post.getId());
        verify(postRepository,times(1)).deleteById(post.getId());

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

        //when
        when(postRepository.findById(null)).thenReturn(Optional.of(post));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            postService.deletePost(2L,post.getId());
        });

        //then
        assert(exception.getExceptionCode() == ExceptionCode.NOT_POST_WRITER);
    }
    private static Member createMember() {
        return new Member(1L, "test", "testpassword", "email", "first", "second", LocalDate.now(), RoleType.EMPLOYEE, "");
    }

    private static PostRequest craetePostRequest(String content, List<String> hashtag, List<String> mention, List<FileResponse> postFileList){
        return new PostRequest(content,hashtag,mention,true,postFileList);
    }

    private static List<PostFile> createPostFile(Post post, List<FileResponse> fileResponses){
        List<PostFile> postFiles = new ArrayList<>();
        fileResponses.forEach(
                fileResponse -> {
                    PostFile postFile = PostFile.builder()
                            .post(post)
                            .fileName(fileResponse.uploadFileName())
                            .url(fileResponse.uploadFileURL())
                            .fileType(fileResponse.fileType())
                            .createdAt(LocalDateTime.now())
                            .build();
                    postFiles.add(postFile);
                }
        );
        return postFiles;
    }

}