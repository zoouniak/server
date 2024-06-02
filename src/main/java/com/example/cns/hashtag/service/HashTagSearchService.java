package com.example.cns.hashtag.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.feed.post.dto.response.PostResponse;
import com.example.cns.hashtag.domain.HashTag;
import com.example.cns.hashtag.domain.repository.HashTagRepository;
import com.example.cns.hashtag.domain.repository.HashTagSearchRepository;
import com.example.cns.member.domain.repository.MemberRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.cns.common.exception.ExceptionCode.FAIL_GET_API;

@Service
@Slf4j
@RequiredArgsConstructor
public class HashTagSearchService {
    private final HashTagRepository hashTagRepository;
    private final HashTagSearchRepository searchRepository;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    @Value("${external-api.recommend-hashtag}")
    private String api;

  /*  public List<PostResponse> getAllPostsByHashTag(String hashtag, Long postId, Long memberId) {
        Optional<HashTag> optionalHashTag = hashTagRepository.findByName(hashtag);
        if (optionalHashTag.isEmpty()) {
            return Collections.emptyList();
        }
        Member member = memberRepository.findById(memberId).get();
        return searchRepository.getPostsByHashTag(optionalHashTag.get().getId(), member, postId, 10);
    }*/

    /*
     *  해시태그에 따른 게시물 추천
     */
    public List<PostResponse> getRecommendPostByHashTag(String hashtag, Long memberId, int page) {
        Optional<HashTag> entity = hashTagRepository.findByName(hashtag);
        if (entity.isEmpty()) return Collections.emptyList();

        // 추천 서버에 api 요청
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // URL 생성 후 HTTP GET 요청 생성
            HttpGet httpGet = new HttpGet(makeUrl(entity.get().getId(), memberId, page));
            // HTTP GET 요청을 실행하고 응답 받음 (try-with-resources -> 응답 자원 자동 해제)
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                // 응답의 상태 코드를 가져옴
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    String responseJson = EntityUtils.toString(response.getEntity());
                    // 문자열을 PostResponse 리스트로 변환하여 반환 (objectMapper 사용)
                    return objectMapper.readValue(responseJson, new TypeReference<>() {
                    });
                } else {
                    log.info("상태코드 = " + status);
                    throw new BusinessException(FAIL_GET_API);
                }
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new BusinessException(FAIL_GET_API);
        }
    }

    /*
     *  추천 서버에 요청할 url 생성
     */
    private String makeUrl(Long hashtagId, Long memberId, int page) {
        return api + "/" + hashtagId + "/" + memberId + "/" + page + "/10";
    }

}
