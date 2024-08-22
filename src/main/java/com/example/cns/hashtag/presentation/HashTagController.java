package com.example.cns.hashtag.presentation;


import com.example.cns.auth.config.Auth;
import com.example.cns.feed.post.dto.response.PostResponse;
import com.example.cns.hashtag.dto.response.HashTagSearchResponse;
import com.example.cns.hashtag.service.HashTagSearchService;
import com.example.cns.hashtag.service.HashTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "해시태그 API", description = "해시태그와 관련된 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HashTagController {

    private final HashTagService hashTagService;
    private final HashTagSearchService searchService;

    @Operation(summary = "해시태그 검색 api", description = "키워드를 입력받아 해시태그를 검색한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "키워드를 바탕으로 만들어진 해시태그를 반환한다.")
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/hashtag/{keyword}")
    public ResponseEntity<List<HashTagSearchResponse>> searchHashTag(@PathVariable String keyword) {
        List<HashTagSearchResponse> searchedHashTag = hashTagService.searchHashTag(keyword);
        return ResponseEntity.ok(searchedHashTag);
    }

    @Operation(summary = "해시태그를 통한 게시물 조회", description = "해시태그를 입력 받고 해당 해시태그를 태그한 게시물을 반환한다.")
    @Parameters({
            @Parameter(name = "hashtag", description = "검색할 해시태그", required = true),
            @Parameter(name = "page", description = "페이징번호, 디폴트값:1")
    })
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "게시물 리스트", content = @Content(schema = @Schema(implementation = PostResponse.class))),
                    @ApiResponse(responseCode = "204", description = "게시물이 존재하지 않는 경우"
                    )
            })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/hashtag/post")
    public ResponseEntity recommend(@Auth Long memberId, @RequestParam(name = "hashtag") String hashtag, @RequestParam(name = "page", defaultValue = "1") int page) {
        List<PostResponse> response = searchService.getRecommendPostByHashTag(hashtag, memberId, page);

        if (response.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(response);
    }
}
