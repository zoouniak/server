package com.example.cns.hashtag.presentation;


import com.example.cns.hashtag.dto.request.HashTagRequest;
import com.example.cns.hashtag.dto.response.HashTagSearchResponse;
import com.example.cns.hashtag.service.HashTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "해시태그 API", description = "해시태그와 관련된 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HashTagController {

    private final HashTagService hashTagService;

    @Operation(summary = "게시글의 해시태그 추가하는 api", description = "게시글이 등록이 되고 난 이후, 게시글의 번호를 통해 해시태그를 생성한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "해시태그 등록이 되면 코드 200을 반환한다.")
            }
    )
    @PostMapping("/hashtag")
    public ResponseEntity createHashTag(@RequestBody HashTagRequest hashTagRequest) {
        hashTagService.createHashTag(hashTagRequest);
        return ResponseEntity.ok().build();
    }

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
