package com.example.cns.hashtag.service;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.feed.post.dto.response.PostResponse;
import com.example.cns.hashtag.domain.repository.HashTagRepository;
import com.example.cns.hashtag.domain.repository.HashTagSearchRepository;
import com.example.cns.member.domain.repository.MemberRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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
     해시태그에 따른 게시물 추천
     */
    public List<PostResponse> getRecommendPostByHashTag(String hashtag, Long memberId, int page) {
        try {
            // 추천 시스템 서버에 api 요청
            HttpResponse response = getRecommends(makeUrl(hashtag, memberId, page));
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                String responseJson = EntityUtils.toString(response.getEntity());
                return objectMapper.readValue(responseJson, new TypeReference<>() {
                });
            } else {
                log.info("상태코드 = " + status);
                throw new BusinessException(FAIL_GET_API);
            }
        } catch (IOException ex) {
            throw new BusinessException(FAIL_GET_API);
        }
    }

    private String makeUrl(String hashtag, Long memberId, int page) {
        return api + "/" + hashtag + "/" + memberId + "/" + page + "/10";
    }

    private HttpResponse getRecommends(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        return httpClient.execute(httpGet);
    }
}
