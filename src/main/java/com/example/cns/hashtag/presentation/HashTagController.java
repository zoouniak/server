package com.example.cns.hashtag.presentation;


import com.example.cns.hashtag.domain.HashTag;
import com.example.cns.hashtag.dto.request.HashTagRequest;
import com.example.cns.hashtag.service.HashTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HashTagController {

    private final HashTagService hashTagService;

    @PostMapping("/hashtag")
    public ResponseEntity createHashTag(@RequestBody HashTagRequest hashTagRequest){
        hashTagService.createHashTag(hashTagRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/hashtag/{keyword}")
    public ResponseEntity<List<HashTag>> searchHashTag(@PathVariable String keyword){
        List<HashTag> hashTags = hashTagService.searchHashTag(keyword);
        return ResponseEntity.ok(hashTags);
    }
}
