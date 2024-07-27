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
import com.example.cns.hashtag.domain.HashTag;
import com.example.cns.hashtag.domain.HashTagPost;
import com.example.cns.hashtag.domain.HashTagPostId;
import com.example.cns.hashtag.domain.repository.HashTagPostRepository;
import com.example.cns.hashtag.domain.repository.HashTagRepository;
import com.example.cns.hashtag.service.HashTagService;
import com.example.cns.member.domain.Member;
import com.example.cns.member.domain.repository.MemberRepository;
import com.example.cns.member.type.RoleType;
import com.example.cns.mention.domain.repository.MentionRepository;
import com.example.cns.mention.service.MentionService;
import com.example.cns.mention.type.MentionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;
import org.springframework.test.util.ReflectionTestUtils;

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
    private PostRepository postRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PostFileRepository postFileRepository;
    @Mock
    private HashTagPostRepository hashTagPostRepository;
    @Mock
    private MentionRepository mentionRepository;

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
    @DisplayName("해시태그, 언급은 존재하며, 파일이 없는 경우에도 글 생성이 가능해야 한다.")
    void create_feed_success_with_hashtag_mention_no_fileList(){
        //given
        Member member = createMember();

        String content = "test content\n#test #test1\n@testMember";
        List<String> hashTag = List.of("test", "test1");
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
        verify(hashTagService,times(1)).createHashTag(anyLong(),anyList());
        verify(postFileRepository,times(postFileList.size())).save(any(PostFile.class));
    }

    @Test
    @DisplayName("해시태그는 존재하지 않으며, 언급, 파일이 있는 경우에도 글 생성이 가능해야 한다.")
    void create_feed_success_with_no_hashtag_mention_fileList(){
        //given
        Member member = createMember();

        String content = "test content\n@testMember";
        List<String> hashTag = new ArrayList<>();
        List<String> mention = List.of("@testMember");
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
        verify(mentionService,times(1)).savePostMention(anyLong(),anyList());
        verify(hashTagService,times(0)).createHashTag(anyLong(),anyList());
        verify(postFileRepository,times(postFileList.size())).save(any(PostFile.class));
    }

    @Test
    @DisplayName("해시태그, 파일이 존재하며 언급이 없을때 글 생성이 가능해야 한다.")
    void create_feed_success_with_hashtag_no_mention_fileList(){
        //given
        Member member = createMember();

        String content = "test content\n#test #test1";
        List<String> hashTag = List.of("test","test1");
        List<String> mention = new ArrayList<>();
        List<FileResponse> postFileList = List.of(new FileResponse("uploadFileName","/file1.png", FileType.PNG));

        PostRequest postRequest = craetePostRequest(content,hashTag,mention,postFileList);
        Post post = postRequest.toEntity(member);
        ReflectionTestUtils.setField(post,"id",1L);

        //when
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        //hashTag 설정
        Long savedPostId = postService.savePost(member.getId(),postRequest);

        //then
        assertEquals(post.getId(),savedPostId);

        verify(postRepository,times(1)).save(any(Post.class));
        verify(mentionService,times(0)).savePostMention(anyLong(),anyList());
        verify(hashTagService,times(1)).createHashTag(anyLong(),anyList());
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
        List<FileResponse> postFileList = List.of(new FileResponse("uploadFileName","/file1.png", FileType.PNG));

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

    private void verifySavedPost(Post post, Long savedPostId, List<String> mention, List<String> hashTag, List<PostFile> postFiles){

        assertEquals(post.getId(),savedPostId);

        List<HashTagPost> savedHashTagPost = hashTagPostRepository.findAllByPostId(savedPostId);
        assertEquals(hashTag.size(),savedHashTagPost.size());
        for(HashTagPost hashTagPost : savedHashTagPost){
            assertEquals(savedPostId,hashTagPost.getId().getPost().longValue());
        }

        List<Object[]> savedMentions = mentionRepository.findMentionsBySubjectId(List.of(savedPostId), MentionType.FEED);
        assertEquals(mention.size(),savedMentions.size());
        for(Object[] mentionObject : savedMentions){
            assertEquals(savedPostId,mentionObject[0]);
        }

        List<PostFile> savedPostFiles = postFileRepository.findAllByPostId(savedPostId);
        assertEquals(postFiles.size(),savedPostFiles.size());
        for(PostFile postFile : savedPostFiles){
            assertEquals(savedPostId,postFile.getPost().getId());
        }

        verify(postRepository,times(1)).save(any(Post.class));
        verify(mentionService,times(1)).savePostMention(eq(savedPostId),eq(mention));
        verify(hashTagService,times(1)).createHashTag(eq(savedPostId),eq(hashTag));
        verify(postFileRepository,times(savedPostFiles.size())).save(any(PostFile.class));
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
        ReflectionTestUtils.setField(post,"id",1L);
        ReflectionTestUtils.setField(post, "member", member);

        //when
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        Long savedId = postService.savePost(member.getId(), postRequest);

        postService.deletePost(member.getId(),savedId);

        //then
        verify(commentService,times(post.getComments().size())).deleteComment(anyLong(),any(CommentDeleteRequest.class));
        verify(hashTagService,times(1)).deleteHashTag(eq(post.getId()));
        verify(mentionService,times(1)).deletePostMention(eq(post.getId()));
        verify(postRepository,times(1)).deleteById(eq(post.getId()));

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
    private static Member createMember() {
        return new Member(1L, "testMember", "testpassword", "email", "first", "second", LocalDate.now(), RoleType.EMPLOYEE, "");
    }

    private static PostRequest craetePostRequest(String content, List<String> hashtag, List<String> mention, List<FileResponse> postFileList){
        return new PostRequest(content,hashtag,mention,true,postFileList);
    }
}