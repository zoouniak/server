package com.example.cns.hashtag.service;

import com.example.cns.hashtag.domain.HashTag;
import com.example.cns.hashtag.domain.HashTagPost;
import com.example.cns.hashtag.domain.HashTagPostId;
import com.example.cns.hashtag.domain.HashTagView;
import com.example.cns.hashtag.domain.repository.HashTagPostRepository;
import com.example.cns.hashtag.domain.repository.HashTagRepository;
import com.example.cns.hashtag.domain.repository.HashTagViewRepository;
import com.example.cns.hashtag.dto.response.HashTagSearchResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashTagServiceTest {

    @InjectMocks
    private HashTagService hashTagService;

    @Mock
    private HashTagRepository hashTagRepository;
    @Mock
    private HashTagPostRepository hashTagPostRepository;
    @Mock
    private HashTagViewRepository  hashTagViewRepository;

    @Test
    @DisplayName("새로운 해시태그가 생성이 되어야 한다.")
    void create_new_hashtag_success(){

        //given
        Long postId = 1L;
        List<String> hashtags = List.of("test","test1");

        HashTag savedHashTag1 = saved_hashtag("test",1L);
        HashTag savedHashTag2 = saved_hashtag("test1",2L);

        //when
        when(hashTagRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(hashTagRepository.save(any())).thenReturn(savedHashTag1,savedHashTag2);

        hashTagService.createHashTag(postId,hashtags);

        //then
        verify(hashTagRepository,times(hashtags.size())).findByName(anyString());
        verify(hashTagRepository,times(hashtags.size())).save(any());
        verify(hashTagPostRepository,times(hashtags.size())).save(any());

    }

    @Test
    @DisplayName("존재하는 해시태그여도 생성이 되어야 한다.")
    void create_existed_hashtag_success(){
        //given
        Long postId = 1L;
        List<String> hashtags = List.of("test","test1");

        HashTag savedHashTag1 = saved_hashtag("test",1L);
        HashTag savedHashTag2 = saved_hashtag("test1",2L);

        //when
        when(hashTagRepository.findByName("test")).thenReturn(Optional.of(savedHashTag1));
        when(hashTagRepository.findByName("test1")).thenReturn(Optional.empty());
        when(hashTagRepository.save(any())).thenReturn(savedHashTag1,savedHashTag2);

        hashTagService.createHashTag(postId,hashtags);

        //then
        verify(hashTagRepository,times(hashtags.size())).findByName(anyString());
        verify(hashTagRepository,times(hashtags.size()-1)).save(any());
        verify(hashTagPostRepository,times(hashtags.size())).save(any());
    }

    @Test
    @DisplayName("해시태그가 하나의 게시글에만 연관되어 있을때, 해시태그 삭제가 가능해야 한다.")
    void delete_hashtag_success(){
        //given
        Long postId = 1L;
        List<String> hashtags = List.of("test","test1");
        List<HashTagPost> savedHashTagPosts = hashTagPostList(hashtags);

        HashTagPost savedHashTagPost1 = new HashTagPost(new HashTagPostId(1L,postId));
        HashTagPost savedHashTagPost2 = new HashTagPost(new HashTagPostId(2L,postId));

        //when
        when(hashTagPostRepository.findAllByHashTagId(1L)).thenReturn(List.of(savedHashTagPost1));
        when(hashTagPostRepository.findAllByHashTagId(2L)).thenReturn(List.of(savedHashTagPost2));

        hashTagService.deleteHashTag(savedHashTagPosts);

        //then
        verify(hashTagPostRepository,times(savedHashTagPosts.size())).findAllByHashTagId(any());
        verify(hashTagRepository,times(savedHashTagPosts.size())).deleteById(anyLong());
        verify(hashTagPostRepository,times(savedHashTagPosts.size())).deleteById(any());
    }

    @Test
    @DisplayName("해시태그가 다른 게시글과 연관되어 있을때, 해시태그 삭제가 가능해야 한다.")
    void delete_hashtag_with_another_post_success(){
        //given
        Long postId1 = 1L;
        Long postId2 = 2L;
        List<String> hashtags = List.of("test","test1");
        List<HashTagPost> savedHashTagPosts = hashTagPostList(hashtags);

        HashTagPost savedHashTagPost1 = new HashTagPost(new HashTagPostId(1L,postId1));
        HashTagPost savedHashTagPost2 = new HashTagPost(new HashTagPostId(2L,postId1));
        HashTagPost savedHashTagPost3 = new HashTagPost(new HashTagPostId(1L,postId2));

        //when
        when(hashTagPostRepository.findAllByHashTagId(1L)).thenReturn(List.of(savedHashTagPost1,savedHashTagPost3));
        when(hashTagPostRepository.findAllByHashTagId(2L)).thenReturn(List.of(savedHashTagPost2));

        hashTagService.deleteHashTag(savedHashTagPosts);

        //then
        verify(hashTagPostRepository,times(savedHashTagPosts.size())).findAllByHashTagId(any());
        verify(hashTagRepository,times(savedHashTagPosts.size()-1)).deleteById(anyLong());
        verify(hashTagPostRepository,times(savedHashTagPosts.size())).deleteById(any());
    }

    @Test
    @DisplayName("해시태그가 하나의 게시글에만 연관되어 있을때, 해시태그 수정이 가능해야 한다.")
    void update_hashtag_success(){
        //given
        Long postId = 1L;
        List<String> addedHashTags = List.of("newHT");
        List<String> removedHashTags = List.of("test1");
        HashTag savedHashTag1 = saved_hashtag("newHT",3L);

        HashTagView existedHashTagView = new HashTagView();
        ReflectionTestUtils.setField(existedHashTagView,"hashtagId",1L);
        ReflectionTestUtils.setField(existedHashTagView,"name","test1");
        ReflectionTestUtils.setField(existedHashTagView,"postCnt",1L);

        //when
        when(hashTagViewRepository.findHashTagViewByName(anyString())).thenReturn(Optional.of(existedHashTagView));
        when(hashTagRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(hashTagRepository.save(any())).thenReturn(savedHashTag1);

        hashTagService.updateHashTag(postId,addedHashTags,removedHashTags);

        //then
        verify(hashTagViewRepository,times(removedHashTags.size())).findHashTagViewByName(anyString());
        verify(hashTagRepository,times(removedHashTags.size())).deleteById(anyLong());
        verify(hashTagPostRepository,times(removedHashTags.size())).deleteHashTagPostById_PostAndId_Hashtag(anyLong(),anyLong());

        verify(hashTagRepository,times(addedHashTags.size())).findByName(anyString());
        verify(hashTagRepository,times(addedHashTags.size())).save(any());
        verify(hashTagPostRepository,times(addedHashTags.size())).save(any());
    }

    @Test
    @DisplayName("해시태그가 여러 게시글에 연관되어 있을때, 해시태그 수정이 가능해야 한다.")
    void update_hashtag_with_another_post_success(){
        //given
        Long postId = 1L;
        List<String> addedHashTags = List.of("newHT");
        List<String> removedHashTags = List.of("test1");
        HashTag savedHashTag1 = saved_hashtag("newHT",3L);

        HashTagView existedHashTagView = new HashTagView();
        ReflectionTestUtils.setField(existedHashTagView,"hashtagId",1L);
        ReflectionTestUtils.setField(existedHashTagView,"name","test1");
        ReflectionTestUtils.setField(existedHashTagView,"postCnt",2L);

        //when
        when(hashTagViewRepository.findHashTagViewByName(anyString())).thenReturn(Optional.of(existedHashTagView));
        when(hashTagRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(hashTagRepository.save(any())).thenReturn(savedHashTag1);

        hashTagService.updateHashTag(postId,addedHashTags,removedHashTags);

        //then
        verify(hashTagViewRepository,times(removedHashTags.size())).findHashTagViewByName(anyString());
        verify(hashTagRepository,times(removedHashTags.size()-1)).deleteById(anyLong());
        verify(hashTagPostRepository,times(removedHashTags.size())).deleteHashTagPostById_PostAndId_Hashtag(anyLong(),anyLong());

        verify(hashTagRepository,times(addedHashTags.size())).findByName(anyString());
        verify(hashTagRepository,times(addedHashTags.size())).save(any());
        verify(hashTagPostRepository,times(addedHashTags.size())).save(any());
    }

    @Test
    @DisplayName("해시태그 검색시 해당 키워드를 포함하는 해시태그를 반환한다.")
    void search_hashtag_success(){
        //given
        String keyword = "t";
        List<HashTagView> hashTagViews = new ArrayList<>();

        HashTagView existedHashTagView = new HashTagView();
        ReflectionTestUtils.setField(existedHashTagView,"hashtagId",1L);
        ReflectionTestUtils.setField(existedHashTagView,"name","test1");
        ReflectionTestUtils.setField(existedHashTagView,"postCnt",2L);

        hashTagViews.add(existedHashTagView);

        HashTagView existedHashTagView2 = new HashTagView();
        ReflectionTestUtils.setField(existedHashTagView2,"hashtagId",2L);
        ReflectionTestUtils.setField(existedHashTagView2,"name","test2");
        ReflectionTestUtils.setField(existedHashTagView2,"postCnt",1L);

        hashTagViews.add(existedHashTagView2);

        //when
        when(hashTagViewRepository.findHashTagViewsByNameContainingIgnoreCase(keyword)).thenReturn(hashTagViews);

        List<HashTagSearchResponse> result = hashTagService.searchHashTag(keyword);

        //then
        List<HashTagSearchResponse> expected = List.of(
                new HashTagSearchResponse(existedHashTagView.getName(), existedHashTagView.getPostCnt()),
                new HashTagSearchResponse(existedHashTagView2.getName(),existedHashTagView2.getPostCnt())
        );

        assertEquals(expected,result);

    }

    private static HashTag saved_hashtag(String hashtags,Long id){
        HashTag hashTag = new HashTag(hashtags);
        ReflectionTestUtils.setField(hashTag,"id",id);
        return hashTag;
    }

    private static List<HashTagPost> hashTagPostList(List<String> hashTag){

        List<HashTagPost> result = new ArrayList<>();

        for(int i = 0; i < hashTag.size(); i++){
            Long hashTagId = (long) (i+1);
            result.add(new HashTagPost(new HashTagPostId(hashTagId,1L)));
        }

        return result;
    }

}