package com.example.cns.feed.post.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.feed.post.dto.request.PostFileRequest;
import com.example.cns.common.service.S3Service;
import com.example.cns.feed.post.dto.request.PostPatchRequest;
import com.example.cns.feed.post.dto.request.PostRequest;
import com.example.cns.feed.post.dto.response.PostFileResponse;
import com.example.cns.feed.post.dto.response.PostResponse;
import com.example.cns.feed.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게시글 API",description = "게시글과 관련된 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PostController {

    private final PostService postService;
    private final S3Service s3Service;

    /*
    게시글 작성
     */

    @Operation(summary = "게시글을 작성하는 api",description = "사용자로부터 게시글 작성에 필요한 정보, 서버에 업로드 된 사진을 입력받고, 게시글 번호를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "저장된 게시글의 번호를 반환한다.",
            content = @Content(schema = @Schema(implementation = long.class)))
    })
    @PostMapping("/post")
    public ResponseEntity createPost(@Auth Long id, @RequestBody @Valid PostRequest postRequest){
        Long postId = postService.savePost(id, postRequest);
        return ResponseEntity.ok(postId);
    }

    /*
    게시글 수정
     */
    @Operation(summary = "게시글을 수정하는 api(미완성)", description = "사용자로부터 수정된 정보를 받아와 수정한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "??")
    })
    @PatchMapping("/post/{postId}")
    public ResponseEntity updatePost(@Auth Long id, @PathVariable Long postId, @RequestBody @Valid PostPatchRequest postPatchRequest){
        //
        postService.updatePost(id, postId,postPatchRequest);
        return ResponseEntity.ok().build();
    }

    /*
    게시글 삭제
     */
    @Operation(summary = "게시글 삭제 api", description = "사용자가 삭제를 시도하면 게시글, 해시태그, 멘션, 사진을 삭제한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공시에 코드 200을 반환한다.")
            }
    )
    @DeleteMapping("/post/{postId}")
    public ResponseEntity deletePost(@Auth Long id, @PathVariable Long postId){
        postService.deletePost(id, postId);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "게시글 미디어 등록 api", description = "사용자로 부터 사진을 입력받고, 저장된 사진의 UUID, URL, 확장자를 반환한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "미디어 저장에 성공시에 해당 미디어의 저장정보를 반환한다.",
                    content = @Content(schema = @Schema(implementation = PostFileResponse.class)))
            }
    )
    @PostMapping("/post/media")
    public ResponseEntity uploadPostFile(PostFileRequest postFileRequest){
        List<PostFileResponse> postFileResponses = s3Service.uploadPostFile(postFileRequest);
        return ResponseEntity.ok(postFileResponses);
    }


    /*
    특정 게시글 조회, 단 게시글 내용만 조회

    이ㅣ녀석 api 문서에 없는데 왜 만든거지?
     */
    @Operation(summary = "특정 게시글 조회 api - 삭제 예정", description = "저장된게 잘 조회되는지 만들어 놓은용이라 삭제 예정입니다.")
    @GetMapping("/post/{postId}")
    public ResponseEntity<PostResponse> getSpecificPost(@PathVariable Long postId){
        PostResponse post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

}
