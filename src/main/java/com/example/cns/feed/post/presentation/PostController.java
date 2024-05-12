package com.example.cns.feed.post.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.common.security.jwt.provider.JwtProvider;
import com.example.cns.common.service.S3Service;
import com.example.cns.feed.post.dto.request.PostFileRequest;
import com.example.cns.feed.post.dto.request.PostLikeRequest;
import com.example.cns.feed.post.dto.request.PostPatchRequest;
import com.example.cns.feed.post.dto.request.PostRequest;
import com.example.cns.feed.post.dto.response.FileResponse;
import com.example.cns.feed.post.dto.response.PostDataListResponse;
import com.example.cns.feed.post.dto.response.PostResponse;
import com.example.cns.feed.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

@Tag(name = "게시글 API", description = "게시글과 관련된 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PostController {

    private final PostService postService;
    private final S3Service s3Service;
    private final JwtProvider jwtProvider;

    /*
    게시글 작성
     */
    @Operation(summary = "게시글을 작성하는 api", description = "사용자로부터 게시글 작성에 필요한 정보, 서버에 업로드 된 사진을 입력받고, 게시글 번호를 반환한다.")
    @Parameters(
            value = {
                    @Parameter(name = "id", description = "JWT/사용자 id"),
                    @Parameter(name = "postRequest", description = "게시글 등록 요청 DTO")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "저장된 게시글의 번호를 반환한다.",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @PostMapping("/post")
    public ResponseEntity createPost(@Parameter(name = "id", description = "JWT/사용자 id") @Auth Long id, @RequestBody @Valid PostRequest postRequest) {
        Long postId = postService.savePost(id, postRequest);
        return ResponseEntity.ok(postId);
    }

    /*
    게시글 수정
     */
    @Operation(summary = "게시글 수정 api", description = "사용자로부터 수정된 정보를 받아와 수정한다.")
    @Parameters(
            value = {
                    @Parameter(name = "id", description = "JWT/사용자 id"),
                    @Parameter(name = "postPatchRequest", description = "게시글 수정 요청 DTO")
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정에 성공하면 200을 반환한다.")
    })
    @PatchMapping("/post/{postId}")
    public ResponseEntity updatePost(@Auth Long id, @PathVariable Long postId, @RequestBody @Valid PostPatchRequest postPatchRequest) {
        //
        postService.updatePost(id, postId, postPatchRequest);
        return ResponseEntity.ok().build();
    }

    /*
    게시글 삭제
     */
    @Operation(summary = "게시글 삭제 api", description = "사용자가 삭제를 시도하면 게시글, 해시태그, 멘션, 사진을 삭제한다.")
    @Parameters(
            value = {
                    @Parameter(name = "id", description = "JWT/사용자 id"),
                    @Parameter(name = "postId", description = "게시글 인덱스")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공시에 코드 200을 반환한다.")
            }
    )
    @DeleteMapping("/post/{postId}")
    public ResponseEntity deletePost(@Auth Long id, @PathVariable Long postId) {
        postService.deletePost(id, postId);
        return ResponseEntity.ok().build();
    }


    /*
    권한 부여 필요
     */
    @Operation(summary = "게시글 미디어 등록 api", description = "사용자로 부터 사진을 입력받고, 저장된 사진의 UUID, URL, 확장자를 반환한다.")
    @Parameters(
            value = {
                    //@Parameter (name = "id",description = "JWT/사용자 id"),
                    @Parameter(name = "postFileRequest", description = "게시글 미디어 등록 요청 DTO")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "미디어 저장에 성공시에 해당 미디어의 저장정보를 반환한다.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FileResponse.class))))
            }
    )
    @PostMapping("/post/media")
    public ResponseEntity uploadPostFile(PostFileRequest postFileRequest) {
        List<FileResponse> postFileResponses = s3Service.uploadFileList(postFileRequest.files(), "post");
        return ResponseEntity.ok(postFileResponses);
    }

    /*
    홈 화면 전체 게시물 조회
    cursor - based
    최초시에는 0이나 null값으로 할시 제일 최근것을 가져옴
    이후부터는 cursorValue에 따른 페이징
     */
    @Operation(summary = "홈 화면 게시글 조회 api", description = "사용자의 커서값을 이용해 무한 스크롤 조회를 한다." +
            "v1 - 변경 가능성 높음 : 초기값은 0 or null값 -> 최신 10개 조회, 이후부터 커서값(게시글 인덱스값)을 입력하면 해당 게시글 이후 내용을 가져옵니다.")
    @Parameters(
            value = {
                    @Parameter(name = "id", description = "JWT/사용자 id"),
                    @Parameter(name = "cursorValue", description = "마지막으로 본 게시글 인덱스")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "페이지 크기 (10) 만큼 게시글을 반환한다.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponse.class))))
            }
    )
    @GetMapping("/post/home")
    public ResponseEntity<List<PostResponse>> getPosts(
            @Auth Long id, @RequestParam(name = "cursorValue", required = false) Long cursorValue) {

        List<PostResponse> posts = postService.getPosts(cursorValue, id);
        return ResponseEntity.ok(posts);
    }

    /*
    게시글 미디어 조회
     */
    @Operation(summary = "특정 게시글 미디어 조회 api", description = "특정 게시글 인덱스값을 받아 미디어를 반환한다.")
    @Parameter(name = "postId", description = "게시글 인덱스")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "해당 게시글에 미디어가 있을시 미디어의 정보를 반환한다.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FileResponse.class))))
            }
    )
    @GetMapping("/post/{postId}/media")
    public ResponseEntity<List<FileResponse>> getPostMedia(@PathVariable Long postId) {
        List<FileResponse> postMedia = postService.getPostMedia(postId);
        return ResponseEntity.ok(postMedia);
    }

    /*
    게시글 좋아요 기능
     */
    @Operation(summary = "게시글 좋아요 api", description = "특정 게시글 인덱스값을 받아 좋아요를 한다.")
    @Parameters(
            value = {
                    @Parameter(name = "id", description = "JWT/사용자 id"),
                    @Parameter(name = "postLikeRequest", description = "게시글 좋아요 요청 DTO")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "좋아요 성공시 200을 반환한다.",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping("/post/like")
    public ResponseEntity addLike(@Auth Long id, @RequestBody PostLikeRequest postLikeRequest) {
        postService.addLike(id, postLikeRequest);
        return ResponseEntity.ok().build();
    }

    /*
    게시글 좋아요 취소 기능
     */
    @Operation(summary = "게시글 좋아요 취소 api", description = "특정 게시글 인덱스값을 받아 좋아요를 취소한다.")
    @Parameters(
            value = {
                    @Parameter(name = "id", description = "JWT/사용자 id"),
                    @Parameter(name = "postLikeRequest", description = "게시글 좋아요 요청 DTO")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "좋아요 취소 성공시 200을 반환한다.",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @DeleteMapping("/post/like")
    public ResponseEntity deleteLike(@Auth Long id, @RequestBody PostLikeRequest postLikeRequest) {
        postService.deleteLike(id, postLikeRequest);
        return ResponseEntity.ok().build();
    }

    /*
    게시글 수정 요청
     */
    @Operation(summary = "게시글 수정 요청 api", description = "특정 게시글 인덱스 값을 받아 게시글관련 데이터를 반환한다.")
    @Parameters(
            value = {
                    @Parameter(name = "id", description = "JWT/사용자 id"),
                    @Parameter(name = "postId", description = "게시글 인덱스")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "해당 게시글의 해시태그, 멘션 리스트를 반환한다.",
                            content = @Content(schema = @Schema(implementation = PostDataListResponse.class)))
            }
    )
    @GetMapping("/post/{postId}")
    public ResponseEntity<PostDataListResponse> getDataAboutPost(@Auth Long id, @PathVariable Long postId){
        PostDataListResponse response = postService.getSpecificPost(id, postId);
        return ResponseEntity.ok(response);
    }

}
