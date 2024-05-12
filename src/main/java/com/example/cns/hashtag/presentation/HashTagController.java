package com.example.cns.hashtag.presentation;


import com.example.cns.hashtag.dto.response.HashTagSearchResponse;
import com.example.cns.hashtag.service.HashTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "해시태그 API", description = "해시태그와 관련된 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HashTagController {

    private final HashTagService hashTagService;

    @Operation(summary = "해시태그 검색 api", description = "키워드를 입력받아 해시태그를 검색한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "키워드를 바탕으로 만들어진 해시태그를 반환한다.")
            }
    )
    @GetMapping("/hashtag/{keyword}")
    public ResponseEntity<List<HashTagSearchResponse>> searchHashTag(@PathVariable String keyword) {
        List<HashTagSearchResponse> searchedHashTag = hashTagService.searchHashTag(keyword);
        return ResponseEntity.ok(searchedHashTag);
    }
}
