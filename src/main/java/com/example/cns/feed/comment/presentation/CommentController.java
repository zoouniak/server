package com.example.cns.feed.comment.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.feed.comment.dto.request.CommentDeleteRequest;
import com.example.cns.feed.comment.dto.request.CommentLikeRequest;
import com.example.cns.feed.comment.dto.request.CommentPostRequest;
import com.example.cns.feed.comment.dto.request.CommentReplyPostRequest;
import com.example.cns.feed.comment.dto.response.CommentResponse;
import com.example.cns.feed.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "댓글 API", description = "댓글과 관련된 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class CommentController {

    private final CommentService commentService;

    /*
    댓글 등록
     */
    @Operation(summary = "댓글 등록 api", description = "게시글 인덱스, 댓글 정보를 받아 댓글을 등록한다.")
    @Parameters(
            value = {
                    @Parameter(name = "id", description = "JWT/사용자 id"),
                    @Parameter(name = "commentPostRequest", description = "댓글 등록 요청 DTO")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "댓글 등록이 성공하면 200을 반환한다.",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping("/comment")
    public ResponseEntity writeComment(@Auth Long id, @RequestBody CommentPostRequest commentPostRequest) {
        commentService.createComment(id, commentPostRequest);
        return ResponseEntity.ok().build();
    }

    /*
    대댓글 등록
     */
    @Operation(summary = "대댓글 등록 api", description = "게시글 인덱스, 댓글 인덱스, 댓글 정보를 받아 대댓글을 등록한다.")
    @Parameters(
            value = {
                    @Parameter(name = "id", description = "JWT/사용자 id"),
                    @Parameter(name = "commentReplyPostRequest", description = "대댓글 등록 요청 DTO")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "대댓글 등록이 성공하면 200을 반환한다.",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping("/comment/reply")
    public ResponseEntity writeCommentReply(@Auth Long id, @RequestBody CommentReplyPostRequest commentReplyPostRequest) {
        commentService.createCommentReply(id, commentReplyPostRequest);
        return ResponseEntity.ok().build();
    }

    /*
    댓글 삭제
     */
    @Operation(summary = "댓글, 대댓글 삭제 api", description = "게시글 인덱스, 댓글 및 대댓글 인덱스를 받아 해당 댓글을 삭제한다.")
    @Parameters(
            value = {
                    @Parameter(name = "id", description = "JWT/사용자 id"),
                    @Parameter(name = "commentDeleteRequest", description = "댓글, 대댓글 삭제 요청 DTO")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "댓글 및 대댓글 삭제에 성공하면 200을 반환한다.",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @DeleteMapping("/comment")
    public ResponseEntity deleteComment(@Auth Long id, @RequestBody CommentDeleteRequest commentDeleteRequest) {
        commentService.deleteComment(id, commentDeleteRequest);
        return ResponseEntity.ok().build();
    }

    /*
    특정 게시글 댓글 조회
     */
    @Operation(summary = "특정 게시글 댓글 조회 api", description = "게시글 인덱스를 받아 해당 게시글의 댓글을 조회한다.")
    @Parameter(name = "postId", description = "게시글 인덱스")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "해당 게시글의 댓글을 댓글등록순으로 불러온다.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentResponse.class))))
            }
    )
    @GetMapping("/{postId}/comment/list")
    public ResponseEntity<List<CommentResponse>> getComment(@PathVariable Long postId) {
        List<CommentResponse> comments = commentService.getComment(postId);
        return ResponseEntity.ok(comments);
    }

    /*
    특정 게시글 대댓글 조회
     */
    @Operation(summary = "특정 게시글 대댓글 조회 api", description = "게시글 인덱스, 해당 댓글의 인덱스를 받아 대댓글을 조회한다.")
    @Parameters(
            value = {
                    @Parameter(name = "postId", description = "게시글 인덱스"),
                    @Parameter(name = "commentId", description = "댓글 인덱스")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "해당 댓글의 대댓글을 조죄한다.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentResponse.class))))
            }
    )
    @GetMapping("/{postId}/comment/{commentId}/list")
    public ResponseEntity getCommentReply(@PathVariable Long postId, @PathVariable Long commentId) {
        List<CommentResponse> commentReply = commentService.getCommentReply(postId, commentId);
        return ResponseEntity.ok(commentReply);
    }

    /*
    댓글 좋아요 기능
     */
    @Operation(summary = "댓글 좋아요 api", description = "특정 댓글 인덱스값을 받아 좋아요를 한다.")
    @Parameters(
            value = {
                    @Parameter(name = "id", description = "JWT/사용자 id"),
                    @Parameter(name = "commentLikeRequest", description = "댓글 좋아요 요청 DTO")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "좋아요 성공시 200을 반환한다.",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping("/comment/like")
    public ResponseEntity addLike(@Auth Long id, @RequestBody CommentLikeRequest commentLikeRequest){
        commentService.addLike(id, commentLikeRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 좋아요 취소 api", description = "특정 댓글 인덱스값을 받아 좋아요를 취소한다.")
    @Parameters(
            value = {
                    @Parameter(name = "id", description = "JWT/사용자 id"),
                    @Parameter(name = "commentLikeRequest", description = "댓글 좋아요 요청 DTO")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "좋아요 취소 성공시 200을 반환한다.",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @DeleteMapping("/comment/like")
    public ResponseEntity deleteLike(@Auth Long id, @RequestBody CommentLikeRequest commentLikeRequest){
        commentService.deleteLike(id,commentLikeRequest);
        return ResponseEntity.ok().build();
    }
}
