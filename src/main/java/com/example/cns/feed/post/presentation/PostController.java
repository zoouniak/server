package com.example.cns.feed.post.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.feed.post.dto.request.PostRequest;
import com.example.cns.feed.post.dto.response.PostResponse;
import com.example.cns.feed.post.service.PostService;
import com.example.cns.hashtag.service.HashTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PostController {

    private final PostService postService;
    private final HashTagService hashTagService;

    /*
    게시글 작성
     */
    @PostMapping("/post")
    public ResponseEntity createPost(@Auth Long id, @RequestBody @Valid PostRequest postRequest){
        Long postId = postService.savePost(id, postRequest);
        return ResponseEntity.ok(postId);
    }

    /*
    게시글 수정
     */
    @PatchMapping("/post/{postId}")
    public ResponseEntity updatePost(@PathVariable Long postId){
        return ResponseEntity.ok().build();
    }

    /*
    게시글 삭제
     */
    @DeleteMapping("/post/{postId}")
    public ResponseEntity deletePost(@Auth Long id, @PathVariable Long postId){
        postService.deletePost(id, postId);
        return ResponseEntity.ok().build();
    }

    /*
    특정 게시글 조회, 단 게시글 내용만 조회
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<PostResponse> getSpecificPost(@PathVariable Long postId){
        PostResponse post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

}
